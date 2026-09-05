package com.example.ui.components

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.SocialEvent
import com.example.ui.theme.*

/**
 * Página Completa do Evento
 * Exibe todas as informações detalhadas, descrição na íntegra, fotos e ações completas
 * quando o usuário clica no card visual do evento.
 */
@Composable
fun EventDetailsDialog(
    event: SocialEvent,
    isOwner: Boolean = false,
    onDismiss: () -> Unit,
    onDirectCheckIn: () -> Unit,
    onOpenRoute: () -> Unit,
    onLikeClick: () -> Unit,
    onRateClick: () -> Unit,
    onOpenStationModal: () -> Unit,
    onManageMedia: () -> Unit = {},
    onAddMedia: (List<String>) -> Unit = {}
) {
    val isInsideGeofence = event.distanceKm <= 0.8
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .testTag("event_details_dialog_${event.id}"),
            shape = RoundedCornerShape(26.dp),
            color = BgSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(bottom = 90.dp)
                ) {
                    // 1. Imagem de Publicação em Alta Resolução / Carretel de Mídia
                    Box(modifier = Modifier.fillMaxWidth()) {
                        EventMediaReelBanner(
                            event = event,
                            isOwner = isOwner,
                            onAddMedia = onAddMedia,
                            onManageMedia = onManageMedia,
                            height = 240.dp
                        )

                        // Botão Fechar sobreposto na foto
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp)
                                .size(36.dp)
                                .background(Color.Black.copy(alpha = 0.55f), CircleShape)
                                .testTag("close_event_details_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fechar Página do Evento",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Categoria & Distância sobrepostos no topo direito
                        Surface(
                            shape = RoundedCornerShape(9999.dp),
                            color = Color.Black.copy(alpha = 0.65f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = event.category.label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // 2. Informações Principais
                    Column(modifier = Modifier.padding(20.dp)) {
                        // Linha de Status de Proximidade e Horário
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isInsideGeofence) {
                                Surface(
                                    shape = RoundedCornerShape(9999.dp),
                                    color = ColorBurntOrangeLight,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ColorBurntOrange.copy(alpha = 0.4f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(7.dp)
                                                .background(ColorBurntOrange, CircleShape)
                                        )
                                        Text(
                                            text = "No Local (45m)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ColorBurntOrangeHover
                                        )
                                    }
                                }
                            } else {
                                val walkMinutes = (event.distanceKm * 12).toInt().coerceAtLeast(2)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.DirectionsWalk,
                                        contentDescription = null,
                                        tint = ColorTeal,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${event.distanceKm} km • ~${walkMinutes} min a pé",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ColorTeal
                                    )
                                }
                            }

                            Text(
                                text = "${event.dateDisplay} às ${event.timeDisplay}",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Título do Encontro
                        Text(
                            text = event.title,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorDarkObsidian,
                            lineHeight = 28.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Localização com ícone
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = ColorBurntOrange,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = event.locationName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ColorDarkObsidian
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Card do Anfitrião
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = BgSecondary,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(ColorTealLight, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = ColorTeal,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = event.hostName,
                                                fontSize = 13.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = ColorDarkObsidian
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Verificado",
                                                tint = ColorTeal,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                        Text(
                                            text = if (isOwner) "👑 Você é o organizador" else "Organizador Verificado",
                                            fontSize = 11.5.sp,
                                            color = if (isOwner) ColorBurntOrange else TextSecondary
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(9999.dp),
                                    color = ColorMustardLight
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        ObsidianPrismRatingIcon(isFilled = true, size = 13.dp, tint = ColorMustardHover)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "★ %.1f".format(event.ratingAvg),
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ColorMustardHover
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // SEÇÃO: Sobre o Encontro (Descrição Completa que foi movida do card)
                        Text(
                            text = "Sobre o Encontro",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorDarkObsidian
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = event.description,
                            fontSize = 14.sp,
                            color = ColorDarkObsidian.copy(alpha = 0.85f),
                            lineHeight = 21.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Presença da Tribo
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = BgSecondary,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row {
                                    listOf("⚡", "🎨", "🚀").forEachIndexed { idx, emoji ->
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(if (idx == 0) ColorTealLight else if (idx == 1) ColorMustardLight else ColorBurntOrangeLight)
                                                .border(1.5.dp, BgSurface, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = emoji, fontSize = 11.sp)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Text(
                                    text = "${event.attendeesCount} pessoas confirmadas da sua Tribo",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ColorDarkObsidian
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Recompensa OQUANTUM
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = ColorMustardLight.copy(alpha = 0.45f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ColorMustard.copy(alpha = 0.35f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.MonetizationOn,
                                    contentDescription = null,
                                    tint = ColorMustardHover,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Desbloqueia Selo Presencial +30 OQ ao validar presença",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ColorMustardHover
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Botão Secundário do Totem
                        OutlinedButton(
                            onClick = onOpenStationModal,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
                        ) {
                            Icon(
                                Icons.Default.QrCode,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Inspecionar Totem da Recepção",
                                fontSize = 13.sp,
                                color = ColorDarkObsidian
                            )
                        }

                        if (isOwner) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = onManageMedia,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ColorBurntOrange),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorBurntOrange)
                            ) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Gerenciar Carretel de Fotos", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // 3. Barra Fixa de Ação no Rodapé do Modal
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(),
                    color = BgSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Curtir / Ressoar
                        ObsidianResonateButton(
                            isResonated = event.isLiked,
                            count = event.likesCount,
                            onClick = onLikeClick,
                            modifier = Modifier.testTag("details_resonate_btn")
                        )

                        // Avaliar
                        ObsidianRatingIconTrigger(
                            onClick = {
                                val checkInTime = event.checkInTimestamp ?: 0L
                                if (System.currentTimeMillis() - checkInTime >= 30 * 60 * 1000L) {
                                    onRateClick()
                                } else {
                                    Toast.makeText(context, "Avaliação liberada apenas após 30 minutos de presença no encontro.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            rating = event.ratingAvg,
                            size = 46.dp,
                            iconSize = 25.dp,
                            modifier = Modifier.testTag("details_rate_btn")
                        )

                        // Ação Principal: Presença com Ícone Exclusivo (SEM TEXTO) ou Rota
                        if (isInsideGeofence) {
                            Spacer(modifier = Modifier.weight(1f))
                            ObsidianCheckInIconTrigger(
                                onClick = onDirectCheckIn,
                                size = 46.dp,
                                iconSize = 25.dp,
                                containerColor = ColorBurntOrange,
                                isPulsing = true,
                                contentDescription = "Validar Presença Física",
                                modifier = Modifier.testTag("details_checkin_primary_btn")
                            )
                        } else {
                            Button(
                                onClick = onOpenRoute,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("details_route_primary_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = ColorTeal),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(
                                    Icons.Default.Navigation,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Como Chegar",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
