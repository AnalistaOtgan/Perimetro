package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * 1. ÍCONE EXCLUSIVO: ESTAÇÃO DE CHECK-IN ("Portal & Beacon Criptográfico de Presença")
 * 
 * Substitui ícones genéricos de QR code ou celular por um totem de ancoragem presencial
 * Obsidian: pedestal facetado, prisma monolítico central, núcleo de beacon rotativo,
 * arcos de radar ressonante dinâmico e retículos de alinhamento óptico de segurança.
 */
@Composable
fun ObsidianStationPortalIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
    size: Dp = 20.dp,
    animated: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "station_radar")
    val waveAlpha by if (animated) {
        infiniteTransition.animateFloat(
            initialValue = 0.35f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "wave_alpha"
        )
    } else {
        remember { mutableFloatStateOf(0.85f) }
    }

    val waveOffset by if (animated) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1.8f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "wave_offset"
        )
    } else {
        remember { mutableFloatStateOf(0f) }
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height

            // 1. Alignment Reticle Brackets (4 cantos ópticos da estação)
            val cornerLen = w * 0.16f
            val cornerStroke = Stroke(width = (w * 0.055f).coerceAtLeast(1.2f), cap = StrokeCap.Round)
            val reticleColor = tint.copy(alpha = 0.50f)

            // Top-Left corner
            val tlPath = Path().apply {
                moveTo(w * 0.08f, h * 0.22f)
                lineTo(w * 0.08f, h * 0.08f)
                lineTo(w * 0.22f, h * 0.08f)
            }
            drawPath(tlPath, reticleColor, style = cornerStroke)

            // Top-Right corner
            val trPath = Path().apply {
                moveTo(w * 0.92f, h * 0.22f)
                lineTo(w * 0.92f, h * 0.08f)
                lineTo(w * 0.78f, h * 0.08f)
            }
            drawPath(trPath, reticleColor, style = cornerStroke)

            // Bottom-Left corner
            val blPath = Path().apply {
                moveTo(w * 0.08f, h * 0.78f)
                lineTo(w * 0.08f, h * 0.92f)
                lineTo(w * 0.22f, h * 0.92f)
            }
            drawPath(blPath, reticleColor, style = cornerStroke)

            // Bottom-Right corner
            val brPath = Path().apply {
                moveTo(w * 0.92f, h * 0.78f)
                lineTo(w * 0.92f, h * 0.92f)
                lineTo(w * 0.78f, h * 0.92f)
            }
            drawPath(brPath, reticleColor, style = cornerStroke)

            // 2. Base Pedestal Facetado da Estação
            val pedestalPath = Path().apply {
                moveTo(w * 0.22f, h * 0.88f)
                lineTo(w * 0.78f, h * 0.88f)
                lineTo(w * 0.68f, h * 0.74f)
                lineTo(w * 0.32f, h * 0.74f)
                close()
            }
            drawPath(pedestalPath, tint.copy(alpha = 0.85f))

            // 3. Monólito Prismático Central (Totem de Recepção)
            // Faceta Esquerda (Tom mais claro)
            val leftFacet = Path().apply {
                moveTo(w * 0.50f, h * 0.32f)
                lineTo(w * 0.36f, h * 0.44f)
                lineTo(w * 0.40f, h * 0.74f)
                lineTo(w * 0.50f, h * 0.72f)
                close()
            }
            drawPath(leftFacet, tint.copy(alpha = 0.95f))

            // Faceta Direita (Tom com ligeira sombra/facetagem 3D)
            val rightFacet = Path().apply {
                moveTo(w * 0.50f, h * 0.32f)
                lineTo(w * 0.64f, h * 0.44f)
                lineTo(w * 0.60f, h * 0.74f)
                lineTo(w * 0.50f, h * 0.72f)
                close()
            }
            drawPath(rightFacet, tint.copy(alpha = 0.75f))

            // Linha de crista central do cristal
            drawLine(
                color = Color.White.copy(alpha = 0.60f),
                start = Offset(w * 0.50f, h * 0.32f),
                end = Offset(w * 0.50f, h * 0.72f),
                strokeWidth = (w * 0.04f).coerceAtLeast(1f)
            )

            // 4. Núcleo Beacon Criptográfico (Diamante Flutuante no Topo)
            val beaconCore = Path().apply {
                moveTo(w * 0.50f, h * 0.16f)
                lineTo(w * 0.60f, h * 0.26f)
                lineTo(w * 0.50f, h * 0.36f)
                lineTo(w * 0.40f, h * 0.26f)
                close()
            }
            drawPath(beaconCore, tint)

            // Ponto central de pulso
            drawCircle(
                color = Color.White,
                radius = w * 0.045f,
                center = Offset(w * 0.50f, h * 0.26f)
            )

            // 5. Arcos de Radar Criptográfico (Ondas de Presença Expansivas)
            val radarStroke = Stroke(
                width = (w * 0.055f).coerceAtLeast(1.2f),
                cap = StrokeCap.Round
            )

            // Arco 1 (Inferior / próximo)
            drawArc(
                color = tint.copy(alpha = waveAlpha * 0.9f),
                startAngle = -140f,
                sweepAngle = 100f,
                useCenter = false,
                topLeft = Offset(w * 0.22f - waveOffset * 0.5f, h * 0.02f - waveOffset * 0.5f),
                size = androidx.compose.ui.geometry.Size(w * 0.56f + waveOffset, h * 0.48f + waveOffset),
                style = radarStroke
            )

            // Arco 2 (Superior / externo)
            drawArc(
                color = tint.copy(alpha = (1f - waveAlpha * 0.5f).coerceIn(0.2f, 0.8f)),
                startAngle = -148f,
                sweepAngle = 116f,
                useCenter = false,
                topLeft = Offset(w * 0.12f, -h * 0.06f),
                size = androidx.compose.ui.geometry.Size(w * 0.76f, h * 0.64f),
                style = Stroke(width = (w * 0.045f).coerceAtLeast(1f), cap = StrokeCap.Round)
            )
        }
    }
}

