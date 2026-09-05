package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.ui.theme.*

@Composable
fun OrganicCirclesBackground(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BgCanvas)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Radial gradient 1: circle at 10% 20%, rgba(0, 121, 107, 0.06) 0%, transparent 40%
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(ColorTeal.copy(alpha = 0.09f), Color.Transparent),
                    center = Offset(w * 0.10f, h * 0.20f),
                    radius = w * 0.45f
                ),
                radius = w * 0.45f,
                center = Offset(w * 0.10f, h * 0.20f)
            )

            // Radial gradient 2: circle at 90% 15%, rgba(216, 122, 86, 0.07) 0%, transparent 45%
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(ColorBurntOrange.copy(alpha = 0.10f), Color.Transparent),
                    center = Offset(w * 0.90f, h * 0.15f),
                    radius = w * 0.50f
                ),
                radius = w * 0.50f,
                center = Offset(w * 0.90f, h * 0.15f)
            )

            // Radial gradient 3: circle at 50% 85%, rgba(249, 199, 79, 0.08) 0%, transparent 50%
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(ColorMustard.copy(alpha = 0.12f), Color.Transparent),
                    center = Offset(w * 0.50f, h * 0.85f),
                    radius = w * 0.55f
                ),
                radius = w * 0.55f,
                center = Offset(w * 0.50f, h * 0.85f)
            )

            // Radial gradient 4: circle at 80% 80%, rgba(0, 121, 107, 0.05) 0%, transparent 35%
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(ColorTeal.copy(alpha = 0.08f), Color.Transparent),
                    center = Offset(w * 0.80f, h * 0.80f),
                    radius = w * 0.40f
                ),
                radius = w * 0.40f,
                center = Offset(w * 0.80f, h * 0.80f)
            )

            // Subtle geometric accent rings from brand identity
            drawCircle(
                color = ColorTeal.copy(alpha = 0.10f),
                radius = w * 0.28f,
                center = Offset(w * 0.12f, h * 0.18f),
                style = Stroke(width = 1.5f)
            )

            drawCircle(
                color = ColorBurntOrange.copy(alpha = 0.10f),
                radius = w * 0.32f,
                center = Offset(w * 0.88f, h * 0.16f),
                style = Stroke(width = 1.5f)
            )
        }
    }
}

