package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CheckInMethod
import com.example.model.SocialEvent
import com.example.ui.components.DynamicQRCodeCard
import com.example.ui.components.ObsidianCheckInIconTrigger
import com.example.ui.components.ObsidianPrismRatingIcon
import com.example.ui.components.ObsidianRatingIconTrigger
import com.example.ui.components.OrganicCirclesBackground
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInScreen(
    events: List<SocialEvent>,
    onPerformCheckIn: (SocialEvent, CheckInMethod) -> Unit,
    onOpenFeedback: (SocialEvent) -> Unit
) {
    var selectedEventIndex by remember { mutableIntStateOf(0) }
    val currentEvent = events.getOrNull(selectedEventIndex) ?: events.firstOrNull()
    var selectedMethod by remember { mutableStateOf(CheckInMethod.QR_DYNAMIC) }
    var showManualFallbackAlert by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCanvas)
    ) {
        // Fluid Organic Pattern Background
        OrganicCirclesBackground()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = BgSurface.copy(alpha = 0.95f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Proof of Presence",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorDarkObsidian
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Surface(
                            shape = RoundedCornerShape(9999.dp),
                            color = ColorTealLight,
                            border = androidx.compose.foundation.BorderStroke(1.dp, ColorTealBorder)
                        ) {
                            Text(
                                text = "2-Fatores Ativo",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = ColorTeal,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Geofence Proximity Status Card
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = ColorTealLight,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ColorTealBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(ColorTeal),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.GpsFixed,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "GEOFENCE POSTGIS ATIVO",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = ColorTeal,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Você está a 45m do centroide do evento",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = ColorDarkObsidian
                            )
                            Text(
                                text = "Precisão GPS excelente • Raio autorizado de 150m",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = TextSecondary
                            )
                        }
                    }
                }

                // Event Selector Card
                if (events.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = BgSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(20.dp), spotColor = Color(0x1F0B0C10))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "EVENTO SELECIONADO",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorObsidianSubtle,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = currentEvent?.title ?: "",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorDarkObsidian
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${currentEvent?.locationName} • ${currentEvent?.dateDisplay} às ${currentEvent?.timeDisplay}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }

                // Method Selector Tabs (QR Dinâmico / GPS Geofence / BLE Beacon)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(9999.dp))
                        .background(BgSecondary)
                        .border(1.dp, BorderWarm, RoundedCornerShape(9999.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        CheckInMethod.QR_DYNAMIC to "QR Dinâmico",
                        CheckInMethod.GPS_ONLY to "GPS Geofence",
                        CheckInMethod.BLE_BEACON to "BLE Beacon"
                    ).forEach { (method, label) ->
                        val isSelected = selectedMethod == method
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(9999.dp)),
                            color = if (isSelected) ColorTeal else Color.Transparent,
                            onClick = { selectedMethod = method }
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else TextSecondary
                                )
                            }
                        }
                    }
                }

                // Interaction Area
                if (currentEvent != null) {
                    if (currentEvent.isCheckedIn) {
                        // Already Checked In State
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(4.dp, RoundedCornerShape(24.dp), spotColor = Color(0x1F0B0C10)),
                            shape = RoundedCornerShape(24.dp),
                            color = BgSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(26.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(CircleShape)
                                        .background(ColorTealLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = null,
                                        tint = ColorTeal,
                                        modifier = Modifier.size(38.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "Presença Validada!",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ColorDarkObsidian
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "+5.0 OQUANTUM creditados pela sua presença real atestada.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = ColorTeal,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(18.dp))

                                // Ação de Avaliação acionada puramente pelo Ícone Exclusivo Criado!
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    ObsidianRatingIconTrigger(
                                        onClick = { onOpenFeedback(currentEvent) },
                                        size = 56.dp,
                                        iconSize = 28.dp,
                                        isPulsing = true,
                                        contentDescription = "Avaliar Experiência do Encontro",
                                        modifier = Modifier.testTag("rate_event_btn")
                                    )
                                }
                            }
                        }
                    } else {
                        // Check-in Methods
                        when (selectedMethod) {
                            CheckInMethod.QR_DYNAMIC -> {
                                DynamicQRCodeCard(eventId = currentEvent.id)

                                Spacer(modifier = Modifier.height(14.dp))

                                // Ação de Check-in acionada puramente pelo Ícone Exclusivo Criado!
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    ObsidianCheckInIconTrigger(
                                        onClick = {
                                            onPerformCheckIn(currentEvent, CheckInMethod.QR_DYNAMIC)
                                        },
                                        size = 56.dp,
                                        iconSize = 30.dp,
                                        containerColor = ColorBurntOrange,
                                        isPulsing = true,
                                        contentDescription = "Validar Check-in no Local",
                                        modifier = Modifier.testTag("validate_qr_btn")
                                    )
                                }
                            }

                            CheckInMethod.GPS_ONLY -> {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(20.dp),
                                    color = BgSurface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(22.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = ColorTeal,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "Validação por Geolocalização PostGIS",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = ColorDarkObsidian
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Coordenadas GPS confirmam sua presença dentro do raio de tolerância.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(18.dp))
                                        Box(
                                            modifier = Modifier.fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            ObsidianCheckInIconTrigger(
                                                onClick = { onPerformCheckIn(currentEvent, CheckInMethod.GPS_ONLY) },
                                                size = 56.dp,
                                                iconSize = 30.dp,
                                                containerColor = ColorTeal,
                                                isPulsing = true,
                                                contentDescription = "Confirmar Presença via GPS",
                                                modifier = Modifier.testTag("validate_gps_btn")
                                            )
                                        }
                                    }
                                }
                            }

                            CheckInMethod.BLE_BEACON -> {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(20.dp),
                                    color = BgSurface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(22.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.BluetoothSearching,
                                            contentDescription = null,
                                            tint = ColorTeal,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "Beacon Físico Pareado",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = ColorDarkObsidian
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Sinal BLE 'Obsidian-Beacon-Hub' detectado com RSSI forte (-54dBm).",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(18.dp))
                                        Box(
                                            modifier = Modifier.fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            ObsidianCheckInIconTrigger(
                                                onClick = { onPerformCheckIn(currentEvent, CheckInMethod.BLE_BEACON) },
                                                size = 56.dp,
                                                iconSize = 30.dp,
                                                containerColor = ColorTeal,
                                                isPulsing = true,
                                                contentDescription = "Autenticar Presença via Beacon",
                                                modifier = Modifier.testTag("validate_beacon_btn")
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Fallback Manual Button
                        TextButton(
                            onClick = { showManualFallbackAlert = true },
                            modifier = Modifier.testTag("fallback_manual_btn")
                        ) {
                            Icon(Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextSecondary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Sem internet ou problema no leitor? Validação Manual",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }

                if (showManualFallbackAlert && currentEvent != null) {
                    AlertDialog(
                        onDismissRequest = { showManualFallbackAlert = false },
                        title = { Text("Validação Manual de Recepção", fontWeight = FontWeight.Bold, color = ColorDarkObsidian) },
                        text = {
                            Text(
                                text = "O staff do evento pode atestar manualmente sua entrada física caso a leitura apresente instabilidade técnica. Confirmar?",
                                color = TextSecondary
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    onPerformCheckIn(currentEvent, CheckInMethod.GPS_ONLY)
                                    showManualFallbackAlert = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ColorBurntOrange),
                                shape = RoundedCornerShape(9999.dp)
                            ) {
                                Text("Atestar Presença", fontWeight = FontWeight.Bold)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showManualFallbackAlert = false }) {
                                Text("Cancelar", color = TextSecondary)
                            }
                        }
                    )
                }
            }
        }
    }
}