/**
 * 2. ÍCONE EXCLUSIVO: CURTIR ("Ressonância de Presença / Cristal Dual de Pulso")
 * 
 * Substitui o coração genérico por um cristal duplo interconectado que forma uma
 * silhueta de coração facetado, contendo a centelha do encontro presencial no centro
 * e arcos de ressonância áurica.
 */
@Composable
fun ObsidianResonanceIcon(
    isResonated: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
    tint: Color = Color.Unspecified
) {
    // Interactive transition for pulse pop
    val scaleAnim by animateFloatAsState(
        targetValue = if (isResonated) 1.15f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "resonate_pop"
    )

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                scaleX = scaleAnim
                scaleY = scaleAnim
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height

            if (isResonated) {
                // ESTADO ATIVO (Ressonando com Presença Real)
                // Gradiente vibrante: Burnt Orange -> Amber -> Teal
                val gradient = Brush.verticalGradient(
                    colors = listOf(
                        ColorBurntOrange,
                        ColorMustard,
                        ColorTeal
                    ),
                    startY = 0f,
                    endY = h
                )

                // 1. Arcos de Ressonância (Ondas orbitais de energia real)
                val auraStroke = Stroke(width = (w * 0.045f).coerceAtLeast(1f), cap = StrokeCap.Round)
                drawArc(
                    color = ColorBurntOrange.copy(alpha = 0.45f),
                    startAngle = 190f,
                    sweepAngle = 70f,
                    useCenter = false,
                    topLeft = Offset(-w * 0.05f, h * 0.05f),
                    size = androidx.compose.ui.geometry.Size(w * 0.50f, h * 0.50f),
                    style = auraStroke
                )
                drawArc(
                    color = ColorBurntOrange.copy(alpha = 0.45f),
                    startAngle = 280f,
                    sweepAngle = 70f,
                    useCenter = false,
                    topLeft = Offset(w * 0.55f, h * 0.05f),
                    size = androidx.compose.ui.geometry.Size(w * 0.50f, h * 0.50f),
                    style = auraStroke
                )

                // 2. Faceta Externa Esquerda do Cristal
                val leftOuterFacet = Path().apply {
                    moveTo(w * 0.50f, h * 0.28f)
                    lineTo(w * 0.28f, h * 0.12f)
                    lineTo(w * 0.12f, h * 0.26f)
                    lineTo(w * 0.10f, h * 0.48f)
                    lineTo(w * 0.32f, h * 0.68f)
                    lineTo(w * 0.50f, h * 0.88f)
                    lineTo(w * 0.38f, h * 0.48f)
                    close()
                }
                drawPath(leftOuterFacet, gradient)

                // Faceta Interna Esquerda (destaque)
                val leftInnerFacet = Path().apply {
                    moveTo(w * 0.50f, h * 0.28f)
                    lineTo(w * 0.28f, h * 0.12f)
                    lineTo(w * 0.38f, h * 0.48f)
                    lineTo(w * 0.50f, h * 0.88f)
                    close()
                }
                drawPath(leftInnerFacet, Color.White.copy(alpha = 0.25f))

                // 3. Faceta Externa Direita do Cristal
                val rightOuterFacet = Path().apply {
                    moveTo(w * 0.50f, h * 0.28f)
                    lineTo(w * 0.72f, h * 0.12f)
                    lineTo(w * 0.88f, h * 0.26f)
                    lineTo(w * 0.90f, h * 0.48f)
                    lineTo(w * 0.68f, h * 0.68f)
                    lineTo(w * 0.50f, h * 0.88f)
                    lineTo(w * 0.62f, h * 0.48f)
                    close()
                }
                drawPath(rightOuterFacet, gradient)

                // Faceta Interna Direita (sombra)
                val rightInnerFacet = Path().apply {
                    moveTo(w * 0.50f, h * 0.28f)
                    lineTo(w * 0.72f, h * 0.12f)
                    lineTo(w * 0.62f, h * 0.48f)
                    lineTo(w * 0.50f, h * 0.88f)
                    close()
                }
                drawPath(rightInnerFacet, Color.Black.copy(alpha = 0.15f))

                // 4. Crista de Luz Central (Seam brilhante)
                drawLine(
                    color = Color.White.copy(alpha = 0.85f),
                    start = Offset(w * 0.50f, h * 0.28f),
                    end = Offset(w * 0.50f, h * 0.88f),
                    strokeWidth = (w * 0.04f).coerceAtLeast(1f)
                )

                // 5. Centelha Central do Encontro (4-pointed Sparkle)
                val sparkPath = Path().apply {
                    val scx = w * 0.50f
                    val scy = h * 0.46f
                    val sR1 = w * 0.16f
                    val sR2 = w * 0.04f
                    val pts = 4
                    for (i in 0 until (pts * 2)) {
                        val r = if (i % 2 == 0) sR1 else sR2
                        val angle = (i * Math.PI / pts - Math.PI / 2).toFloat()
                        val px = scx + r * cos(angle)
                        val py = scy + r * sin(angle)
                        if (i == 0) moveTo(px, py) else lineTo(px, py)
                    }
                    close()
                }
                drawPath(sparkPath, Color.White)
                drawCircle(color = ColorMustard, radius = w * 0.05f, center = Offset(w * 0.50f, h * 0.46f))

            } else {
                // ESTADO INATIVO (Estrutura Criptográfica e Facetas em Contorno)
                val defaultLineColor = if (tint != Color.Unspecified) tint else TextMuted
                val lineStroke = Stroke(
                    width = (w * 0.06f).coerceAtLeast(1.3f),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
                val facetStroke = Stroke(
                    width = (w * 0.035f).coerceAtLeast(0.8f),
                    cap = StrokeCap.Round
                )

                // Contorno Crystalline Heart
                val heartOutline = Path().apply {
                    moveTo(w * 0.50f, h * 0.28f)
                    lineTo(w * 0.28f, h * 0.12f)
                    lineTo(w * 0.12f, h * 0.26f)
                    lineTo(w * 0.10f, h * 0.48f)
                    lineTo(w * 0.32f, h * 0.68f)
                    lineTo(w * 0.50f, h * 0.88f)
                    lineTo(w * 0.68f, h * 0.68f)
                    lineTo(w * 0.90f, h * 0.48f)
                    lineTo(w * 0.88f, h * 0.26f)
                    lineTo(w * 0.72f, h * 0.12f)
                    close()
                }
                drawPath(heartOutline, defaultLineColor, style = lineStroke)

                // Facetas Internas em Linhas Finas de Precisão
                drawLine(
                    color = defaultLineColor.copy(alpha = 0.50f),
                    start = Offset(w * 0.50f, h * 0.28f),
                    end = Offset(w * 0.50f, h * 0.88f),
                    strokeWidth = (w * 0.045f).coerceAtLeast(1f)
                )
                drawLine(
                    color = defaultLineColor.copy(alpha = 0.40f),
                    start = Offset(w * 0.28f, h * 0.12f),
                    end = Offset(w * 0.50f, h * 0.48f),
                    strokeWidth = (w * 0.035f).coerceAtLeast(0.8f)
                )
                drawLine(
                    color = defaultLineColor.copy(alpha = 0.40f),
                    start = Offset(w * 0.72f, h * 0.12f),
                    end = Offset(w * 0.50f, h * 0.48f),
                    strokeWidth = (w * 0.035f).coerceAtLeast(0.8f)
                )

                // Ponto de Centelha Central Suave
                drawCircle(
                    color = defaultLineColor.copy(alpha = 0.60f),
                    radius = w * 0.05f,
                    center = Offset(w * 0.50f, h * 0.48f)
                )
            }
        }
    }
}

