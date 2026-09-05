package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.example.model.SocialEvent
import com.example.ui.theme.*

@Composable
fun EventCardBanner(
    event: SocialEvent,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
    ) {
        // High-craft Graphic Illustration of real-world venue / stage / conference
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            when (event.bannerTheme) {
                "tech" -> {
                    // Modern Tech Auditorium / Summit Stage
                    val bgBrush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF131B24), Color(0xFF1E293B), Color(0xFF0F172A))
                    )
                    drawRect(brush = bgBrush)

                    // Stage glow & spotlight
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(ColorTeal.copy(alpha = 0.45f), Color.Transparent),
                            center = Offset(w * 0.5f, h * 0.35f),
                            radius = w * 0.45f
                        ),
                        center = Offset(w * 0.5f, h * 0.35f),
                        radius = w * 0.45f
                    )

                    // Digital grid perspective lines on the floor
                    val gridPath = Path().apply {
                        for (i in 0..10) {
                            val startX = w * 0.5f + (i - 5) * (w * 0.05f)
                            val endX = (i / 10f) * w
                            moveTo(startX, h * 0.65f)
                            lineTo(endX, h)
                        }
                    }
                    drawPath(gridPath, color = ColorTeal.copy(alpha = 0.18f))

                    // Audience heads silhouette at bottom
                    for (i in 0..12) {
                        val cx = (i / 12f) * w + (i % 3 * 8f)
                        val cy = h - 6f - (i % 2 * 10f)
                        drawCircle(
                            color = Color(0xFF0B0C10),
                            radius = 16f,
                            center = Offset(cx, cy)
                        )
                    }

                    // Keynote Stage Screen glowing
                    drawRoundRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(ColorBurntOrange.copy(alpha = 0.6f), ColorTeal.copy(alpha = 0.7f))
                        ),
                        topLeft = Offset(w * 0.32f, h * 0.22f),
                        size = Size(w * 0.36f, h * 0.35f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                    )
                }

                "code" -> {
                    // Rust & Go Developers Lounge / Hackathon Room
                    val bgBrush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF1A1D20), Color(0xFF262C34), Color(0xFF15181C))
                    )
                    drawRect(brush = bgBrush)

                    // Warm amber and teal monitor glows
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(ColorBurntOrange.copy(alpha = 0.35f), Color.Transparent),
                            center = Offset(w * 0.25f, h * 0.4f),
                            radius = w * 0.4f
                        ),
                        center = Offset(w * 0.25f, h * 0.4f),
                        radius = w * 0.4f
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(ColorTeal.copy(alpha = 0.30f), Color.Transparent),
                            center = Offset(w * 0.75f, h * 0.45f),
                            radius = w * 0.4f
                        ),
                        center = Offset(w * 0.75f, h * 0.45f),
                        radius = w * 0.4f
                    )

                    // Silhouettes of laptops & developers sitting together
                    drawRect(
                        color = Color(0xFF0B0C10).copy(alpha = 0.95f),
                        topLeft = Offset(0f, h * 0.75f),
                        size = Size(w, h * 0.25f)
                    )
                    for (i in 0..5) {
                        val lx = w * 0.15f + i * (w * 0.15f)
                        drawRoundRect(
                            color = Color(0xFF4B5563),
                            topLeft = Offset(lx, h * 0.68f),
                            size = Size(24f, 16f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
                        )
                    }
                }

                else -> {
                    // Rooftop, Bar or General Ambient Scene with Twilight & Lanterns
                    val bgBrush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF1B1A2A), Color(0xFF2B2538), Color(0xFF15141E))
                    )
                    drawRect(brush = bgBrush)

                    // Twilight warm lanterns
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(ColorMustard.copy(alpha = 0.40f), Color.Transparent),
                            center = Offset(w * 0.3f, h * 0.3f),
                            radius = w * 0.35f
                        ),
                        center = Offset(w * 0.3f, h * 0.3f),
                        radius = w * 0.35f
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(ColorBurntOrange.copy(alpha = 0.35f), Color.Transparent),
                            center = Offset(w * 0.7f, h * 0.35f),
                            radius = w * 0.4f
                        ),
                        center = Offset(w * 0.7f, h * 0.35f),
                        radius = w * 0.4f
                    )

                    // String light line
                    val stringPath = Path().apply {
                        moveTo(0f, h * 0.25f)
                        quadraticBezierTo(w * 0.5f, h * 0.45f, w, h * 0.25f)
                    }
                    drawPath(stringPath, color = ColorMustard.copy(alpha = 0.4f))
                    for (i in 1..6) {
                        val t = i / 7f
                        val x = w * t
                        val y = h * 0.25f + (h * 0.20f) * (4 * t * (1 - t))
                        drawCircle(color = ColorMustard, radius = 5f, center = Offset(x, y))
                    }
                }
            }
        }
    }
}
