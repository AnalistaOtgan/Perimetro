package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.EventDetailsDialog
import com.example.ui.components.EventMediaReelBanner
import com.example.ui.components.HelpCenterDialog
import com.example.ui.components.ManageEventMediaDialog
import com.example.ui.components.ObsidianCheckInIconTrigger
import com.example.ui.components.ObsidianLogoEmblem
import com.example.ui.components.ObsidianPrismRatingIcon
import com.example.ui.components.ObsidianRatingIconTrigger
import com.example.ui.components.ObsidianResonateButton
import com.example.ui.components.OrganicCirclesBackground
import com.example.ui.theme.*

enum class EventListFilterMode(val label: String) {
    TODOS("Todos"),
    MAIS_PROXIMOS("🚶 Mais Próximos (< 1km)"),
    BOMBANDO("🔥 Bombando na Tribo"),
    COM_FOTOS("📸 Com Carretel de Fotos")
}

/**
 * Tela Exclusiva de Eventos: Feed completo de encontros e rolês da comunidade
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    events: List<SocialEvent>,
    currentUserTier: UserTier = UserTier.PRATA,
    currentUser: UserProfile? = null,
    onCheckInClick: (SocialEvent) -> Unit,
    onCreateEventClick: () -> Unit,
    onToggleLike: (String) -> Unit,
    onSubmitRating: (String, Int, List<String>) -> Unit,
    onAddMediaToEvent: (String, List<String>) -> Unit = { _, _ -> },
    onRemoveMediaFromEvent: (String, String) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<EventCategory?>(null) }
    var selectedFilterMode by remember { mutableStateOf(EventListFilterMode.TODOS) }

    // Estados para Modais
    var eventForDetails by remember { mutableStateOf<SocialEvent?>(null) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var eventForRating by remember { mutableStateOf<SocialEvent?>(null) }
    var eventForRouteGuidance by remember { mutableStateOf<SocialEvent?>(null) }
    var eventForStation by remember { mutableStateOf<SocialEvent?>(null) }
    var eventForManagingMedia by remember { mutableStateOf<SocialEvent?>(null) }

    // Filtragem dos eventos
    val filteredEvents = remember(events, searchQuery, selectedCategory, selectedFilterMode) {
        events.filter { event ->
            val matchesQuery = searchQuery.isBlank() ||
                    event.title.contains(searchQuery, ignoreCase = true) ||
                    event.description.contains(searchQuery, ignoreCase = true) ||
                    event.locationName.contains(searchQuery, ignoreCase = true) ||
                    event.hostName.contains(searchQuery, ignoreCase = true)

            val matchesCategory = selectedCategory == null || event.category == selectedCategory

            val matchesMode = when (selectedFilterMode) {
                EventListFilterMode.TODOS -> true
                EventListFilterMode.MAIS_PROXIMOS -> event.distanceKm <= 1.5
                EventListFilterMode.BOMBANDO -> event.attendeesCount >= 10 || event.likesCount >= 15
                EventListFilterMode.COM_FOTOS -> event.mediaReel.isNotEmpty()
            }

            matchesQuery && matchesCategory && matchesMode
        }.sortedBy { it.distanceKm }
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = BgSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Logo & Título da Tela
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ObsidianLogoEmblem(size = 32.dp)
                            Column {
                                Text(
                                    text = "Feed de Eventos",
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ColorDarkObsidian,
                                    letterSpacing = (-0.3).sp
                                )
                                Text(
                                    text = "${filteredEvents.size} rolês confirmados em SP ✨",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        // Right: Ações Rápidas (Ajuda + Criar Encontro)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = { showHelpDialog = true },
                                modifier = Modifier.testTag("help_icon_btn_events")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HelpOutline,
                                    contentDescription = "Central de Ajuda",
                                    tint = ColorTeal,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Button(
                                onClick = onCreateEventClick,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ColorBurntOrange,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(9999.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 7.dp),
                                modifier = Modifier.testTag("create_event_btn_top")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Text(
                                        text = "Criar Rolê",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgCanvas)
        ) {
            OrganicCirclesBackground()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .testTag("events_feed_list"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Barra de Busca
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                text = "Buscar vibe, artista, encontro ou local...",
                                color = TextMuted,
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = ColorTeal,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Limpar",
                                        tint = TextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(16.dp), spotColor = Color(0x140B0C10))
                            .testTag("events_search_field"),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = BgSurface,
                            unfocusedContainerColor = BgSurface,
                            focusedBorderColor = ColorTeal,
                            unfocusedBorderColor = BorderWarm
                        ),
                        singleLine = true
                    )
                }

                // 2. Filtros Rápidos
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        items(EventListFilterMode.values()) { mode ->
                            val isSelected = selectedFilterMode == mode
                            Surface(
                                shape = RoundedCornerShape(9999.dp),
                                color = if (isSelected) ColorTeal else BgSurface,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) ColorTeal else BorderWarm
                                ),
                                modifier = Modifier
                                    .clickable { selectedFilterMode = mode }
                                    .shadow(if (isSelected) 3.dp else 1.dp, RoundedCornerShape(9999.dp), spotColor = Color(0x2400796B))
                            ) {
                                Text(
                                    text = mode.label,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else TextSecondary
                                )
                            }
                        }
                    }
                }

                // 3. Chips de Categorias
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(9999.dp),
                                color = if (selectedCategory == null) ColorBurntOrange else BgSurface,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (selectedCategory == null) ColorBurntOrange else BorderWarm
                                ),
                                modifier = Modifier
                                    .clickable { selectedCategory = null }
                                    .shadow(1.dp, RoundedCornerShape(9999.dp))
                            ) {
                                Text(
                                    text = "Todas Categorias",
                                    modifier = Modifier.padding(horizontal = 13.dp, vertical = 6.dp),
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedCategory == null) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedCategory == null) Color.White else TextSecondary
                                )
                            }
                        }
                        items(EventCategory.values()) { cat ->
                            val isSelected = selectedCategory == cat
                            Surface(
                                shape = RoundedCornerShape(9999.dp),
                                color = if (isSelected) ColorBurntOrange else BgSurface,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) ColorBurntOrange else BorderWarm
                                ),
                                modifier = Modifier
                                    .clickable {
                                        selectedCategory = if (selectedCategory == cat) null else cat
                                    }
                                    .shadow(1.dp, RoundedCornerShape(9999.dp))
                            ) {
                                Text(
                                    text = cat.label,
                                    modifier = Modifier.padding(horizontal = 13.dp, vertical = 6.dp),
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else TextSecondary
                                )
                            }
                        }
                    }
                }

                // 4. Cabeçalho da Lista
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Rolês Disponíveis",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorDarkObsidian
                            )
                            Text(
                                text = "${filteredEvents.size} encontros encontrados",
                                fontSize = 12.5.sp,
                                color = TextSecondary
                            )
                        }

                        // Badge de Ordenação
                        Surface(
                            shape = RoundedCornerShape(9999.dp),
                            color = BgSecondary,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.NearMe, contentDescription = null, tint = ColorTeal, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Mais próximos", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = ColorDarkObsidian)
                            }
                        }
                    }
                }

                // 5. Lista de Eventos ou Estado Vazio
                if (filteredEvents.isEmpty()) {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            color = BgSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "🔍", fontSize = 42.sp)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Nenhum encontro encontrado",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = ColorDarkObsidian
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Tente alterar os termos de busca ou limpar o filtro.",
                                    fontSize = 13.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        searchQuery = ""
                                        selectedCategory = null
                                        selectedFilterMode = EventListFilterMode.TODOS
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ColorTeal),
                                    shape = RoundedCornerShape(9999.dp)
                                ) {
                                    Text("Resetar Filtros", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    items(filteredEvents, key = { it.id }) { event ->
                        val isOwner = event.isUserHost || event.hostId == (currentUser?.id ?: "user_me")
                        ParticipantEventCard(
                            event = event,
                            isOwner = isOwner,
                            onAddMedia = { uris -> onAddMediaToEvent(event.id, uris) },
                            onManageMedia = { eventForManagingMedia = event },
                            onDirectCheckIn = { onCheckInClick(event) },
                            onOpenRoute = { eventForRouteGuidance = event },
                            onLikeClick = { onToggleLike(event.id) },
                            onRateClick = { eventForRating = event },
                            onOpenStationModal = { eventForStation = event },
                            onOpenEventDetails = { eventForDetails = event }
                        )
                    }
                }

                // 6. Card Convidando para Criar Encontro
                item {
                    ParticipantHostInviteCard(onCreateEventClick = onCreateEventClick)
                }

                // 7. Rodapé Oficial da Comunidade
                item {
                    ObsidianDesignFooter()
                }
            }
        }
    }

    // Modal da Página Completa do Evento (visual-first)
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
            onLikeClick = { onToggleLike(currentEvent.id) },
            onRateClick = {
                eventForRating = currentEvent
                eventForDetails = null
            },
            onOpenStationModal = {
                eventForStation = currentEvent
                eventForDetails = null
            },
            onManageMedia = {
                eventForManagingMedia = currentEvent
                eventForDetails = null
            },
            onAddMedia = { uris ->
                onAddMediaToEvent(currentEvent.id, uris)
            }
        )
    }

    // Modal da Central de Ajuda
    if (showHelpDialog) {
        HelpCenterDialog(
            onDismiss = { showHelpDialog = false }
        )
    }

    // Modal de Gerenciamento do Carretel de Mídia (Exclusivo para o Anfitrião / Dono)
    eventForManagingMedia?.let { ev ->
        val currentEvent = events.find { it.id == ev.id } ?: ev
        ManageEventMediaDialog(
            event = currentEvent,
            onDismiss = { eventForManagingMedia = null },
            onAddMedia = { uris ->
                onAddMediaToEvent(currentEvent.id, uris)
            },
            onRemoveMedia = { mediaUri ->
                onRemoveMediaFromEvent(currentEvent.id, mediaUri)
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

    // Modal de Feedback do Rolê
    eventForRating?.let { ev ->
        EventFeedbackDialog(
            event = ev,
            onDismiss = { eventForRating = null },
            onSubmitFeedback = { stars, tags ->
                onSubmitRating(ev.id, stars, tags)
                eventForRating = null
            }
        )
    }
}