/**
 * 3. ÍCONE EXCLUSIVO: AVALIAR ("Selo Prismático de Reputação Presencial")
 * 
 * Substitui a estrela amarela genérica por uma Estrela de Merkaba Criptográfica
 * de 8 pontas facetadas, com núcleo de gema lapidada, reflexos prismáticos
 * e facetamento geométrico que atesta reputação autêntica.
 */
@Composable
fun ObsidianPrismRatingIcon(
    isFilled: Boolean = true,
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
    tint: Color = Color.Unspecified
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val cx = w / 2f
            val cy = h / 2f

            val rCardinal = w * 0.46f   // 4 Pontas principais (Norte, Sul, Leste, Oeste)
            val rDiagonal = w * 0.31f   // 4 Pontas secundárias (NE, SE, SO, NO)
            val rCore = w * 0.16f       // Raio do octógono central

            if (isFilled) {
                // CORES PRISMÁTICAS DO SELO PREENCHIDO
                val goldHighlight = Color(0xFFFFF0B3)
                val goldBase = if (tint != Color.Unspecified) tint else ColorMustard
                val goldDark = Color(0xFFE29578) // Refração Burnt Orange sutil

                // Ângulos das 8 pontas (0=N, 1=NE, 2=E, 3=SE, 4=S, 5=SW, 6=W, 7=NW)
                for (p in 0 until 8) {
                    val isCardinal = (p % 2 == 0)
                    val rTip = if (isCardinal) rCardinal else rDiagonal
                    val angleTip = (p * Math.PI / 4.0 - Math.PI / 2.0).toFloat()
                    val tipX = cx + rTip * cos(angleTip)
                    val tipY = cy + rTip * sin(angleTip)

                    // Ângulo base esquerda e direita no núcleo
                    val angleL = (angleTip - Math.PI / 8.0).toFloat()
                    val angleR = (angleTip + Math.PI / 8.0).toFloat()
                    val baseLX = cx + rCore * cos(angleL)
                    val baseLY = cy + rCore * sin(angleL)
                    val baseRX = cx + rCore * cos(angleR)
                    val baseRY = cy + rCore * sin(angleR)

                    // Meia faceta iluminada (esquerda)
                    val facetL = Path().apply {
                        moveTo(cx, cy)
                        lineTo(baseLX, baseLY)
                        lineTo(tipX, tipY)
                        close()
                    }
                    drawPath(facetL, if (p % 2 == 0) goldHighlight else goldBase)

                    // Meia faceta sombreada (direita)
                    val facetR = Path().apply {
                        moveTo(cx, cy)
                        lineTo(tipX, tipY)
                        lineTo(baseRX, baseRY)
                        close()
                    }
                    drawPath(facetR, if (p % 2 == 0) goldBase else goldDark)
                }

                // Cristas e Vértices em Traço Cristalino
                for (p in 0 until 8) {
                    val rTip = if (p % 2 == 0) rCardinal else rDiagonal
                    val angleTip = (p * Math.PI / 4.0 - Math.PI / 2.0).toFloat()
                    drawLine(
                        color = Color.White.copy(alpha = 0.70f),
                        start = Offset(cx, cy),
                        end = Offset(cx + rTip * cos(angleTip), cy + rTip * sin(angleTip)),
                        strokeWidth = (w * 0.035f).coerceAtLeast(0.9f)
                    )
                }

                // Núcleo Octogonal Central (Gema de Integridade)
                val gemPath = Path().apply {
                    for (i in 0 until 8) {
                        val angle = (i * Math.PI / 4.0).toFloat()
                        val gx = cx + (rCore * 0.75f) * cos(angle)
                        val gy = cy + (rCore * 0.75f) * sin(angle)
                        if (i == 0) moveTo(gx, gy) else lineTo(gx, gy)
                    }
                    close()
                }
                drawPath(gemPath, Color.White)

                // Pupila de Diamante / Refração Teal
                drawCircle(
                    color = ColorTeal,
                    radius = w * 0.045f,
                    center = Offset(cx, cy)
                )

            } else {
                // ESTADO NÃO PREENCHIDO / OUTLINE CRISTALINO
                val outlineColor = if (tint != Color.Unspecified) tint else TextMuted
                val lineStroke = Stroke(
                    width = (w * 0.055f).coerceAtLeast(1.2f),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )

                // Contorno das 8 pontas conectadas
                val prismOutline = Path().apply {
                    for (p in 0 until 8) {
                        val isCardinal = (p % 2 == 0)
                        val rTip = if (isCardinal) rCardinal else rDiagonal
                        val angleTip = (p * Math.PI / 4.0 - Math.PI / 2.0).toFloat()
                        val tipX = cx + rTip * cos(angleTip)
                        val tipY = cy + rTip * sin(angleTip)

                        val valleyAngle = (angleTip + Math.PI / 8.0).toFloat()
                        val valleyX = cx + rCore * cos(valleyAngle)
                        val valleyY = cy + rCore * sin(valleyAngle)

                        if (p == 0) moveTo(tipX, tipY) else lineTo(tipX, tipY)
                        lineTo(valleyX, valleyY)
                    }
                    close()
                }
                drawPath(prismOutline, outlineColor, style = lineStroke)

                // Linhas radiais sutis de facetamento
                for (p in 0 until 8 step 2) {
                    val angleTip = (p * Math.PI / 4.0 - Math.PI / 2.0).toFloat()
                    drawLine(
                        color = outlineColor.copy(alpha = 0.40f),
                        start = Offset(cx, cy),
                        end = Offset(cx + rCardinal * cos(angleTip), cy + rCardinal * sin(angleTip)),
                        strokeWidth = (w * 0.035f).coerceAtLeast(0.8f)
                    )
                }

                drawCircle(
                    color = outlineColor.copy(alpha = 0.50f),
                    radius = w * 0.05f,
                    center = Offset(cx, cy)
                )
            }
        }
    }
}

