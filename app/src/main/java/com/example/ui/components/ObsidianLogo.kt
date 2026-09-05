package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkObsidian
import com.example.ui.theme.ObsidianBurntOrange
import com.example.ui.theme.ObsidianMustardYellow
import com.example.ui.theme.ObsidianTeal

@Composable
fun ObsidianLogoEmblem(
    modifier: Modifier = Modifier,
    size: Dp = 96.dp
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = this.size.width
            val canvasH = this.size.height
            val center = Offset(canvasW / 2f, canvasH / 2f)
            val radius = canvasW * 0.42f

            // Background subtle warmth circle
            drawCircle(
                color = ObsidianMustardYellow.copy(alpha = 0.10f),
                radius = radius * 1.08f,
                center = center
            )

            // Person 1 (Teal - Top-Left)
            val p1HeadCenter = Offset(center.x - radius * 0.48f, center.y - radius * 0.44f)
            drawCircle(
                color = ObsidianTeal,
                radius = radius * 0.22f,
                center = p1HeadCenter
            )
            val p1BodyPath = Path().apply {
                moveTo(p1HeadCenter.x, p1HeadCenter.y + radius * 0.22f)
                cubicTo(
                    center.x - radius * 0.85f, center.y - radius * 0.1f,
                    center.x - radius * 0.75f, center.y + radius * 0.45f,
                    center.x - radius * 0.35f, center.y + radius * 0.35f
                )
                cubicTo(
                    center.x - radius * 0.15f, center.y + radius * 0.1f,
                    center.x - radius * 0.15f, center.y - radius * 0.15f,
                    p1HeadCenter.x, p1HeadCenter.y + radius * 0.22f
                )
                close()
            }
            drawPath(path = p1BodyPath, color = ObsidianTeal)

            // Person 2 (Burnt Orange - Top-Right)
            val p2HeadCenter = Offset(center.x + radius * 0.48f, center.y - radius * 0.44f)
            drawCircle(
                color = ObsidianBurntOrange,
                radius = radius * 0.22f,
                center = p2HeadCenter
            )
            val p2BodyPath = Path().apply {
                moveTo(p2HeadCenter.x, p2HeadCenter.y + radius * 0.22f)
                cubicTo(
                    center.x + radius * 0.85f, center.y - radius * 0.1f,
                    center.x + radius * 0.75f, center.y + radius * 0.45f,
                    center.x + radius * 0.35f, center.y + radius * 0.35f
                )
                cubicTo(
                    center.x + radius * 0.15f, center.y + radius * 0.1f,
                    center.x + radius * 0.15f, center.y - radius * 0.15f,
                    p2HeadCenter.x, p2HeadCenter.y + radius * 0.22f
                )
                close()
            }
            drawPath(path = p2BodyPath, color = ObsidianBurntOrange)

            // Person 3 (Mustard Yellow - Bottom)
            val p3HeadCenter = Offset(center.x, center.y + radius * 0.65f)
            drawCircle(
                color = ObsidianMustardYellow,
                radius = radius * 0.22f,
                center = p3HeadCenter
            )
            val p3BodyPath = Path().apply {
                moveTo(p3HeadCenter.x - radius * 0.25f, p3HeadCenter.y - radius * 0.15f)
                cubicTo(
                    center.x - radius * 0.5f, center.y + radius * 0.3f,
                    center.x + radius * 0.5f, center.y + radius * 0.3f,
                    p3HeadCenter.x + radius * 0.25f, p3HeadCenter.y - radius * 0.15f
                )
                cubicTo(
                    center.x + radius * 0.25f, center.y + radius * 0.1f,
                    center.x - radius * 0.25f, center.y + radius * 0.1f,
                    p3HeadCenter.x - radius * 0.25f, p3HeadCenter.y - radius * 0.15f
                )
                close()
            }
            drawPath(path = p3BodyPath, color = ObsidianMustardYellow)

            // Central Gleaming Star (Spark of Presence)
            val starPath = Path().apply {
                val r1 = radius * 0.24f
                val r2 = radius * 0.08f
                val points = 8
                val angleStep = Math.PI / points
                for (i in 0 until (points * 2)) {
                    val r = if (i % 2 == 0) r1 else r2
                    val angle = i * angleStep - Math.PI / 2
                    val x = (center.x + r * Math.cos(angle)).toFloat()
                    val y = (center.y + r * Math.sin(angle)).toFloat()
                    if (i == 0) moveTo(x, y) else lineTo(x, y)
                }
                close()
            }
            drawPath(path = starPath, color = Color.White)
        }
    }
}

@Composable
fun ObsidianHeader(
    modifier: Modifier = Modifier,
    title: String = "Obsidian",
    subtitle: String? = "A Rede Social da Presença Real"
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ObsidianLogoEmblem(size = 72.dp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
