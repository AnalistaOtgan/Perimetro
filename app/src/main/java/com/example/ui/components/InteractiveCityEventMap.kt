package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.location.UserLocation
import com.example.model.EventCategory
import com.example.model.SocialEvent
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// Coordenadas centrais de referência (São Paulo - Região Paulista / Jardins)
const val USER_DEFAULT_LAT = -23.5615
const val USER_DEFAULT_LON = -46.6560

// Fator de conversão de coordenadas geográficas para pixels no mapa base (escala urbana SP)
const val GEO_SCALE_FACTOR = 16000.0

/**
 * Motor de Mapa Urbano Interativo em Tela Cheia (estilo Uber / 99 Pop para Encontros)
 *
 * Suporta:
 * - Gestos de arrastar (pan) e pinça (pinch-to-zoom).
 * - Motor nativo de renderização de mapa mundi em estilo pontilhado (dot matrix).
 * - O mapa mantém a proporção e densidade dos pontos ao dar zoom, revelando mais do mapa real.
 * - Suporta gestos de pan e pinch-to-zoom suaves.
 * - Pins interativos flutuando sobre a grade global.
 */
@Composable
fun InteractiveCityEventMap(
    events: List<SocialEvent>,
    selectedEvent: SocialEvent?,
    onSelectEvent: (SocialEvent) -> Unit,
    modifier: Modifier = Modifier,
    userLocation: UserLocation? = null,
    isRouteActive: Boolean = false,
    onRecenterRequested: Boolean = false,
    onResetRecenter: () -> Unit = {}
) {
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()

    val currentLat = userLocation?.latitude ?: USER_DEFAULT_LAT
    val currentLon = userLocation?.longitude ?: USER_DEFAULT_LON

    // Estado de navegação e câmera do mapa
    var panX by remember { mutableFloatStateOf(0f) }
    var panY by remember { mutableFloatStateOf(0f) }
    var zoomScale by remember { mutableFloatStateOf(1.2f) }

    // Pulso contínuo do GPS do usuário
    val infiniteTransition = rememberInfiniteTransition(label = "map_animations")
    val gpsPulseProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gps_pulse"
    )

    // Efeito de fluxo na rota (pontos viajando pela linha estilo app de transporte)
    val routeDashOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 60f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "route_dash"
    )

    // Recentralizar na posição do usuário quando solicitado
    LaunchedEffect(onRecenterRequested) {
        if (onRecenterRequested) {
            coroutineScope.launch {
                val animPanX = Animatable(panX)
                val animPanY = Animatable(panY)
                val animZoom = Animatable(zoomScale)
                launch { animPanX.animateTo(0f, tween(500, easing = FastOutSlowInEasing)) }
                launch { animPanY.animateTo(0f, tween(500, easing = FastOutSlowInEasing)) }
                launch { animZoom.animateTo(1.25f, tween(500, easing = FastOutSlowInEasing)) }
                panX = 0f
                panY = 0f
                zoomScale = 1.25f
                onResetRecenter()
            }
        }
    }

    // Centralizar no evento selecionado quando houver mudança externa (ex: swipe no carrossel)
    LaunchedEffect(selectedEvent?.id) {
        selectedEvent?.let { ev ->
            val deltaX = ((ev.longitude - currentLon) * GEO_SCALE_FACTOR).toFloat()
            val deltaY = ((currentLat - ev.latitude) * GEO_SCALE_FACTOR).toFloat()
            // Ajustar o pan para centralizar o evento (com ligeiro deslocamento para cima para dar espaço ao bottom sheet)
            val targetPanX = -deltaX * zoomScale
            val targetPanY = -deltaY * zoomScale - 80f

            coroutineScope.launch {
                val animX = Animatable(panX)
                val animY = Animatable(panY)
                launch { animX.animateTo(targetPanX, tween(450, easing = FastOutSlowInEasing)) }
                launch { animY.animateTo(targetPanY, tween(450, easing = FastOutSlowInEasing)) }
                panX = targetPanX
                panY = targetPanY
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D1415))
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    zoomScale = (zoomScale * zoom).coerceIn(0.65f, 2.8f)
                    // Pan com limites suaves da região metropolitana
                    val maxPan = 1400f * zoomScale
                    panX = (panX + pan.x).coerceIn(-maxPan, maxPan)
                    panY = (panY + pan.y).coerceIn(-maxPan, maxPan)
                }
            }
            .testTag("interactive_city_map")
    ) {
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }
        val centerX = widthPx / 2f + panX
        val centerY = heightPx / 2f + panY

        // Posição na tela do usuário (centro de referência)
        val userScreenX = centerX
        val userScreenY = centerY

        // Posição na tela do evento selecionado (para rota)
        val selectedEventScreenPos = selectedEvent?.let { ev ->
            val evRelX = ((ev.longitude - currentLon) * GEO_SCALE_FACTOR).toFloat()
            val evRelY = ((currentLat - ev.latitude) * GEO_SCALE_FACTOR).toFloat()
            Offset(
                x = centerX + evRelX * zoomScale,
                y = centerY + evRelY * zoomScale
            )
        }

        // 1. RENDERIZAÇÃO DO MAPA MUNDI PONTILHADO (DOT MATRIX) NO CANVAS
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawDottedWorldMap(panX, panY, zoomScale)

            // E. Anéis de Alcance (opcional - mantido para contexto de proximidade)
            drawProximityRadiusRings(centerX, centerY, zoomScale)

            // F. Rota Dinâmica estilo Uber (se ativada ou evento selecionado)
            if (selectedEventScreenPos != null && (isRouteActive || selectedEvent != null)) {
                drawUberStyleRoute(
                    start = Offset(userScreenX, userScreenY),
                    end = selectedEventScreenPos,
                    dashOffset = routeDashOffset,
                    isRouteActive = isRouteActive
                )
            }

            // G. Beacon de Localização do Usuário (GPS com Halo pulsante, precisão e bússola)
            drawUserGpsBeacon(
                x = userScreenX,
                y = userScreenY,
                pulseProgress = gpsPulseProgress,
                bearing = userLocation?.bearing,
                accuracyMeters = userLocation?.accuracyMeters,
                zoomScale = zoomScale
            )
        }

        // 2. CAMADA DE MARCADORES (PINS) INTERATIVOS DOS ENCONTROS
        events.forEach { event ->
            val evRelX = ((event.longitude - currentLon) * GEO_SCALE_FACTOR).toFloat()
            val evRelY = ((currentLat - event.latitude) * GEO_SCALE_FACTOR).toFloat()
            val screenX = centerX + evRelX * zoomScale
            val screenY = centerY + evRelY * zoomScale

            val isSelected = event.id == selectedEvent?.id
            val isNearGeofence = event.distanceKm <= 0.8

            // Renderizar o Pin apenas se estiver dentro ou próximo dos limites da tela
            if (screenX >= -120f && screenX <= widthPx + 120f && screenY >= -120f && screenY <= heightPx + 120f) {
                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                x = (screenX - 40.dp.toPx()).toInt(),
                                y = (screenY - 60.dp.toPx()).toInt()
                            )
                        }
                        .testTag("map_pin_${event.id}")
                ) {
                    EventMapPinMarker(
                        event = event,
                        isSelected = isSelected,
                        isNearGeofence = isNearGeofence,
                        onClick = { onSelectEvent(event) }
                    )
                }
            }
        }

        // 3. ETIQUETA FLUTUANTE DE ROTA / TEMPO ESTIMADO (ESTILO UBER)
        if (isRouteActive && selectedEvent != null && selectedEventScreenPos != null) {
            val midX = (userScreenX + selectedEventScreenPos.x) / 2f
            val midY = (userScreenY + selectedEventScreenPos.y) / 2f

            Surface(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = (midX - 70.dp.toPx()).toInt(),
                            y = (midY - 24.dp.toPx()).toInt()
                        )
                    }
                    .shadow(8.dp, RoundedCornerShape(9999.dp), spotColor = ColorTeal),
                shape = RoundedCornerShape(9999.dp),
                color = ColorDarkObsidian,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, ColorTeal)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsWalk,
                        contentDescription = null,
                        tint = ColorTeal,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${(selectedEvent.distanceKm * 12).toInt().coerceAtLeast(3)} min • ${selectedEvent.distanceKm} km",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Marcador (Pin) Interativo do Encontro no Mapa
 */