/**
 * COMPONENTE BOTÃO INTERATIVO: CURTIR / RESSONÂNCIA DE PRESENÇA
 * Inclui feedback tátil visual, animação do cristal de ressonância e contagem de pulsos.
 */
@Composable
fun ObsidianResonateButton(
    isResonated: Boolean,
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(9999.dp),
        color = if (isResonated) ColorBurntOrangeLight else BgSurface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isResonated) ColorBurntOrangeBorder else BorderWarm
        ),
        modifier = modifier.height(38.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            ObsidianResonanceIcon(
                isResonated = isResonated,
                size = 18.dp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isResonated) "$count Pulsos" else "$count",
                fontSize = 12.sp,
                fontWeight = if (isResonated) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.SemiBold,
                color = if (isResonated) ColorBurntOrange else TextSecondary
            )
        }
    }
}

/**
 * COMPONENTE BOTÃO INTERATIVO: AVALIAR EXPERIÊNCIA
 * Exibe o Selo Prismático de Reputação criado para a plataforma Obsidian.
 */
@Composable
fun ObsidianRatingButton(
    rating: Double,
    ratingsCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ObsidianRatingIconTrigger(
        rating = rating,
        onClick = onClick,
        modifier = modifier
    )
}

/**
 * BARRA DE SELEÇÃO INTERATIVA COM SELOS PRISMÁTICOS (1 a 5)
 * Usada no modal de avaliação de experiência presencial.
 */
