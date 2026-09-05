package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ObsidianRepository
import com.example.model.*
import com.example.ui.components.ObsidianEventsNexusIcon
import com.example.ui.components.ObsidianStationPortalIcon
import com.example.ui.screens.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val repository = ObsidianRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ObsidianApp(repository = repository)
                }
            }
        }
    }
}

enum class NavigationDest(val title: String) {
    RADAR("Radar"),
    EVENTS("Eventos"),
    CHECKIN("Presença"),
    BADGES("Tribo"),
    PROFILE("Perfil")
}

@Composable
fun ObsidianApp(repository: ObsidianRepository) {
    var isLoggedIn by remember { mutableStateOf(false) }
    var currentDest by remember { mutableStateOf(NavigationDest.RADAR) }

    val user by repository.currentUser.collectAsStateWithLifecycle()
    val events by repository.events.collectAsStateWithLifecycle()
    val badges by repository.badges.collectAsStateWithLifecycle()
    val connections by repository.connections.collectAsStateWithLifecycle()

    var isCreatingEvent by remember { mutableStateOf(false) }
    var eventForFeedback by remember { mutableStateOf<SocialEvent?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    if (!isLoggedIn) {
        LoginScreen(onLoginSuccess = { isLoggedIn = true })
    } else if (isCreatingEvent) {
        // PÁGINA EXCLUSIVA DE CRIAÇÃO DE ENCONTROS (Substitui modal por página dedicada no design Obsidian)
        CreateEventScreen(
            onNavigateBack = { isCreatingEvent = false },
            onCreateEvent = { title, desc, cat, loc, vis, method, mediaList ->
                repository.createEvent(title, desc, cat, loc, vis, method, mediaList)
                isCreatingEvent = false
                currentDest = NavigationDest.EVENTS
                coroutineScope.launch {
                    val mediaMsg = if (mediaList.isNotEmpty()) " com ${mediaList.size} foto(s) no carretel" else ""
                    snackbarHostState.showSnackbar("Encontro \"$title\" publicado no Feed de Eventos$mediaMsg!")
                }
            }
        )
    } else {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                NavigationBar(
                    containerColor = BgSurface,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .border(1.dp, BorderLight)
                        .testTag("main_navigation_bar")
                ) {
                    NavigationBarItem(
                        selected = currentDest == NavigationDest.RADAR,
                        onClick = { currentDest = NavigationDest.RADAR },
                        icon = {
                            Icon(
                                if (currentDest == NavigationDest.RADAR) Icons.Filled.Explore else Icons.Outlined.Explore,
                                contentDescription = "Radar"
                            )
                        },
                        label = { Text("Radar", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ColorTeal,
                            selectedTextColor = ColorTeal,
                            indicatorColor = ColorTealLight,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_radar")
                    )

                    NavigationBarItem(
                        selected = currentDest == NavigationDest.EVENTS,
                        onClick = { currentDest = NavigationDest.EVENTS },
                        icon = {
                            ObsidianEventsNexusIcon(
                                selected = currentDest == NavigationDest.EVENTS,
                                tint = if (currentDest == NavigationDest.EVENTS) ColorTeal else TextMuted,
                                size = 24.dp,
                                animated = currentDest == NavigationDest.EVENTS
                            )
                        },
                        label = { Text("Eventos", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ColorTeal,
                            selectedTextColor = ColorTeal,
                            indicatorColor = ColorTealLight,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_events")
                    )

                    NavigationBarItem(
                        selected = currentDest == NavigationDest.CHECKIN,
                        onClick = { currentDest = NavigationDest.CHECKIN },
                        icon = {
                            ObsidianStationPortalIcon(
                                size = 24.dp,
                                tint = if (currentDest == NavigationDest.CHECKIN) ColorTeal else TextMuted,
                                animated = currentDest == NavigationDest.CHECKIN
                            )
                        },
                        label = { Text("Presença", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ColorTeal,
                            selectedTextColor = ColorTeal,
                            indicatorColor = ColorTealLight,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_checkin")
                    )

                    NavigationBarItem(
                        selected = currentDest == NavigationDest.BADGES,
                        onClick = { currentDest = NavigationDest.BADGES },
                        icon = {
                            Icon(
                                if (currentDest == NavigationDest.BADGES) Icons.Filled.MilitaryTech else Icons.Outlined.MilitaryTech,
                                contentDescription = "Tribo"
                            )
                        },
                        label = { Text("Tribo", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ColorTeal,
                            selectedTextColor = ColorTeal,
                            indicatorColor = ColorTealLight,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_badges")
                    )

                    NavigationBarItem(
                        selected = currentDest == NavigationDest.PROFILE,
                        onClick = { currentDest = NavigationDest.PROFILE },
                        icon = {
                            Icon(
                                if (currentDest == NavigationDest.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                                contentDescription = "Perfil"
                            )
                        },
                        label = { Text("Perfil", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ColorTeal,
                            selectedTextColor = ColorTeal,
                            indicatorColor = ColorTealLight,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_profile")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentDest) {
                    NavigationDest.RADAR -> {
                        RadarScreen(
                            events = events,
                            currentUserTier = user.tier,
                            currentUser = user,
                            onSelectEvent = { ev ->
                                currentDest = NavigationDest.CHECKIN
                            },
                            onCheckInClick = { ev ->
                                repository.performCheckIn(ev.id, ev.checkInMethod)
                                currentDest = NavigationDest.CHECKIN
                            },
                            onCreateEventClick = { isCreatingEvent = true },
                            onNavigateToEvents = {
                                currentDest = NavigationDest.EVENTS
                            }
                        )
                    }

                    NavigationDest.EVENTS -> {
                        EventsScreen(
                            events = events,
                            currentUserTier = user.tier,
                            currentUser = user,
                            onCheckInClick = { ev ->
                                repository.performCheckIn(ev.id, ev.checkInMethod)
                                currentDest = NavigationDest.CHECKIN
                            },
                            onCreateEventClick = { isCreatingEvent = true },
                            onToggleLike = { eventId ->
                                repository.toggleLikeEvent(eventId)
                            },
                            onSubmitRating = { eventId, stars, tags ->
                                repository.submitEventRating(eventId, stars, tags)
                            },
                            onAddMediaToEvent = { eventId, mediaUris ->
                                repository.addMediaToEvent(eventId, mediaUris)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("${mediaUris.size} foto(s) adicionada(s) ao carretel do encontro!")
                                }
                            },
                            onRemoveMediaFromEvent = { eventId, mediaUri ->
                                repository.removeMediaFromEvent(eventId, mediaUri)
                            }
                        )
                    }

                    NavigationDest.CHECKIN -> {
                        CheckInScreen(
                            events = events,
                            onPerformCheckIn = { ev, method ->
                                repository.performCheckIn(ev.id, method)
                            },
                            onOpenFeedback = { ev ->
                                eventForFeedback = ev
                            }
                        )
                    }

                    NavigationDest.BADGES -> {
                        BadgesScreen(
                            badges = badges,
                            connections = connections,
                            onAwardBadgeToPeer = { recipient, badge, tier ->
                                repository.awardBadgeToPeer(recipient, badge, tier)
                            }
                        )
                    }

                    NavigationDest.PROFILE -> {
                        ProfileScreen(
                            user = user,
                            onCreateEventClick = { isCreatingEvent = true },
                            onLogoutClick = { isLoggedIn = false }
                        )
                    }
                }
            }

            eventForFeedback?.let { ev ->
                EventFeedbackDialog(
                    event = ev,
                    onDismiss = { eventForFeedback = null },
                    onSubmitFeedback = { stars, tags ->
                        repository.submitEventFeedback(ev.id, stars, tags)
                        eventForFeedback = null
                    }
                )
            }
        }
    }
}