@Composable
fun EventMapPinMarker(
    event: SocialEvent,
    isSelected: Boolean,
    isNearGeofence: Boolean,
    onClick: () -> Unit
) {
    val categoryColor = when (event.category) {
        EventCategory.TECNOLOGIA -> ColorTeal
        EventCategory.MEETUP -> Color(0xFF4A90E2)
        EventCategory.SHOW -> ColorBurntOrange
        EventCategory.BAR -> ColorMustard
        EventCategory.ESPACO -> Color(0xFF9C27B0)
        EventCategory.PAREDAO -> Color(0xFFFF5722)
        EventCategory.PRIVADO -> Color(0xFFFFD700)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        // Tag superior do Pin com Attendees / Avaliação ou "Ao Vivo"
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isSelected) ColorDarkObsidian else Color(0xEE162224),
            border = androidx.compose.foundation.BorderStroke(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) categoryColor else Color(0x44FFFFFF)
            ),
            modifier = Modifier.shadow(if (isSelected) 8.dp else 3.dp, RoundedCornerShape(8.dp))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (isNearGeofence) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(ColorBurntOrange, CircleShape)
                    )
                }
                if (isNearGeofence) {
                    Text(
                        text = "Perto!",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorBurntOrangeLight
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = ColorMustard,
                            modifier = Modifier.size(10.dp)
                        )
                        Text(
                            text = "%.1f".format(event.ratingAvg),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                Text(text = "•", fontSize = 10.sp, color = TextMuted)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(10.dp)
                    )
                    Text(
                        text = "${event.attendeesCount}",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(3.dp))

        // Corpo Principal do Pin (Ícone & Cor)
        Box(
            modifier = Modifier
                .size(if (isSelected) 42.dp else 34.dp)
                .shadow(
                    elevation = if (isSelected) 12.dp else 4.dp,
                    shape = CircleShape,
                    spotColor = categoryColor
                )
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            categoryColor,
                            Color(0xFF0F1819)
                        )
                    ),
                    shape = CircleShape
                )
                .border(
                    width = if (isSelected) 2.5.dp else 1.5.dp,
                    color = if (isSelected) Color.White else categoryColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when (event.category) {
                    EventCategory.TECNOLOGIA -> Icons.Default.Code
                    EventCategory.MEETUP -> Icons.Default.Groups
                    EventCategory.SHOW -> Icons.Default.MusicNote
                    EventCategory.BAR -> Icons.Default.LocalBar
                    EventCategory.ESPACO -> Icons.Default.Place
                    EventCategory.PAREDAO -> Icons.Default.Speaker
                    EventCategory.PRIVADO -> Icons.Default.Lock
                },
                contentDescription = event.title,
                tint = Color.White,
                modifier = Modifier.size(if (isSelected) 22.dp else 17.dp)
            )
        }

        // Ponta inferior do Pin (Teardrop pointer)
        Canvas(modifier = Modifier.size(8.dp, 6.dp)) {
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width / 2f, size.height)
                close()
            }
            drawPath(path, color = if (isSelected) Color.White else categoryColor)
        }
    }
}