@Composable
fun ObsidianPrismRatingSelector(
    rating: Int,
    onRatingSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    itemSize: Dp = 38.dp
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            val isFilled = i <= rating
            IconButton(
                onClick = { onRatingSelected(i) },
                modifier = Modifier.size(itemSize + 8.dp)
            ) {
                ObsidianPrismRatingIcon(
                    isFilled = isFilled,
                    size = itemSize
                )
            }
        }
    }
}

/**
 * COMPONENTE BOTÃO ICÔNICO EXCLUSIVO DE CHECK-IN PRESENCIAL
 * 
 * Executa o Check-in de Presença FÍSICA usando puramente o ícone criado 
 * (ObsidianStationPortalIcon), sem depender de botões de texto ou layouts genéricos.
 * Possui halo pulsante, feedback tátil e design prismático em Terracota ou Teal.
 */
@Composable
fun ObsidianCheckInIconTrigger(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 46.dp,
    iconSize: Dp = 24.dp,
    containerColor: Color = ColorBurntOrange,
    contentColor: Color = Color.White,
    isPulsing: Boolean = true,
    contentDescription: String = "Fazer Check-in Presencial"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "checkin_trigger_pulse")
    val haloPulse by if (isPulsing) {
        infiniteTransition.animateFloat(
            initialValue = 0.95f,
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "halo_scale"
        )
    } else {
        remember { mutableFloatStateOf(1f) }
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Halo de pulso sutil se estiver em modo ativo
        if (isPulsing) {
            Box(
                modifier = Modifier
                    .size(size * haloPulse)
                    .background(containerColor.copy(alpha = 0.20f), androidx.compose.foundation.shape.CircleShape)
            )
        }

        // Botão de ação icônica
        Surface(
            onClick = onClick,
            shape = androidx.compose.foundation.shape.CircleShape,
            color = containerColor,
            shadowElevation = 4.dp,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.35f)),
            modifier = Modifier.size(size)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                ObsidianStationPortalIcon(
                    size = iconSize,
                    tint = contentColor,
                    animated = isPulsing
                )
            }
        }
    }
}

