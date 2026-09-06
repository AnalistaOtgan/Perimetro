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
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
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
import com.example.location.LocationUtils
import com.example.location.UserLocation
import com.example.model.CheckInMethod
import com.example.model.SocialEvent
import com.example.ui.components.DynamicQRCodeCard
import com.example.ui.components.ObsidianCheckInIconTrigger
import com.example.ui.components.ObsidianModalDialog
import com.example.ui.components.ObsidianPrismRatingIcon
import com.example.ui.components.ObsidianRatingIconTrigger
import com.example.ui.components.OrganicCirclesBackground
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInScreen(
    events: List<SocialEvent>,
    userLocation: UserLocation? = null,
    onPerformCheckIn: (SocialEvent, CheckInMethod) -> Unit,
    onOpenFeedback: (SocialEvent) -> Unit
) {
    var selectedEventIndex by remember { mutableIntStateOf(0) }
    val currentEvent = events.getOrNull(selectedEventIndex) ?: events.firstOrNull()
    var selectedMethod by remember { mutableStateOf(CheckInMethod.QR_DYNAMIC) }
    var showManualFallbackAlert by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val distanceMeters = remember(userLocation, currentEvent) {
        if (userLocation != null && currentEvent != null) {
            LocationUtils.calculateDistanceMeters(
                userLocation.latitude, userLocation.longitude,
                currentEvent.latitude, currentEvent.longitude
            ).toInt()
        } else {
            45
        }
    }
    val isInsideGeofence = remember(distanceMeters, currentEvent) {
        currentEvent?.let { distanceMeters <= it.geofenceRadiusMeters } ?: true
    }

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
                    color = if (isInsideGeofence) ColorTealLight else ColorBurntOrangeLight,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isInsideGeofence) ColorTealBorder else ColorBurntOrange.copy(alpha = 0.5f)
                    ),
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
                                .background(if (isInsideGeofence) ColorTeal else ColorBurntOrange),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isInsideGeofence) Icons.Default.GpsFixed else Icons.Default.GpsNotFixed,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = if (isInsideGeofence) "GEOFENCE POSTGIS: DENTRO DO RAIO" else "GEOFENCE POSTGIS: FORA DO RAIO",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isInsideGeofence) ColorTeal else ColorBurntOrange,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = if (distanceMeters < 1000) {
                                    "Você está a ${distanceMeters}m do centroide do evento"
                                } else {
                                    "Você está a %.1f km do centroide do evento".format(distanceMeters / 1000f)
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = ColorDarkObsidian
                            )
                            Text(
                                text = if (isInsideGeofence) {
                                    "Presença confirmada no raio de ${currentEvent?.geofenceRadiusMeters ?: 150}m • ${userLocation?.formattedAccuracy ?: "GPS Ativo"}"
                                } else {
                                    "Aproxime-se a menos de ${currentEvent?.geofenceRadiusMeters ?: 150}m para validar presença física"
                                },
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
                                        onClick = { 
                                            val checkInTime = currentEvent.checkInTimestamp ?: 0L
                                            if (System.currentTimeMillis() - checkInTime >= 30 * 60 * 1000L) {
                                                onOpenFeedback(currentEvent)
                                            } else {
                                                Toast.makeText(context, "Avaliação liberada apenas após 30 minutos de presença no encontro.", Toast.LENGTH_SHORT).show()
                                            }
                                        },
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
                                                onClick = {
                                                    if (!isInsideGeofence) {
                                                        val distStr = if (distanceMeters < 1000) "${distanceMeters}m" else "%.1f km".format(distanceMeters / 1000f)
                                                        Toast.makeText(
                                                            context,
                                                            "Atenção: Seu GPS está a $distStr (fora do raio de ${currentEvent.geofenceRadiusMeters}m). Check-in validado em modo de teste.",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                                    onPerformCheckIn(currentEvent, CheckInMethod.GPS_ONLY)
                                                },
                                                size = 56.dp,
                                                iconSize = 30.dp,
                                                containerColor = if (isInsideGeofence) ColorTeal else ColorBurntOrange,
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
                    ObsidianModalDialog(
                        onDismissRequest = { showManualFallbackAlert = false },
                        title = "Validação Manual de Entrada",
                        subtitle = currentEvent.title,
                        icon = Icons.Default.AdminPanelSettings,
                        iconTint = ColorBurntOrange,
                        iconBgColor = ColorBurntOrangeLight,
                        headerAccentGradient = listOf(ColorBurntOrange, ColorMustard, ColorTeal),
                        wrapHeight = true,
                        buttons = {
                            OutlinedButton(
                                onClick = { showManualFallbackAlert = false },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(9999.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                            ) {
                                Text("Cancelar", fontWeight = FontWeight.SemiBold)
                            }

                            Button(
                                onClick = {
                                    onPerformCheckIn(currentEvent, CheckInMethod.GPS_ONLY)
                                    showManualFallbackAlert = false
                                },
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ColorBurntOrange),
                                shape = RoundedCornerShape(9999.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Atestar Presença", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = BgSecondary,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(ColorTealLight, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.SupportAgent,
                                            contentDescription = null,
                                            tint = ColorTeal,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Text(
                                        text = "O staff autorizado do evento pode atestar manualmente sua entrada física caso a leitura por QR Code ou câmera apresente instabilidade técnica.",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 16.sp),
                                        color = TextSecondary
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = BgSecondary,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = ColorBurntOrange,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = currentEvent.locationName,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            color = ColorDarkObsidian
                                        )
                                        Text(
                                            text = "Validação cruzada com coordenadas geográficas do local",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                                            color = TextMuted
                                        )
                                    }
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = ColorTealLight
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = ColorTeal,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "Protocolo de contingência auditado • Salvo em histórico com timestamp",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                                        color = ColorTeal,
                                        fontWeight = FontWeight.SemiBold
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