// -----------------------------------------------------------------------------------------
// FUNÇÕES DE DESENHO DO MAPA MUNDI PONTILHADO
// -----------------------------------------------------------------------------------------

private val WORLD_MAP_MATRIX = listOf(
    "                                                                                ",
    "                                                                                ",
    "                                                                                ",
    "                   xxxxxx                                                       ",
    "                xxxxxxxxxxxxx            xxxxxx                                 ",
    "               xxxxxxxxxxxxxxx          xxxxxxxxxxx                             ",
    "              xxxxxxxxxxxxxxxxx       xxxxxxxxxxxxxxx                           ",
    "              xxxxxxxxxxxxxxxxxx     xxxxxxxxxxxxxxxxxx      xxxxxx             ",
    "               xxxxxxxxxxxxxxxxxx   xxxxxxxxxxxxxxxxxxxx    xxxxxxxxx           ",
    "                xxxxxxxxxxxxxxxxx   xxxxxxxxxxxxxxxxxxxxx   xxxxxxxxx           ",
    "                 xxxxxxxxxxxxxxxx   xxxxxxxxxxxxxxxxxxxxx   xxxxxxxxx           ",
    "                  xxxxxxxxxxxxxxx   xxxxxxxxxxxxxxxxxxxx     xxxxxxx            ",
    "                   xxxxxxxxxxxx      xxxxxxxxxxxxxxxxxxx      xxxxx             ",
    "                    xxxxxxxxx         xxxxxxxxxxxxxxxxx        xxx              ",
    "                     xxxxxxx           xxxxxxxxxxxxxxx          x               ",
    "                      xxxxxx           xxxxxxxxxxxxxxx                          ",
    "                       xxxxx             xxxxxxxxxxx                            ",
    "                        xxxx              xxxxxxxxx                  xxx        ",
    "                         xxx               xxxxxxx                  xxxxx       ",
    "                          xx               xxxxxxx                   xxxx       ",
    "                                            xxxxxx                    xx        ",
    "                                             xxxx                               ",
    "                                             xxxx                               ",
    "                                              xx                                ",
    "                                                                                ",
    "                                                                                ",
    "                                                                                "
)