/**
 * DISPARADOR ICÔNICO EXCLUSIVO DE AVALIAÇÃO (Obsidian Rating Trigger)
 * 
 * Aciona a avaliação diretamente através do Selo Prismático Lapidado criado para a plataforma,
 * sem botões de texto.
 */
@Composable
fun ObsidianRatingIconTrigger(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 46.dp,
    iconSize: Dp = 24.dp,
    rating: Double? = null,
    containerColor: Color = ColorMustardLight,
    contentColor: Color = ColorMustardHover,
    isPulsing: Boolean = false,
    contentDescription: String = "Avaliar Experiência com Selo Prismático"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rating_trigger_pulse")
    val haloPulse by if (isPulsing) {
        infiniteTransition.animateFloat(
            initialValue = 0.95f,
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(1300, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "halo_rating_scale"
        )
    } else {
        remember { mutableFloatStateOf(1f) }
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        if (isPulsing) {
            Box(
                modifier = Modifier
                    .size(size * haloPulse)
                    .background(ColorMustard.copy(alpha = 0.22f), androidx.compose.foundation.shape.CircleShape)
            )
        }

        Surface(
            onClick = onClick,
            shape = androidx.compose.foundation.shape.CircleShape,
            color = containerColor,
            shadowElevation = 3.dp,
            border = androidx.compose.foundation.BorderStroke(1.2.dp, ColorMustardBorder),
            modifier = Modifier.size(size)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                ObsidianPrismRatingIcon(
                    isFilled = true,
                    size = iconSize,
                    tint = contentColor
                )
            }
        }
    }
}

/**
 * 4. ÍCONE EXCLUSIVO: EVENTOS ("Nexus de Eventos / Pavilhão de Confluência Cultural")
 * 
 * Substitui o ticket genérico de cinema por um Pavilhão Prismático de Confluência e Celebração Coletiva:
 * - Dois arcos facetados de anfiteatro arquitetônico em perspectiva isométrica.
 * - Monólito/prisma cênico focal no coração da estrutura com centelha de ativação.
 * - Fachos de luz/ressonância estelar projetando para o topo (palco e vibração ao vivo).
 * - Base semicircular de confluência da comunidade.
 * 
 * Suporta estado ativo/selecionado (com preenchimentos facetados luminosos, refração em gradiente
 * e pulso suave dos feixes de palco) e estado inativo/não selecionado (linhas de precisão arquitetônica).
 */
