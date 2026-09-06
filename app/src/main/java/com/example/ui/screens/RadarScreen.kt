package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.EventCardBanner
import com.example.ui.components.EventDetailsDialog
import com.example.ui.components.EventMediaReelBanner
import com.example.ui.components.ManageEventMediaDialog
import com.example.ui.components.ObsidianCheckInIconTrigger
import com.example.ui.components.ObsidianEventsNexusIcon
import com.example.ui.components.ObsidianLogoEmblem
import com.example.ui.components.ObsidianPrismRatingIcon
import com.example.ui.components.ObsidianRatingButton
import com.example.ui.components.ObsidianRatingIconTrigger
import com.example.ui.components.ObsidianResonateButton
import com.example.ui.components.ObsidianStationPortalIcon
import com.example.ui.components.OrganicCirclesBackground
import com.example.ui.components.InteractiveCityEventMap
import com.example.ui.components.ParticipantSonarRadar
import com.example.ui.theme.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class ParticipantRadarViewMode {
    MAPA,
    LISTA
}

enum class ParticipantFilterMode(val label: String, val icon: ImageVector) {
    TODOS("Todos", Icons.Default.LocalFireDepartment),
    NO_MEU_RAIO("< 1.5km", Icons.Default.DirectionsWalk),
    AO_VIVO("Ao Vivo", Icons.Default.AutoAwesome),
    BARES("Bares", Icons.Default.LocalBar),
    TECH("Tech", Icons.Default.Code),
    SHOWS("Shows", Icons.Default.MusicNote),
    TRIBO("Minha Tribo", Icons.Default.Groups)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadarScreen(
    events: List<SocialEvent>,
    currentUserTier: UserTier = UserTier.PRATA,
    currentUser: UserProfile? = null,
    onSelectEvent: (SocialEvent) -> Unit = {},
    onCheckInClick: (SocialEvent) -> Unit = {},
    onCreateEventClick: () -> Unit = {},
    onNavigateToEvents: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedSonarEvent by remember { mutableStateOf(events.firstOrNull()) }
    var eventForDetails by remember { mutableStateOf<SocialEvent?>(null) }
    var eventForRouteGuidance by remember { mutableStateOf<SocialEvent?>(null) }
    var eventForStation by remember { mutableStateOf<SocialEvent?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var currentFilter by remember { mutableStateOf(ParticipantFilterMode.TODOS) }
    var isRouteActive by remember { mutableStateOf(false) }
    var recenterRequested by remember { mutableStateOf(false) }

    // Pulso do GPS ativo
    val infiniteTransition = rememberInfiniteTransition(label = "radar_pulse")
    val gpsPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gps_pulse"
    )

    // Filtragem dinâmica de encontros por texto de busca e pílulas de filtro
    val filteredEvents = remember(events, searchQuery, currentFilter) {
        events.filter { ev ->
            val matchesSearch = searchQuery.isBlank() ||
                ev.title.contains(searchQuery, ignoreCase = true) ||
                ev.locationName.contains(searchQuery, ignoreCase = true) ||
                ev.category.label.contains(searchQuery, ignoreCase = true) ||
                ev.tags.any { it.contains(searchQuery, ignoreCase = true) }

            val matchesFilter = when (currentFilter) {
                ParticipantFilterMode.TODOS -> true
                ParticipantFilterMode.NO_MEU_RAIO -> ev.distanceKm <= 1.5
                ParticipantFilterMode.AO_VIVO -> ev.distanceKm <= 3.5
                ParticipantFilterMode.BARES -> ev.category == EventCategory.BAR
                ParticipantFilterMode.TECH -> ev.category == EventCategory.TECNOLOGIA
                ParticipantFilterMode.SHOWS -> ev.category == EventCategory.SHOW
                ParticipantFilterMode.TRIBO -> ev.attendeesCount >= 10
            }

            matchesSearch && matchesFilter
        }
    }

    // Manter um evento selecionado válido
    LaunchedEffect(filteredEvents) {
        if (selectedSonarEvent == null || filteredEvents.none { it.id == selectedSonarEvent?.id }) {
            selectedSonarEvent = filteredEvents.firstOrNull()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1415))
    ) {
        // 1. CAMADA BASE (100% DA TELA): MAPA URBANO INTERATIVO ESTILO UBER / 99 POP
        InteractiveCityEventMap(
            events = filteredEvents,
            selectedEvent = selectedSonarEvent,
            onSelectEvent = { ev ->
                selectedSonarEvent = ev
                isRouteActive = true
                eventForDetails = ev
            },
            isRouteActive = isRouteActive,
            onRecenterRequested = recenterRequested,
            onResetRecenter = { recenterRequested = false },
            modifier = Modifier.fillMaxSize()
        )

        // 2. CONTROLES FLUTUANTES NO TOPO (Header, Busca e Filtros estilo Uber)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .align(Alignment.TopCenter)
        ) {
            // Barra Superior de Marca, Presença Ao Vivo e Pontos OQ
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = BgSurface.copy(alpha = 0.94f),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ObsidianLogoEmblem(size = 28.dp)
                        Column {
                            Text(
                                text = "Radar de Encontros",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = ColorDarkObsidian,
                                letterSpacing = (-0.3).sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(ColorTeal.copy(alpha = gpsPulseAlpha), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "São Paulo, SP • Ao vivo agora",
                                    fontSize = 10.5.sp,
                                    color = ColorTeal,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(9999.dp),
                            color = ColorMustardLight,
                            border = androidx.compose.foundation.BorderStroke(1.dp, ColorMustard.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = ColorMustardHover,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "248.8 OQ",
                                    fontSize = 11.sp,
                                    color = ColorMustardHover,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(9999.dp),
                            color = ColorTealLight,
                            border = androidx.compose.foundation.BorderStroke(1.dp, ColorTealBorder)
                        ) {
                            Text(
                                text = currentUserTier.title,
                                fontSize = 10.5.sp,
                                color = ColorTeal,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Barra de Busca Flutuante ("Para onde quer ir hoje? Buscar encontro...")
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(9999.dp),
                color = BgSurface.copy(alpha = 0.95f),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm),
                shadowElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = ColorTeal,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                text = "Para onde vamos? Buscar encontro ou vibe...",
                                fontSize = 13.sp,
                                color = TextMuted
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { searchQuery = "" },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Limpar busca",
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Pílulas de Filtros Horizontais
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(ParticipantFilterMode.values()) { filterMode ->
                    ParticipantFilterChip(
                        label = filterMode.label,
                        icon = filterMode.icon,
                        isSelected = currentFilter == filterMode,
                        onClick = { currentFilter = filterMode }
                    )
                }
            }
        }

        // 3. BOTÕES FLUTUANTES LATERAIS (Recentralizar GPS e Alternar Rota)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp, bottom = 240.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Botão Recentralizar GPS no Usuário
            Surface(
                shape = CircleShape,
                color = BgSurface.copy(alpha = 0.95f),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .size(46.dp)
                    .clickable {
                        recenterRequested = true
                        Toast.makeText(context, "GPS centralizado na sua localização", Toast.LENGTH_SHORT).show()
                    }
                    .testTag("recenter_gps_btn")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Centralizar no meu local",
                        tint = ColorTeal,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Botão Ativar / Desativar Traçado de Rota Estilo Uber
            if (selectedSonarEvent != null) {
                Surface(
                    shape = CircleShape,
                    color = if (isRouteActive) ColorTeal else BgSurface.copy(alpha = 0.95f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isRouteActive) ColorTeal else BorderWarm
                    ),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .size(46.dp)
                        .clickable {
                            isRouteActive = !isRouteActive
                        }
                        .testTag("toggle_route_btn")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.DirectionsWalk,
                            contentDescription = "Ver rota",
                            tint = if (isRouteActive) Color.White else ColorTeal,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        // 4. PAINEL INFERIOR FLUTUANTE DE ENCONTROS (Bottom Drawer Estilo Uber / 99 Pop)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .shadow(16.dp, RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp), spotColor = Color.Black),
            shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp),
            color = BgSurface.copy(alpha = 0.98f),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Pílula / Handle de arraste superior
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(4.dp)
                        .background(Color(0xFF8899A6).copy(alpha = 0.4f), CircleShape)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Card do Encontro Selecionado / Focado
                selectedSonarEvent?.let { activeEvent ->
                    val isInGeofence = activeEvent.distanceKm <= 0.8
                    val walkMins = (activeEvent.distanceKm * 12).toInt().coerceAtLeast(3)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BgSecondary, RoundedCornerShape(18.dp))
                            .border(
                                1.2.dp,
                                if (isInGeofence) ColorBurntOrange else BorderWarm,
                                RoundedCornerShape(18.dp)
                            )
                            .clickable { eventForDetails = activeEvent }
                            .padding(14.dp)
                    ) {
                        // Linha Superior: Categoria, Distância/Tempo e Nota
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(9999.dp),
                                    color = ColorTealLight,
                                    border = androidx.compose.foundation.BorderStroke(0.8.dp, ColorTealBorder)
                                ) {
                                    Text(
                                        text = activeEvent.category.label,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ColorTeal,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                if (isInGeofence) {
                                    Surface(
                                        shape = RoundedCornerShape(9999.dp),
                                        color = ColorBurntOrangeLight,
                                        border = androidx.compose.foundation.BorderStroke(0.8.dp, ColorBurntOrange.copy(alpha = 0.5f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Box(modifier = Modifier.size(6.dp).background(ColorBurntOrange, CircleShape))
                                            Text(
                                                text = "No Perímetro!",
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = ColorBurntOrangeHover
                                            )
                                        }
                                    }
                                } else {
                                    Text(
                                        text = "${activeEvent.distanceKm} km • ${walkMins} min a pé",
                                        fontSize = 11.5.sp,
                                        color = TextMuted,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            // Avaliação
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                ObsidianPrismRatingIcon(isFilled = true, size = 12.dp, tint = ColorMustard)
                                Text(
                                    text = "%.1f".format(activeEvent.ratingAvg),
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ColorMustardHover
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Título do Encontro
                        Text(
                            text = activeEvent.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorDarkObsidian,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Endereço / Local e Tribo
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    tint = ColorBurntOrange,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = activeEvent.locationName,
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Groups,
                                    contentDescription = null,
                                    tint = ColorTeal,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "${activeEvent.attendeesCount} confirmados",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ColorTeal
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Linha de Ações (Estilo Uber: Rota / Como Chegar e Check-in com Ícone Exclusivo SEM TEXTO)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isInGeofence) {
                                // Ação de Presença acionada puramente pelo Ícone Exclusivo de Check-in (SEM TEXTO)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    ObsidianCheckInIconTrigger(
                                        onClick = { onCheckInClick(activeEvent) },
                                        size = 46.dp,
                                        iconSize = 24.dp,
                                        containerColor = ColorBurntOrange,
                                        isPulsing = true,
                                        contentDescription = "Validar Presença Física no Encontro",
                                        modifier = Modifier.testTag("geofence_instant_checkin_btn")
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(9999.dp),
                                        color = ColorMustardLight,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, ColorMustard.copy(alpha = 0.4f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Bolt,
                                                contentDescription = null,
                                                tint = ColorMustardHover,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Text(
                                                text = "+50 OQ",
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = ColorMustardHover
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.weight(1f))
                            } else {
                                // Fora do perímetro: botão de Como Chegar / Ver Rota
                                Button(
                                    onClick = {
                                        isRouteActive = true
                                        eventForRouteGuidance = activeEvent
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ColorTeal,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp)
                                        .testTag("route_guidance_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Navigation,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Como Chegar (${walkMins} min)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Botão Secundário: Ver Encontro
                            OutlinedButton(
                                onClick = { eventForDetails = activeEvent },
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorDarkObsidian),
                                modifier = Modifier.height(42.dp)
                            ) {
                                Text(
                                    text = "Ver Encontro",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Carrossel Horizontal de Outros Encontros Próximos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Outros Encontros Próximos (${filteredEvents.size})",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorDarkObsidian
                    )
                    Text(
                        text = "Ver Feed Completo >",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorTeal,
                        modifier = Modifier
                            .clickable { onNavigateToEvents() }
                            .testTag("shortcut_to_events_tab")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredEvents) { ev ->
                        val isCurrent = ev.id == selectedSonarEvent?.id
                        Surface(
                            modifier = Modifier
                                .width(180.dp)
                                .clickable {
                                    selectedSonarEvent = ev
                                    isRouteActive = true
                                },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isCurrent) ColorTealLight else BgSecondary,
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isCurrent) 1.5.dp else 1.dp,
                                color = if (isCurrent) ColorTeal else BorderWarm
                            )
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = ev.category.label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCurrent) ColorTeal else TextMuted
                                    )
                                    Text(
                                        text = "${ev.distanceKm} km",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ColorDarkObsidian
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = ev.title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ColorDarkObsidian,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = ColorMustard,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        text = "${ev.ratingAvg}",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ColorDarkObsidian
                                    )
                                    Text(text = "•", fontSize = 10.5.sp, color = TextMuted)
                                    Icon(
                                        imageVector = Icons.Default.Groups,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        text = "${ev.attendeesCount}",
                                        fontSize = 10.5.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal da Página Completa do Evento
    eventForDetails?.let { ev ->
        val currentEvent = events.find { it.id == ev.id } ?: ev
        val isOwner = currentEvent.isUserHost || currentEvent.hostId == (currentUser?.id ?: "user_me")
        EventDetailsDialog(
            event = currentEvent,
            isOwner = isOwner,
            onDismiss = { eventForDetails = null },
            onDirectCheckIn = {
                onCheckInClick(currentEvent)
                eventForDetails = null
            },
            onOpenRoute = {
                eventForRouteGuidance = currentEvent
                eventForDetails = null
            },
            onLikeClick = { /* Like */ },
            onRateClick = {},
            onOpenStationModal = {
                eventForStation = currentEvent
                eventForDetails = null
            }
        )
    }

    // Modal de Como Chegar & Guia de Presença
    eventForRouteGuidance?.let { ev ->
        RouteGuidanceDialog(
            event = ev,
            onDismiss = { eventForRouteGuidance = null },
            onDirectCheckIn = {
                onCheckInClick(ev)
                eventForRouteGuidance = null
            }
        )
    }

    // Modal da Estação de Check-in (para inspecionar totem físico)
    eventForStation?.let { ev ->
        CheckInStationDialog(
            event = ev,
            onDismiss = { eventForStation = null }
        )
    }
}

/**
 * Card Chamativo: Participante chegou ao Encontro!
 */
@Composable
fun ParticipantInGeofenceCard(
    event: SocialEvent,
    onCheckInClick: () -> Unit,
    onOpenRoute: () -> Unit,
    onOpenDetails: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenDetails() }
            .shadow(6.dp, RoundedCornerShape(22.dp), spotColor = Color(0x33D87A56)),
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFF161F20),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, ColorBurntOrange)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Badge de Chegada ao Evento
                Surface(
                    shape = RoundedCornerShape(9999.dp),
                    color = ColorBurntOrange.copy(alpha = 0.25f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ColorBurntOrange)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(ColorBurntOrange, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Celebration,
                            contentDescription = null,
                            tint = ColorBurntOrangeLight,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "VOCÊ CHEGOU AO ENCONTRO!",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = ColorBurntOrangeLight,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "A 45m • Ao vivo",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorMustard
                    )
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = ColorMustard,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = event.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = event.locationName,
                    fontSize = 13.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Presença social da tribo no local
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .background(Color(0xFF1F2B2C), RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row {
                    listOf(Icons.Default.Bolt, Icons.Default.Headphones, Icons.Default.AutoAwesome).forEachIndexed { idx, iconVector ->
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .offset(x = (-idx * 5).dp)
                                .clip(CircleShape)
                                .background(if (idx == 0) ColorTeal else ColorBurntOrange)
                                .border(1.dp, Color(0xFF161F20), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = iconVector,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(11.dp)
                            )
                        }
                    }
                }
                Text(
                    text = "Beatriz, Carlos e mais 14 da sua rede estão aqui",
                    fontSize = 11.5.sp,
                    color = ColorTealLight,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ação de Presença acionada puramente pelo Totem com identidade exclusiva (SEM TEXTO)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ObsidianCheckInIconTrigger(
                        onClick = onCheckInClick,
                        size = 52.dp,
                        iconSize = 28.dp,
                        containerColor = ColorBurntOrange,
                        isPulsing = true,
                        contentDescription = "Validar Presença Física",
                        modifier = Modifier.testTag("geofence_instant_checkin_btn")
                    )

                    Surface(
                        shape = RoundedCornerShape(9999.dp),
                        color = Color(0xFF232D2E),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF354446))
                    ) {
                        Text(
                            text = "+50 OQ",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorMustard,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Botão Secundário: Ponto de Encontro
                OutlinedButton(
                    onClick = onOpenRoute,
                    shape = RoundedCornerShape(9999.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ColorTeal.copy(alpha = 0.6f)),
                    modifier = Modifier.height(42.dp)
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = null, tint = ColorTealLight, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Ponto de Encontro",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

data class TribeFriendStory(
    val id: String,
    val name: String,
    val handle: String,
    val avatarIcon: ImageVector,
    val eventTitle: String,
    val statusText: String,
    val isLive: Boolean = true,
    val accentColor: Color = ColorBurntOrange
)

/**
 * Seção Social da Tribo: Amigos nos Encontros & Vibe da Comunidade
 * Substitui o formato de dashboard corporativo por um feed dinâmico de rede social
 */
@Composable
fun TribeSocialPresenceBar(
    onFriendClick: (TribeFriendStory) -> Unit = {}
) {
    val friends = remember {
        listOf(
            TribeFriendStory("f_1", "Beatriz", "@bia.lima", Icons.Default.Bolt, "Noite Eletrônica", "Na pista", true, ColorTeal),
            TribeFriendStory("f_2", "Carlos", "@kadu_sp", Icons.Default.Headphones, "Sessão Acústica", "Chegando", true, ColorBurntOrange),
            TribeFriendStory("f_3", "Juliana", "@ju.costa", Icons.Default.AutoAwesome, "Paredão Cultural", "Vem que tá lindo!", true, ColorMustardHover),
            TribeFriendStory("f_4", "Rodrigo", "@rodrigo", Icons.Default.RocketLaunch, "Summit IA", "Troca boa", true, ColorTeal),
            TribeFriendStory("f_5", "Camila", "@camila", Icons.Default.WbSunny, "Rooftop Sunset", "Pôr do sol", true, ColorBurntOrange),
            TribeFriendStory("f_6", "Lucas", "@lucas", Icons.Default.LocalBar, "Craft Beer Pub", "Mesa 4", false, ColorDarkObsidian)
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Cabeçalho da Galera
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sua Galera nos Encontros",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = ColorDarkObsidian
            )

            Surface(
                shape = RoundedCornerShape(9999.dp),
                color = ColorBurntOrangeLight,
                border = androidx.compose.foundation.BorderStroke(1.dp, ColorBurntOrange.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(modifier = Modifier.size(6.dp).background(ColorBurntOrange, CircleShape))
                    Text(
                        text = "14 amigos ativos hoje",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorBurntOrangeHover
                    )
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = ColorBurntOrangeHover,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }

        // Carrossel de Stories de Amigos na Pista
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            // Card "Seu Momento"
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { /* Abre status */ }
                        .width(68.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(BgSecondary)
                            .border(1.5.dp, BorderWarm, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = ColorTeal,
                            modifier = Modifier.size(24.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .align(Alignment.BottomEnd)
                                .background(ColorTeal, CircleShape)
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Seu Status",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ColorDarkObsidian,
                        maxLines = 1
                    )
                    Text(
                        text = "Compartilhar",
                        fontSize = 9.5.sp,
                        color = TextMuted,
                        maxLines = 1
                    )
                }
            }

            // Amigos da Tribo
            items(friends) { friend ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onFriendClick(friend) }
                        .width(68.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(friend.accentColor.copy(alpha = 0.12f))
                            .border(
                                width = 2.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(friend.accentColor, ColorMustard)
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = friend.avatarIcon,
                            contentDescription = null,
                            tint = friend.accentColor,
                            modifier = Modifier.size(24.dp)
                        )
                        if (friend.isLive) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .align(Alignment.BottomEnd)
                                    .background(Color(0xFF2ECC71), CircleShape)
                                    .border(2.dp, Color.White, CircleShape)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = friend.name,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorDarkObsidian,
                        maxLines = 1
                    )
                    Text(
                        text = friend.statusText,
                        fontSize = 9.5.sp,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
            }
        }

        // Cartão Social "Vibe da Comunidade / Galera Conectada"
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = BgSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Avatares empilhados
                        Row {
                            listOf(Icons.Default.Bolt, Icons.Default.Headphones, Icons.Default.AutoAwesome, Icons.Default.RocketLaunch).forEachIndexed { idx, iconVector ->
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .offset(x = (-idx * 6).dp)
                                        .clip(CircleShape)
                                        .background(if (idx % 2 == 0) ColorTealLight else ColorBurntOrangeLight)
                                        .border(1.5.dp, BgSurface, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = iconVector,
                                        contentDescription = null,
                                        tint = if (idx % 2 == 0) ColorTeal else ColorBurntOrange,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "+12 amigos confirmados",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorDarkObsidian
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(9999.dp),
                        color = ColorTealLight
                    ) {
                        Text(
                            text = "Rolando agora",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorTeal,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ParticipantFilterChip(
    label: String,
    icon: ImageVector? = null,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(9999.dp),
        color = if (isSelected) ColorTeal else BgSurface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) ColorTeal else BorderWarm
        ),
        modifier = Modifier
            .clickable { onClick() }
            .shadow(if (isSelected) 3.dp else 1.dp, RoundedCornerShape(9999.dp), spotColor = Color(0x2400796B))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else TextSecondary,
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else TextSecondary
            )
        }
    }
}

/**
 * Card de Encontro Visual-First com Imagem em Máximo Destaque
 * Textos longos e descrições completas são lidos ao clicar no card e abrir a Página do Evento.
 */
@Composable
fun ParticipantEventCard(
    event: SocialEvent,
    isOwner: Boolean = false,
    onAddMedia: (List<String>) -> Unit = {},
    onManageMedia: () -> Unit = {},
    onDirectCheckIn: () -> Unit,
    onOpenRoute: () -> Unit,
    onLikeClick: () -> Unit,
    onRateClick: () -> Unit,
    onOpenStationModal: () -> Unit,
    onOpenEventDetails: () -> Unit = {}
) {
    val isInsideGeofence = event.distanceKm <= 0.8
    val walkMinutes = (event.distanceKm * 12).toInt().coerceAtLeast(2)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenEventDetails() }
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(22.dp),
                spotColor = Color(0x2E0B0C10)
            )
            .testTag("event_card_${event.id}"),
        shape = RoundedCornerShape(22.dp),
        color = BgSurface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isInsideGeofence) ColorBurntOrange.copy(alpha = 0.6f) else BorderWarm
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 1. Imagem Principal / Carretel com Altura de 220dp em Máximo Destaque Visual
            Box(modifier = Modifier.fillMaxWidth()) {
                EventMediaReelBanner(
                    event = event,
                    isOwner = isOwner,
                    onAddMedia = onAddMedia,
                    onManageMedia = onManageMedia,
                    height = 220.dp
                ) {
                    // Overlays na Foto: Categoria e Proximidade no Topo Esquerdo
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(9999.dp),
                            color = Color.Black.copy(alpha = 0.65f),
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, Color.White.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = event.category.label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        if (isInsideGeofence) {
                            Surface(
                                shape = RoundedCornerShape(9999.dp),
                                color = ColorBurntOrange,
                                border = androidx.compose.foundation.BorderStroke(0.8.dp, Color.White.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(modifier = Modifier.size(6.dp).background(Color.White, CircleShape))
                                    Text(
                                        text = "No Local",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(9999.dp),
                                color = Color.Black.copy(alpha = 0.65f),
                                border = androidx.compose.foundation.BorderStroke(0.8.dp, Color.White.copy(alpha = 0.25f))
                            ) {
                                Text(
                                    text = "${event.distanceKm} km",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ColorTealLight,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    // Avaliação no Topo Direito
                    Surface(
                        shape = RoundedCornerShape(9999.dp),
                        color = Color.Black.copy(alpha = 0.65f),
                        border = androidx.compose.foundation.BorderStroke(0.8.dp, Color.White.copy(alpha = 0.25f)),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            ObsidianPrismRatingIcon(isFilled = true, size = 12.dp, tint = ColorMustard)
                            Text(
                                text = "%.1f".format(event.ratingAvg),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorMustard
                            )
                        }
                    }

                    // Data e Horário no Rodapé Esquerdo da Foto (sem redundância no corpo do card)
                    Surface(
                        shape = RoundedCornerShape(9999.dp),
                        color = Color.Black.copy(alpha = 0.65f),
                        border = androidx.compose.foundation.BorderStroke(0.8.dp, Color.White.copy(alpha = 0.25f)),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "${event.dateDisplay} às ${event.timeDisplay}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // 2. Dados Sintéticos e Ações Diretas (Largura total para o título, sem data redundante)
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = event.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorDarkObsidian,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = ColorBurntOrange,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = event.locationName,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ColorDarkObsidian,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = null,
                            tint = ColorTeal,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "${event.attendeesCount} na Tribo",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ColorTeal,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Barra de Ações Rápidas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Curtir (Ressonância)
                    ObsidianResonateButton(
                        isResonated = event.isLiked,
                        count = event.likesCount,
                        onClick = onLikeClick,
                        modifier = Modifier.testTag("resonate_btn_${event.id}")
                    )

                    // Ação de Presença / Rota (Botão de Presença estritamente com Ícone Exclusivo, SEM TEXTO)
                    if (isInsideGeofence) {
                        ObsidianCheckInIconTrigger(
                            onClick = onDirectCheckIn,
                            size = 42.dp,
                            iconSize = 22.dp,
                            containerColor = ColorBurntOrange,
                            isPulsing = true,
                            contentDescription = "Validar Presença Física",
                            modifier = Modifier.testTag("participant_checkin_btn_${event.id}")
                        )
                    } else {
                        OutlinedButton(
                            onClick = onOpenRoute,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ColorTeal),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorTeal),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            modifier = Modifier
                                .height(42.dp)
                                .testTag("participant_route_btn_${event.id}")
                        ) {
                            Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Rota (${walkMinutes} min)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Botão para Abrir a Página Completa do Evento
                    Surface(
                        onClick = onOpenEventDetails,
                        shape = RoundedCornerShape(12.dp),
                        color = BgSecondary,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm),
                        modifier = Modifier
                            .height(42.dp)
                            .testTag("open_event_details_btn_${event.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Ver Encontro",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorDarkObsidian
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowForwardIos,
                                contentDescription = "Ver Página do Evento",
                                tint = ColorDarkObsidian,
                                modifier = Modifier.size(11.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Card Discreto no Rodapé para quem também for anfitrião
 */
@Composable
fun ParticipantHostInviteCard(
    onCreateEventClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = BgSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Quer reunir sua Tribo?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = ColorDarkObsidian
                )
                Text(
                    text = "Crie seu próprio encontro presencial com validação antifraude.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Button(
                onClick = onCreateEventClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorBurntOrange,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(9999.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text("+ Criar Encontro", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ObsidianDesignFooter() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = BgSecondary,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ObsidianLogoEmblem(size = 22.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Obsidian • A Rede Social da Presença Real",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = ColorDarkObsidian
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Desconecte do virtual. Presença física validada por Geofence & BLE.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = TextMuted
            )
        }
    }
}
