package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EventCategory
import com.example.model.SocialEvent
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * RADAR SONAR FÍSICO INTERATIVO DO PARTICIPANTE (Obsidian Proximity Radar)
 * 
 * Exibe a posição do usuário no centro e os eventos e encontros próximos
 * dispostos em anéis de alcance físico (150m, 500m, 1.5km, 3km).
 * Possui linha de varredura giratória contínua (Sonar), pulso biométrico no centro,
 * nós interativos clicáveis e chip de evento selecionado.
 */
@Composable
fun ParticipantSonarRadar(
    events: List<SocialEvent>,
    selectedEvent: SocialEvent?,
    onSelectEvent: (SocialEvent) -> Unit,
    onCheckInClick: (SocialEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    var sonarRangeKm by remember { mutableDoubleStateOf(3.0) }

    // Animação contínua da linha de varredura (Sweep do Sonar)
    val infiniteTransition = rememberInfiniteTransition(label = "sonar_sweep")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep_angle"
    )

    // Pulso do centro (User presence halo)
    val userPulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "user_pulse"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(24.dp), spotColor = Color(0x1F0B0C10)),
        shape = RoundedCornerShape(24.dp),
        color = ColorDarkObsidian,
        border = androidx.compose.foundation.BorderStroke(1.dp, ColorTeal.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header do Sonar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(ColorTeal, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "RADAR DE ENCONTROS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = ColorTeal,
                        letterSpacing = 0.5.sp
                    )
                }

                // Range selector pill
                Row(
                    modifier = Modifier
                        .background(Color(0xFF1B2828), RoundedCornerShape(9999.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(1.0 to "1km", 3.0 to "3km", 5.0 to "5km").forEach { (km, label) ->
                        val isSelected = sonarRangeKm == km
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(9999.dp))
                                .background(if (isSelected) ColorTeal else Color.Transparent)
                                .clickable { sonarRangeKm = km }
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else TextMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Canvas Central do Sonar
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0D1617)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val cx = w / 2f
                    val cy = h / 2f
                    val maxRadius = w * 0.44f

                    // 1. Grid Círculos Concêntricos de Distância
                    val ringFractions = listOf(0.25f, 0.50f, 0.75f, 1.0f)
                    ringFractions.forEach { frac ->
                        val r = maxRadius * frac
                        drawCircle(
                            color = ColorTeal.copy(alpha = 0.20f),
                            radius = r,
                            center = Offset(cx, cy),
                            style = Stroke(width = 1f)
                        )
                    }

                    // 2. Eixos Cruzados (N-S, L-O)
                    drawLine(
                        color = ColorTeal.copy(alpha = 0.15f),
                        start = Offset(cx, cy - maxRadius),
                        end = Offset(cx, cy + maxRadius),
                        strokeWidth = 1f
                    )
                    drawLine(
                        color = ColorTeal.copy(alpha = 0.15f),
                        start = Offset(cx - maxRadius, cy),
                        end = Offset(cx + maxRadius, cy),
                        strokeWidth = 1f
                    )

                    // 3. Linha de Varredura do Sonar (Sweep Gradient)
                    val sweepRad = Math.toRadians(sweepAngle.toDouble()).toFloat()
                    val sweepEndX = cx + maxRadius * cos(sweepRad)
                    val sweepEndY = cy + maxRadius * sin(sweepRad)

                    drawLine(
                        color = ColorTeal.copy(alpha = 0.85f),
                        start = Offset(cx, cy),
                        end = Offset(sweepEndX, sweepEndY),
                        strokeWidth = 2f,
                        cap = StrokeCap.Round
                    )

                    // 4. Feixe em leque translúcido atrás da linha
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(
                                Color.Transparent,
                                ColorTeal.copy(alpha = 0.05f),
                                ColorTeal.copy(alpha = 0.25f)
                            ),
                            center = Offset(cx, cy)
                        ),
                        startAngle = sweepAngle - 45f,
                        sweepAngle = 45f,
                        useCenter = true,
                        topLeft = Offset(cx - maxRadius, cy - maxRadius),
                        size = androidx.compose.ui.geometry.Size(maxRadius * 2, maxRadius * 2)
                    )

                    // 5. Halo e Ponto do Usuário ("Você")
                    drawCircle(
                        color = ColorTeal.copy(alpha = 0.25f * (1.5f - userPulse * 0.5f)),
                        radius = (w * 0.06f) * userPulse,
                        center = Offset(cx, cy)
                    )
                    drawCircle(
                        color = ColorTeal,
                        radius = w * 0.035f,
                        center = Offset(cx, cy)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = w * 0.015f,
                        center = Offset(cx, cy)
                    )
                }

                // 6. Nós dos Eventos no Radar posicionados por ângulo e distância simulada
                events.take(6).forEachIndexed { index, ev ->
                    // Cálculo de posição no sonar
                    val angles = listOf(35.0, 110.0, 200.0, 290.0, 75.0, 330.0)
                    val angleDeg = angles.getOrElse(index) { (index * 60.0) }
                    val angleRad = Math.toRadians(angleDeg)
                    val normalizedDist = (ev.distanceKm / sonarRangeKm).coerceIn(0.2, 0.95)
                    val radiusDp = (105 * normalizedDist).dp

                    val offsetX = (radiusDp.value * cos(angleRad)).dp
                    val offsetY = (radiusDp.value * sin(angleRad)).dp

                    val isSelected = selectedEvent?.id == ev.id
                    val isInsideGeofence = ev.distanceKm <= 0.8 // no raio

                    Box(
                        modifier = Modifier
                            .offset(x = offsetX, y = offsetY)
                            .size(if (isSelected) 34.dp else 26.dp)
                            .clip(CircleShape)
                            .background(
                                if (isInsideGeofence) ColorBurntOrange
                                else if (isSelected) ColorTeal
                                else Color(0xFF233535)
                            )
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) Color.White else ColorTealLight,
                                shape = CircleShape
                            )
                            .clickable { onSelectEvent(ev) },
                        contentAlignment = Alignment.Center
                    ) {
                        val categoryIcon = when (ev.category) {
                            EventCategory.TECNOLOGIA -> Icons.Default.Bolt
                            EventCategory.SHOW -> Icons.Default.MusicNote
                            EventCategory.BAR -> Icons.Default.LocalBar
                            EventCategory.MEETUP -> Icons.Default.Groups
                            EventCategory.PRIVADO -> Icons.Default.Shield
                            else -> Icons.Default.Place
                        }
                        Icon(
                            imageVector = categoryIcon,
                            contentDescription = ev.category.label,
                            tint = Color.White,
                            modifier = Modifier.size(if (isSelected) 14.dp else 11.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Legenda e Status da Presença
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(ColorBurntOrange, CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("A pé (< 500m)", fontSize = 11.sp, color = TextMuted)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(ColorTeal, CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Encontros Confirmados", fontSize = 11.sp, color = TextMuted)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(Color.White, CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Você Aqui", fontSize = 11.sp, color = TextMuted)
                }
            }

            // Preview rápido do evento focado no sonar
            selectedEvent?.let { ev ->
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF141F20),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ColorTeal.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (ev.distanceKm <= 0.8) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = ColorMustard,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "VOCÊ ESTÁ NO LOCAL!",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ColorMustard
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsWalk,
                                        contentDescription = null,
                                        tint = ColorTealLight,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "A ${ev.distanceKm}km de você",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ColorTealLight
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "• ${ev.timeDisplay}",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = ev.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1
                            )
                            Text(
                                text = ev.locationName,
                                fontSize = 11.5.sp,
                                color = TextMuted,
                                maxLines = 1
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        if (ev.distanceKm <= 0.8) {
                            // Ação de Check-in usando exclusivamente o ícone criado (sem botão de texto)
                            ObsidianCheckInIconTrigger(
                                onClick = { onCheckInClick(ev) },
                                size = 44.dp,
                                iconSize = 24.dp,
                                containerColor = ColorBurntOrange,
                                isPulsing = true,
                                contentDescription = "Fazer Check-in no Encontro"
                            )
                        } else {
                            Surface(
                                onClick = { onCheckInClick(ev) },
                                shape = CircleShape,
                                color = ColorTeal,
                                border = androidx.compose.foundation.BorderStroke(1.dp, ColorTealBorder),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                    Icon(
                                        imageVector = Icons.Default.Navigation,
                                        contentDescription = "Ver Rota",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