@Composable
fun ObsidianEventsNexusIcon(
    selected: Boolean = false,
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
    size: Dp = 24.dp,
    animated: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "events_nexus_anim")
    val flarePulse by if (selected && animated) {
        infiniteTransition.animateFloat(
            initialValue = 0.5f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(1400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "events_flare_pulse"
        )
    } else {
        remember { mutableFloatStateOf(0.75f) }
    }

    val beamScale by if (selected && animated) {
        infiniteTransition.animateFloat(
            initialValue = 0.9f,
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "events_beam_scale"
        )
    } else {
        remember { mutableFloatStateOf(1.0f) }
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height

            val primaryColor = tint
            val accentColor = ColorBurntOrange

            if (selected) {
                // ==========================================
                // ESTADO ATIVO / SELECIONADO (NEXUS ILUMINADO)
                // ==========================================

                // 1. Feixes Cênicos Superiores (Spotlights / Vibração do Palco)
                val beamColor = primaryColor.copy(alpha = flarePulse * 0.9f)

                // Feixe Central Vertical
                drawLine(
                    color = beamColor,
                    start = Offset(w * 0.50f, h * 0.32f),
                    end = Offset(w * 0.50f, h * 0.10f * beamScale),
                    strokeWidth = (w * 0.07f).coerceAtLeast(1.5f),
                    cap = StrokeCap.Round
                )

                // Feixe Esquerdo Diagonal
                drawLine(
                    color = beamColor.copy(alpha = flarePulse * 0.7f),
                    start = Offset(w * 0.40f, h * 0.35f),
                    end = Offset(w * 0.22f, h * 0.16f),
                    strokeWidth = (w * 0.055f).coerceAtLeast(1.2f),
                    cap = StrokeCap.Round
                )

                // Feixe Direito Diagonal
                drawLine(
                    color = beamColor.copy(alpha = flarePulse * 0.7f),
                    start = Offset(w * 0.60f, h * 0.35f),
                    end = Offset(w * 0.78f, h * 0.16f),
                    strokeWidth = (w * 0.055f).coerceAtLeast(1.2f),
                    cap = StrokeCap.Round
                )

                // 2. Asa do Pavilhão Esquerdo (Anfiteatro Facetado)
                val leftWingOuter = Path().apply {
                    moveTo(w * 0.50f, h * 0.36f)
                    lineTo(w * 0.22f, h * 0.28f)
                    lineTo(w * 0.10f, h * 0.54f)
                    lineTo(w * 0.24f, h * 0.82f)
                    lineTo(w * 0.50f, h * 0.70f)
                    lineTo(w * 0.36f, h * 0.52f)
                    close()
                }
                drawPath(
                    path = leftWingOuter,
                    brush = Brush.verticalGradient(
                        colors = listOf(primaryColor, primaryColor.copy(alpha = 0.85f)),
                        startY = h * 0.28f,
                        endY = h * 0.82f
                    )
                )

                // Faceta interna de contraste na asa esquerda
                val leftWingFacet = Path().apply {
                    moveTo(w * 0.22f, h * 0.28f)
                    lineTo(w * 0.36f, h * 0.52f)
                    lineTo(w * 0.24f, h * 0.82f)
                    lineTo(w * 0.10f, h * 0.54f)
                    close()
                }
                drawPath(leftWingFacet, Color.Black.copy(alpha = 0.18f))

                // 3. Asa do Pavilhão Direito (Anfiteatro Facetado)
                val rightWingOuter = Path().apply {
                    moveTo(w * 0.50f, h * 0.36f)
                    lineTo(w * 0.78f, h * 0.28f)
                    lineTo(w * 0.90f, h * 0.54f)
                    lineTo(w * 0.76f, h * 0.82f)
                    lineTo(w * 0.50f, h * 0.70f)
                    lineTo(w * 0.64f, h * 0.52f)
                    close()
                }
                drawPath(
                    path = rightWingOuter,
                    brush = Brush.verticalGradient(
                        colors = listOf(primaryColor.copy(alpha = 0.95f), primaryColor.copy(alpha = 0.75f)),
                        startY = h * 0.28f,
                        endY = h * 0.82f
                    )
                )

                // Faceta interna de contraste na asa direita (iluminada)
                val rightWingFacet = Path().apply {
                    moveTo(w * 0.78f, h * 0.28f)
                    lineTo(w * 0.64f, h * 0.52f)
                    lineTo(w * 0.76f, h * 0.82f)
                    lineTo(w * 0.90f, h * 0.54f)
                    close()
                }
                drawPath(rightWingFacet, Color.White.copy(alpha = 0.22f))

                // 4. Prisma Central Focal (O Palco / Hearth do Evento)
                val stagePrism = Path().apply {
                    moveTo(w * 0.50f, h * 0.34f)
                    lineTo(w * 0.62f, h * 0.52f)
                    lineTo(w * 0.50f, h * 0.72f)
                    lineTo(w * 0.38f, h * 0.52f)
                    close()
                }
                drawPath(stagePrism, Color.White)

                // Meia sombra do prisma central
                val stagePrismHalf = Path().apply {
                    moveTo(w * 0.50f, h * 0.34f)
                    lineTo(w * 0.62f, h * 0.52f)
                    lineTo(w * 0.50f, h * 0.72f)
                    close()
                }
                drawPath(stagePrismHalf, Color(0xFFE0E0E0))

                // Centelha de Energia do Encontro no Coração do Prisma
                drawCircle(
                    color = accentColor,
                    radius = w * 0.055f,
                    center = Offset(w * 0.50f, h * 0.52f)
                )

                // 5. Arco Inferior de Confluência da Tribo (Platéia / Base de Conexão)
                val baseArc = Path().apply {
                    moveTo(w * 0.18f, h * 0.88f)
                    quadraticTo(
                        w * 0.50f, h * 0.98f,
                        w * 0.82f, h * 0.88f
                    )
                }
                drawPath(
                    path = baseArc,
                    color = primaryColor,
                    style = Stroke(
                        width = (w * 0.07f).coerceAtLeast(1.5f),
                        cap = StrokeCap.Round
                    )
                )

                // Detalhe de luz central na base
                drawCircle(
                    color = Color.White,
                    radius = w * 0.04f,
                    center = Offset(w * 0.50f, h * 0.93f)
                )

            } else {
                // ==========================================
                // ESTADO INATIVO / WIREFRAME ARQUITETÔNICO
                // ==========================================
                val outlineColor = tint
                val mainStroke = Stroke(
                    width = (w * 0.06f).coerceAtLeast(1.3f),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
                val detailStroke = Stroke(
                    width = (w * 0.038f).coerceAtLeast(0.9f),
                    cap = StrokeCap.Round
                )

                // 1. Feixe de Luz Central Sutil
                drawLine(
                    color = outlineColor.copy(alpha = 0.6f),
                    start = Offset(w * 0.50f, h * 0.32f),
                    end = Offset(w * 0.50f, h * 0.12f),
                    strokeWidth = (w * 0.055f).coerceAtLeast(1.2f),
                    cap = StrokeCap.Round
                )

                // 2. Contorno do Pavilhão de Eventos (Asas Esquerda e Direita Integradas)
                val pavilionOutline = Path().apply {
                    moveTo(w * 0.50f, h * 0.36f)
                    lineTo(w * 0.22f, h * 0.28f)
                    lineTo(w * 0.10f, h * 0.54f)
                    lineTo(w * 0.24f, h * 0.82f)
                    lineTo(w * 0.50f, h * 0.70f)
                    lineTo(w * 0.76f, h * 0.82f)
                    lineTo(w * 0.90f, h * 0.54f)
                    lineTo(w * 0.78f, h * 0.28f)
                    close()
                }
                drawPath(pavilionOutline, color = outlineColor, style = mainStroke)

                // 3. Prisma Central (Palco) em Linhas
                val stagePrismOutline = Path().apply {
                    moveTo(w * 0.50f, h * 0.38f)
                    lineTo(w * 0.60f, h * 0.52f)
                    lineTo(w * 0.50f, h * 0.68f)
                    lineTo(w * 0.40f, h * 0.52f)
                    close()
                }
                drawPath(stagePrismOutline, color = outlineColor.copy(alpha = 0.8f), style = detailStroke)

                // Vértices do palco
                drawLine(
                    color = outlineColor.copy(alpha = 0.6f),
                    start = Offset(w * 0.50f, h * 0.38f),
                    end = Offset(w * 0.50f, h * 0.68f),
                    strokeWidth = (w * 0.035f).coerceAtLeast(0.8f)
                )

                // Ponto Focal Central
                drawCircle(
                    color = outlineColor.copy(alpha = 0.75f),
                    radius = w * 0.045f,
                    center = Offset(w * 0.50f, h * 0.52f)
                )

                // 4. Arco Inferior de Confluência
                val baseArc = Path().apply {
                    moveTo(w * 0.20f, h * 0.88f)
                    quadraticTo(
                        w * 0.50f, h * 0.96f,
                        w * 0.80f, h * 0.88f
                    )
                }
                drawPath(baseArc, color = outlineColor.copy(alpha = 0.65f), style = detailStroke)
            }
        }
    }
}