/**
 * Desenha a grade de pontos que forma o mapa mundi.
 * Ao dar pan e zoom, iteramos na tela e descobrimos qual ponto da matriz desenhar,
 * mantendo o tamanho do ponto constante na tela.
 */
private fun DrawScope.drawDottedWorldMap(panX: Float, panY: Float, zoom: Float) {
    val dotColor = Color(0xFF162224)
    val dotRadius = 2.dp.toPx()
    val dotSpacing = 12.dp.toPx()
    
    val mapCols = WORLD_MAP_MATRIX[0].length
    val mapRows = WORLD_MAP_MATRIX.size
    
    // Virtual width of the map based on current zoom
    val virtualMapWidth = mapCols * dotSpacing * zoom * 0.8f
    val virtualMapHeight = mapRows * dotSpacing * zoom * 0.8f
    
    // Centralizar o mapa na tela inicial
    val startX = (size.width - virtualMapWidth) / 2f + panX
    val startY = (size.height - virtualMapHeight) / 2f + panY

    // Desenhamos apenas uma grade de pontos no espaço da tela
    var screenY = 0f
    while (screenY < size.height) {
        var screenX = 0f
        while (screenX < size.width) {
            // Mapeia de (screenX, screenY) para a matriz do mapa
            val mapX = ((screenX - startX) / (dotSpacing * zoom * 0.8f)).toInt()
            val mapY = ((screenY - startY) / (dotSpacing * zoom * 0.8f)).toInt()
            
            // Se estiver dentro da matriz, verificamos se é terra ('x')
            if (mapY in 0 until mapRows && mapX in 0 until mapCols) {
                if (WORLD_MAP_MATRIX[mapY][mapX] == 'x') {
                    // Desenha o ponto (tamanho constante)
                    drawCircle(
                        color = dotColor,
                        radius = dotRadius,
                        center = Offset(screenX, screenY)
                    )
                }
            }
            screenX += dotSpacing
        }
        screenY += dotSpacing
    }
}



/**
 * Anéis concêntricos de radar de alcance físico (500m, 1.5km, 3km)
 */
private fun DrawScope.drawProximityRadiusRings(cx: Float, cy: Float, zoom: Float) {
    val ringColor = ColorTeal.copy(alpha = 0.18f)
    val dashedEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)

    listOf(
        120f * zoom to "500m",
        240f * zoom to "1.5km",
        400f * zoom to "3.0km"
    ).forEach { (radius, _) ->
        drawCircle(
            color = ringColor,
            center = Offset(cx, cy),
            radius = radius,
            style = Stroke(width = 1.2f, pathEffect = dashedEffect)
        )
    }
}

/**
 * Desenha a Rota estilo Uber / 99 Pop conectando o usuário ao evento
 */
