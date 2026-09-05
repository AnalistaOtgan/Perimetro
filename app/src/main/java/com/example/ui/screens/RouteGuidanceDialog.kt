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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.SocialEvent
import com.example.ui.components.ObsidianCheckInIconTrigger
import com.example.ui.components.ObsidianStationPortalIcon
import com.example.ui.theme.*

/**
 * Diálogo de Como Chegar e Guia de Presença Física do Participante
 */
@Composable
fun RouteGuidanceDialog(
    event: SocialEvent,
    onDismiss: () -> Unit,
    onDirectCheckIn: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(16.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            color = BgSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(ColorTealLight, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Navigation,
                                contentDescription = null,
                                tint = ColorTeal,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Rota & Presença",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorDarkObsidian
                            )
                            Text(
                                text = "Guia do Participante",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

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
                            Icon(Icons.Default.Place, contentDescription = null, tint = ColorBurntOrange, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = event.locationName,
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Métricas de Deslocamento
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Distância
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        color = BgSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Distância", fontSize = 11.sp, color = TextMuted)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (event.distanceKm <= 0.2) "${(event.distanceKm * 1000).toInt()}m" else "${event.distanceKm} km",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = ColorDarkObsidian
                            )
                        }
                    }

                    // Tempo a Pé
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        color = BgSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("A pé / Bike", fontSize = 11.sp, color = TextMuted)
                            Spacer(modifier = Modifier.height(2.dp))
                            val walkingMin = (event.distanceKm * 12).toInt().coerceAtLeast(1)
                            Text(
                                text = "$walkingMin min",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = ColorTeal
                            )
                        }
                    }

                    // Raio Geofence
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        color = BgSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Raio de Validação", fontSize = 11.sp, color = TextMuted)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${event.geofenceRadiusMeters}m",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = ColorBurntOrange
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Checklist Presencial para o Participante
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = ColorTealLight.copy(alpha = 0.5f),
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
                            Text("Entre no raio de ${event.geofenceRadiusMeters}m do local físico.", fontSize = 12.sp, color = ColorDarkObsidian)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("2.", fontWeight = FontWeight.Black, color = ColorTeal, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Aproxime-se do totem ou totem na recepção com seu smartphone.", fontSize = 12.sp, color = ColorDarkObsidian)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("3.", fontWeight = FontWeight.Black, color = ColorTeal, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Confirme o check-in para ganhar +50 OQ e o Selo de Presença.", fontSize = 12.sp, color = ColorDarkObsidian)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Ação de Presença acionada puramente pelo Ícone Exclusivo (SEM TEXTO)
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    ObsidianCheckInIconTrigger(
                        onClick = {
                            onDismiss()
                            onDirectCheckIn()
                        },
                        size = 56.dp,
                        iconSize = 30.dp,
                        containerColor = ColorBurntOrange,
                        isPulsing = true,
                        contentDescription = "Validar Presença Física"
                    )
                }
            }
        }
    }
}
