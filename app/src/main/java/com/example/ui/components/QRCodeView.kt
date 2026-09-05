package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkObsidian
import com.example.ui.theme.ObsidianBurntOrange
import com.example.ui.theme.ObsidianTeal
import kotlinx.coroutines.delay

@Composable
fun DynamicQRCodeCard(
    eventId: String,
    onCodeGenerated: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var secondsLeft by remember { mutableStateOf(15) }
    var qrSeed by remember { mutableStateOf(System.currentTimeMillis()) }
    var hashDisplay by remember { mutableStateOf("OBS-INIT") }

    LaunchedEffect(Unit) {
        while (true) {
            val epochSeconds = System.currentTimeMillis() / 1000
            val windowSeconds = 15
            val remaining = (windowSeconds - (epochSeconds % windowSeconds)).toInt()
            secondsLeft = remaining
            if (remaining == 15 || hashDisplay == "OBS-INIT") {
                qrSeed = System.currentTimeMillis()
                val hex = (qrSeed.toString() + eventId).hashCode().toUInt().toString(16).uppercase().take(8)
                hashDisplay = "OBS-$hex"
                onCodeGenerated(hashDisplay)
            }
            delay(1000)
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("dynamic_qr_card"),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(ObsidianTeal)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Proof of Presence",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = ObsidianTeal
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        progress = { secondsLeft / 15f },
                        modifier = Modifier.size(20.dp),
                        color = if (secondsLeft <= 3) ObsidianBurntOrange else ObsidianTeal,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${secondsLeft}s",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (secondsLeft <= 3) ObsidianBurntOrange else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // QR Code Matrix Box
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(2.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val gridSize = 19
                    val cellSize = size.width / gridSize
                    val random = java.util.Random(qrSeed / 15000)

                    // Draw 3 standard corner anchors
                    fun drawAnchor(xStart: Int, yStart: Int) {
                        drawRect(
                            color = Color(0xFF0B0C10),
                            topLeft = Offset(xStart * cellSize, yStart * cellSize),
                            size = Size(7 * cellSize, 7 * cellSize)
                        )
                        drawRect(
                            color = Color.White,
                            topLeft = Offset((xStart + 1) * cellSize, (yStart + 1) * cellSize),
                            size = Size(5 * cellSize, 5 * cellSize)
                        )
                        drawRect(
                            color = Color(0xFF0B0C10),
                            topLeft = Offset((xStart + 2) * cellSize, (yStart + 2) * cellSize),
                            size = Size(3 * cellSize, 3 * cellSize)
                        )
                    }

                    drawAnchor(0, 0)
                    drawAnchor(gridSize - 7, 0)
                    drawAnchor(0, gridSize - 7)

                    // Draw random data cells
                    for (x in 0 until gridSize) {
                        for (y in 0 until gridSize) {
                            val inAnchor1 = x < 7 && y < 7
                            val inAnchor2 = x >= gridSize - 7 && y < 7
                            val inAnchor3 = x < 7 && y >= gridSize - 7
                            if (!inAnchor1 && !inAnchor2 && !inAnchor3) {
                                if (random.nextBoolean()) {
                                    drawRect(
                                        color = Color(0xFF0B0C10),
                                        topLeft = Offset(x * cellSize, y * cellSize),
                                        size = Size(cellSize * 0.92f, cellSize * 0.92f)
                                    )
                                }
                            }
                        }
                    }

                    // Center spark overlay
                    val centerPoint = Offset(size.width / 2f, size.height / 2f)
                    drawCircle(
                        color = Color(0xFFD87A56),
                        radius = cellSize * 1.5f,
                        center = centerPoint
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = hashDisplay,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "QR Code Dinâmico • Anti-Screenshot",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
