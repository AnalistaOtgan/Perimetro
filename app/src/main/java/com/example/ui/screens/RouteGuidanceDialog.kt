package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SocialEvent
import com.example.ui.components.ObsidianModalDialog
import com.example.ui.theme.*

/**
 * Diálogo de Como Chegar e Guia de Presença Física do Participante com estética Obsidian sob medida
 */
@Composable
fun RouteGuidanceDialog(
    event: SocialEvent,
    onDismiss: () -> Unit,
    onDirectCheckIn: () -> Unit
) {
    ObsidianModalDialog(
        onDismissRequest = onDismiss,
        title = "Rota & Presença",
        subtitle = "Guia do Participante • ${event.title}",
        icon = Icons.Default.Navigation,
        iconTint = ColorTeal,
        iconBgColor = ColorTealLight,
        headerAccentGradient = listOf(ColorTeal, ColorBurntOrange, ColorMustard),
        wrapHeight = true,
        buttons = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(9999.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
            ) {
                Text("Fechar", fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = {
                    onDismiss()
                    onDirectCheckIn()
                },
                modifier = Modifier
                    .weight(1.4f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ColorBurntOrange),
                shape = RoundedCornerShape(9999.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Validar Presença", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Event Title & Location Box
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = BgSecondary,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = event.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorDarkObsidian
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Place,
                            contentDescription = null,
                            tint = ColorBurntOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = event.locationName,
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Métricas de Deslocamento
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Distância
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = BgSecondary,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Distância", fontSize = 11.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (event.distanceKm <= 0.2) "${(event.distanceKm * 1000).toInt()}m" else "${event.distanceKm} km",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = ColorDarkObsidian
                        )
                    }
                }

                // Tempo a Pé
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = BgSecondary,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("A pé / Bike", fontSize = 11.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(2.dp))
                        val walkingMin = (event.distanceKm * 12).toInt().coerceAtLeast(1)
                        Text(
                            text = "$walkingMin min",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = ColorTeal
                        )
                    }
                }

                // Raio Geofence
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = BgSecondary,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Raio Validação", fontSize = 11.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${event.geofenceRadiusMeters}m",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = ColorBurntOrange
                        )
                    }
                }
            }

            // Checklist Presencial para o Participante
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = ColorTealLight,
                border = androidx.compose.foundation.BorderStroke(1.dp, ColorTealBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Como validar sua presença física:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = ColorTeal
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("1.", fontWeight = FontWeight.Black, color = ColorTeal, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Entre no raio de ${event.geofenceRadiusMeters}m do local físico.",
                            fontSize = 12.sp,
                            color = ColorDarkObsidian
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("2.", fontWeight = FontWeight.Black, color = ColorTeal, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Aproxime-se do totem ou da recepção com seu smartphone.",
                            fontSize = 12.sp,
                            color = ColorDarkObsidian
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("3.", fontWeight = FontWeight.Black, color = ColorTeal, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Confirme o check-in para ganhar +50 OQ e o Selo de Presença.",
                            fontSize = 12.sp,
                            color = ColorDarkObsidian
                        )
                    }
                }
            }
        }
    }
}