private fun DrawScope.drawUberStyleRoute(
    start: Offset,
    end: Offset,
    dashOffset: Float,
    isRouteActive: Boolean
) {
    // Caminho em L ou curva suave acompanhando a malha viária
    val midX = (start.x + end.x) / 2f
    val path = Path().apply {
        moveTo(start.x, start.y)
        // Manobra urbana em curva suave (bezier cúbica)
        cubicTo(
            midX, start.y,
            midX, end.y,
            end.x, end.y
        )
    }

    // 1. Sombra / Brilho Neon de Fundo da Rota
    drawPath(
        path = path,
        color = ColorTeal.copy(alpha = 0.35f),
        style = Stroke(width = 10f, cap = StrokeCap.Round)
    )

    // 2. Linha Principal Sólida
    drawPath(
        path = path,
        brush = Brush.linearGradient(
            colors = listOf(ColorTeal, ColorBurntOrange),
            start = start,
            end = end
        ),
        style = Stroke(width = 5f, cap = StrokeCap.Round)
    )

    // 3. Efeito de Pulsos / Traços em Movimento (Animação de deslocamento em tempo real)
    if (isRouteActive) {
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 14f), dashOffset)
        drawPath(
            path = path,
            color = Color.White.copy(alpha = 0.85f),
            style = Stroke(width = 3.5f, cap = StrokeCap.Round, pathEffect = dashEffect)
        )
    }
}

/**
 * Beacon de Localização do Usuário (GPS) com Pulso Halo, Círculo de Precisão e Bússola
 */
private fun DrawScope.drawUserGpsBeacon(
    x: Float,
    y: Float,
    pulseProgress: Float,
    bearing: Float? = null,
    accuracyMeters: Float? = null,
    zoomScale: Float = 1.0f
) {
    val center = Offset(x, y)

    // 0. Círculo de Acurácia do GPS (área de incerteza em escala métrica proporcional)
    accuracyMeters?.let { accMeters ->
        val pixelRadius = (accMeters * (GEO_SCALE_FACTOR / 111320.0) * zoomScale)
            .toFloat()
            .coerceIn(16f, 220f)

        drawCircle(
            color = ColorTeal.copy(alpha = 0.08f),
            center = center,
            radius = pixelRadius
        )
        drawCircle(
            color = ColorTeal.copy(alpha = 0.35f),
            center = center,
            radius = pixelRadius,
            style = Stroke(
                width = 1.5f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
            )
        )
    }

    // 1. Halo Pulsante Expansivo (Radar de Presença Ativa)
    val maxPulseRadius = 46f
    val currentPulseRadius = 14f + (maxPulseRadius - 14f) * pulseProgress
    val pulseAlpha = (1f - pulseProgress) * 0.55f

    drawCircle(
        color = ColorTeal.copy(alpha = pulseAlpha),
        center = center,
        radius = currentPulseRadius
    )

    // 2. Cone Direcional (Indicador de Orientação / Heading real do GPS ou Sensores)
    val headingAngle = bearing?.toDouble()?.minus(90.0) ?: -45.0
    val headingRad = Math.toRadians(headingAngle)
    val coneDistance = 24f
    val coneSpread = Math.toRadians(28.0)

    val conePath = Path().apply {
        moveTo(center.x, center.y)
        lineTo(
            (center.x + coneDistance * cos(headingRad - coneSpread)).toFloat(),
            (center.y + coneDistance * sin(headingRad - coneSpread)).toFloat()
        )
        lineTo(
            (center.x + coneDistance * cos(headingRad + coneSpread)).toFloat(),
            (center.y + coneDistance * sin(headingRad + coneSpread)).toFloat()
        )
        close()
    }
    drawPath(
        path = conePath,
        color = ColorTeal.copy(alpha = 0.45f)
    )

    // 3. Círculo Externo Branco de Contraste
    drawCircle(
        color = Color.White,
        center = center,
        radius = 10f
    )

    // 4. Núcleo Central Ciano / Teal (Dot do Usuário)
    drawCircle(
        color = ColorTeal,
        center = center,
        radius = 7.5f
    )
}
