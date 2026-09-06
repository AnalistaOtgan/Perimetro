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
    MAIS_PROXIMOS("Mais Próximos (< 1km)"),
    BOMBANDO("Bombando na Tribo"),
    COM_FOTOS("Com Carretel de Fotos")
}

data class SearchTag(val keyword: String, val label: String, val category: EventCategory? = null, val mode: EventListFilterMode? = null)

/**
 * Tela Exclusiva de Eventos: Feed completo de encontros da comunidade
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

    val searchTags = remember {
        val tags = mutableListOf<SearchTag>()
        tags.add(SearchTag("@proximos", "Mais Próximos (<1km)", mode = EventListFilterMode.MAIS_PROXIMOS))
        tags.add(SearchTag("@bombando", "Bombando na Tribo", mode = EventListFilterMode.BOMBANDO))
        tags.add(SearchTag("@fotos", "Com Carretel de Fotos", mode = EventListFilterMode.COM_FOTOS))
        
        EventCategory.values().forEach { cat ->
            val tagWord = "@" + cat.name.lowercase().replace("_", "")
            tags.add(SearchTag(tagWord, cat.label, category = cat))
        }
        tags
    }

    // Estados para Modais
    var eventForDetails by remember { mutableStateOf<SocialEvent?>(null) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var eventForRating by remember { mutableStateOf<SocialEvent?>(null) }
    var eventForRouteGuidance by remember { mutableStateOf<SocialEvent?>(null) }
    var eventForStation by remember { mutableStateOf<SocialEvent?>(null) }
    var eventForManagingMedia by remember { mutableStateOf<SocialEvent?>(null) }

    // Filtragem dos eventos
    val filteredEvents = remember(events, searchQuery) {
        // Parse active tags
        val activeTags = searchTags.filter { searchQuery.contains(it.keyword, ignoreCase = true) }
        
        val activeCategories = activeTags.mapNotNull { it.category }
        val activeModes = activeTags.mapNotNull { it.mode }
        
        // Clean query for text search
        var cleanQuery = searchQuery
        activeTags.forEach { cleanQuery = cleanQuery.replace(it.keyword, "", ignoreCase = true) }
        cleanQuery = cleanQuery.trim()

        events.filter { event ->
            val matchesQuery = cleanQuery.isBlank() ||
                    event.title.contains(cleanQuery, ignoreCase = true) ||
                    event.description.contains(cleanQuery, ignoreCase = true) ||
                    event.locationName.contains(cleanQuery, ignoreCase = true) ||
                    event.hostName.contains(cleanQuery, ignoreCase = true)

            val matchesCategory = activeCategories.isEmpty() || activeCategories.contains(event.category)

            val matchesMode = activeModes.isEmpty() || activeModes.all { mode ->
                when (mode) {
                    EventListFilterMode.TODOS -> true
                    EventListFilterMode.MAIS_PROXIMOS -> event.distanceKm <= 1.5
                    EventListFilterMode.BOMBANDO -> event.attendeesCount >= 10 || event.likesCount >= 15
                    EventListFilterMode.COM_FOTOS -> event.mediaReel.isNotEmpty()
                }
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
                                    text = "${filteredEvents.size} encontros confirmados em SP",
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
                                        text = "Criar Encontro",
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
                // 1. Barra de Busca com suporte a @ tags
                item {
                    val showSuggestions = searchQuery.substringAfterLast(" ").startsWith("@")
                    val currentWord = if (showSuggestions) searchQuery.substringAfterLast(" ") else ""
                    val suggestedTags = if (showSuggestions) {
                        searchTags.filter { it.keyword.startsWith(currentWord, ignoreCase = true) }
                    } else emptyList()

                    Column(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = {
                                Text(
                                    text = "Buscar evento ou filtre com @...",
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

                        // Painel de sugestões inteligente
                        androidx.compose.animation.AnimatedVisibility(visible = showSuggestions && suggestedTags.isNotEmpty()) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .shadow(4.dp, RoundedCornerShape(12.dp), spotColor = Color(0x140B0C10)),
                                shape = RoundedCornerShape(12.dp),
                                color = BgSurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
                            ) {
                                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                    suggestedTags.take(5).forEach { tag ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    val before = searchQuery.substringBeforeLast(" ")
                                                    searchQuery = if (before == searchQuery) {
                                                        tag.keyword + " "
                                                    } else {
                                                        before + " " + tag.keyword + " "
                                                    }
                                                }
                                                .padding(horizontal = 16.dp, vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Tag, 
                                                contentDescription = null, 
                                                tint = ColorTeal, 
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                text = tag.keyword,
                                                fontWeight = FontWeight.Bold,
                                                color = ColorTeal,
                                                modifier = Modifier.width(110.dp)
                                            )
                                            Text(
                                                text = tag.label,
                                                color = TextSecondary,
                                                fontSize = 13.sp
                                            )
                                        }
                                    }
                                }
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
                                text = "Encontros Disponíveis",
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
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    modifier = Modifier.size(42.dp),
                                    tint = TextMuted
                                )
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

    // Modal de Feedback do Encontro
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
