package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

class ObsidianRepository {

    private val _currentUser = MutableStateFlow(
        UserProfile(
            id = "user_me",
            name = "Alex Silva",
            handle = "@alex.obsidian",
            bio = "Amante de música ao vivo, festivais e conexões reais. Sempre no front.",
            avatarEmoji = "⚡",
            tier = UserTier.PRATA,
            oquantumBalance = 248.75,
            badgesCount = 18,
            checkInsCount = 14,
            connectionsCount = 7
        )
    )
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    private val _events = MutableStateFlow<List<SocialEvent>>(
        listOf(
            SocialEvent(
                id = "ev_1",
                title = "Summit IA e Presença Real 2026",
                description = "Encontro presencial exclusivo para fundadores e desenvolvedores explorando IA aplicada, agentes autônomos e validação criptográfica.",
                category = EventCategory.TECNOLOGIA,
                locationName = "Cubo Itaú - 5º Andar (raio: 150m)",
                distanceKm = 0.8,
                dateDisplay = "Hoje",
                timeDisplay = "08:30",
                attendeesCount = 3,
                ratingAvg = 4.9,
                ratingsCount = 18,
                visibilityTier = VisibilityTier.PUBLICO,
                checkInMethod = CheckInMethod.QR_DYNAMIC,
                tags = listOf("IA", "Agentes", "Networking", "Criptografia"),
                hostName = "Obsidian Tech Hub • 🛡️",
                hostId = "obsidian_hub",
                isUserHost = false,
                mediaReel = listOf(
                    "https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=800&q=80",
                    "https://images.unsplash.com/photo-1515187029135-18ee286d815b?w=800&q=80",
                    "https://images.unsplash.com/photo-1475721027785-f74eccf877e2?w=800&q=80"
                ),
                geofenceRadiusMeters = 150,
                bannerTheme = "tech"
            ),
            SocialEvent(
                id = "ev_2",
                title = "Noite dos Rustaceans & Go Hackers",
                description = "Arquitetura distribuída de alta performance, peer-to-peer networking e debate técnico presencial com café e cerveja.",
                category = EventCategory.MEETUP,
                locationName = "Inovabra Habitat - Lounge Principal (raio: 200m)",
                distanceKm = 2.1,
                dateDisplay = "06 de set.",
                timeDisplay = "09:29",
                attendeesCount = 2,
                ratingAvg = 4.8,
                ratingsCount = 12,
                visibilityTier = VisibilityTier.BRONZE_PLUS,
                checkInMethod = CheckInMethod.QR_DYNAMIC,
                tags = listOf("Rust", "Go", "Performance", "Open Source"),
                hostName = "Alex Silva (Você) • 🛡️",
                hostId = "user_me",
                isUserHost = true,
                mediaReel = listOf(
                    "https://images.unsplash.com/photo-1531482615713-2afd69097998?w=800&q=80",
                    "https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=800&q=80"
                ),
                geofenceRadiusMeters = 200,
                bannerTheme = "code"
            ),
            SocialEvent(
                id = "ev_3",
                title = "Noite Eletrônica no Rooftop",
                description = "Encontro sob as estrelas com os melhores DJs underground. Presença real validada por QR rotativo e geofence.",
                category = EventCategory.SHOW,
                locationName = "Terraço Horizonte, Centro",
                distanceKm = 1.4,
                dateDisplay = "Hoje",
                timeDisplay = "22:00 - 04:00",
                attendeesCount = 86,
                ratingAvg = 4.9,
                ratingsCount = 42,
                visibilityTier = VisibilityTier.PUBLICO,
                checkInMethod = CheckInMethod.QR_DYNAMIC,
                tags = listOf("Música", "Bebidas", "Ambiente", "Segurança"),
                hostName = "Coletivo Horizonte • 🛡️",
                hostId = "coletivo_horizonte",
                isUserHost = false,
                mediaReel = listOf(
                    "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=800&q=80",
                    "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=800&q=80"
                ),
                geofenceRadiusMeters = 100,
                bannerTheme = "rooftop"
            ),
            SocialEvent(
                id = "ev_4",
                title = "Sessão Acústica & Craft Beer",
                description = "Ambiente intimista para degustação de cervejas artesanais e conversas autênticas sem telas.",
                category = EventCategory.BAR,
                locationName = "Lounge Malte & Alma",
                distanceKm = 3.2,
                dateDisplay = "Amanhã",
                timeDisplay = "19:30 - 23:30",
                attendeesCount = 38,
                ratingAvg = 4.7,
                ratingsCount = 28,
                visibilityTier = VisibilityTier.BRONZE_PLUS,
                checkInMethod = CheckInMethod.GPS_ONLY,
                tags = listOf("Custo-benefício", "Bebidas", "Som", "Decoração"),
                hostName = "Cervejaria Malte • 🛡️",
                geofenceRadiusMeters = 80,
                bannerTheme = "beer"
            ),
            SocialEvent(
                id = "ev_5",
                title = "Gala Secreto dos Guardiões",
                description = "Encontro exclusivo para membros Ouro e Obsidianas com alta reputação e badges de presença.",
                category = EventCategory.PRIVADO,
                locationName = "Mansão da Falésia (Localização Revelada pós-Checkin)",
                distanceKm = 6.8,
                dateDisplay = "Sábado",
                timeDisplay = "21:00",
                attendeesCount = 24,
                ratingAvg = 5.0,
                ratingsCount = 19,
                visibilityTier = VisibilityTier.OURO_VIP,
                checkInMethod = CheckInMethod.BLE_BEACON,
                tags = listOf("Exclusivo", "Segurança", "Gourmet", "Networking"),
                hostName = "Conselho Obsidian • 🛡️",
                geofenceRadiusMeters = 50,
                bannerTheme = "gala"
            )
        )
    )
    val events: StateFlow<List<SocialEvent>> = _events.asStateFlow()

    private val _badges = MutableStateFlow<List<SocialBadge>>(
        listOf(
            // Música
            SocialBadge("b_1", "Roqueiro", BadgeCategory.MUSICA, "Presente em mais de 5 shows de rock e festivais ao vivo.", "🎸", UserTier.PRATA, 7, 15, true),
            SocialBadge("b_2", "Eletrônico", BadgeCategory.MUSICA, "Vivência em pistas e amanheceres da música eletrônica.", "🎛️", UserTier.BRONZE, 4, 5, true),
            SocialBadge("b_3", "Eclético", BadgeCategory.MUSICA, "Aprecia de jazz ao paredão sem preconceitos sonoros.", "🎧", UserTier.OURO, 28, 40, true),

            // Comportamento
            SocialBadge("b_4", "Animado", BadgeCategory.COMPORTAMENTO, "Irradia energia positiva e anima a roda de presença.", "☀️", UserTier.PRATA, 12, 15, true),
            SocialBadge("b_5", "Respeitoso", BadgeCategory.COMPORTAMENTO, "Postura exemplar de consentimento e cordialidade coletiva.", "🤝", UserTier.OBSIDIAN, 102, 100, true),
            SocialBadge("b_6", "Confiável", BadgeCategory.COMPORTAMENTO, "Presença confirmada e pontualidade nos compromissos da tribo.", "🛡️", UserTier.OURO, 45, 40, true),

            // Frequência
            SocialBadge("b_7", "Coruja", BadgeCategory.FREQUENCIA, "Ativo na vida noturna até o nascer do sol.", "🦉", UserTier.PRATA, 14, 15, false),
            SocialBadge("b_8", "Maratonista", BadgeCategory.FREQUENCIA, "Três ou mais eventos no mesmo fim de semana.", "👟", UserTier.BRONZE, 3, 5, false),
            SocialBadge("b_9", "Consistente", BadgeCategory.FREQUENCIA, "Check-in em eventos toda semana por 3 meses seguidos.", "📅", UserTier.BRONZE, 2, 5, false),

            // Exploração
            SocialBadge("b_10", "Explorador", BadgeCategory.EXPLORACAO, "Desbravou eventos em mais de 5 bairros diferentes.", "🧭", UserTier.PRATA, 11, 15, true),
            SocialBadge("b_11", "Viajante", BadgeCategory.EXPLORACAO, "Validou presença física em eventos de outras cidades.", "✈️", UserTier.BRONZE, 2, 5, false),
            SocialBadge("b_12", "Lenda do Pedaço", BadgeCategory.EXPLORACAO, "Figura carimbada e reconhecida na sua região.", "👑", UserTier.OURO, 38, 40, false),

            // Nicho
            SocialBadge("b_13", "Cervejeiro", BadgeCategory.NICHO, "Frequenta taprooms e degustações de cerveja artesanal.", "🍺", UserTier.BRONZE, 4, 5, false),
            SocialBadge("b_14", "Gourmet", BadgeCategory.NICHO, "Apreciador da gastronomia de rua e bistrôs locais.", "🍽️", UserTier.BRONZE, 3, 5, false),
            SocialBadge("b_15", "Artístico", BadgeCategory.NICHO, "Presença assídua em vernissages, cinema e poesia.", "🎨", UserTier.PRATA, 9, 15, true),

            // Conquista
            SocialBadge("b_16", "Organizador", BadgeCategory.CONQUISTA, "Criou eventos que reuniram mais de 50 pessoas.", "📋", UserTier.BRONZE, 1, 5, false),
            SocialBadge("b_17", "Lendário", BadgeCategory.CONQUISTA, "Mais de 100 presenças atestadas por Proof of Presence.", "🔥", UserTier.OBSIDIAN, 42, 100, false),
            SocialBadge("b_18", "Colecionador", BadgeCategory.CONQUISTA, "Acumulou selos em todas as 6 categorias da plataforma.", "💎", UserTier.OURO, 5, 6, false)
        )
    )
    val badges: StateFlow<List<SocialBadge>> = _badges.asStateFlow()

    private val _connections = MutableStateFlow<List<Connection>>(
        listOf(
            Connection("c_1", "Beatriz Lima", "@bia.lima", "Noite Eletrônica no Rooftop", UserTier.PRATA, "Ontem"),
            Connection("c_2", "Carlos Eduardo", "@kadu_sp", "Sessão Acústica & Craft Beer", UserTier.PRATA, "3 dias atrás"),
            Connection("c_3", "Juliana Costa", "@ju.costa", "Paredão Cultural da Praça", UserTier.BRONZE, "Semana passada")
        )
    )
    val connections: StateFlow<List<Connection>> = _connections.asStateFlow()

    // Dynamic QR Generator state (15s rotation with salt)
    fun generateDynamicQrHash(eventId: String): Pair<String, Int> {
        val epochSeconds = System.currentTimeMillis() / 1000
        val windowSeconds = 15
        val currentWindow = epochSeconds / windowSeconds
        val secondsRemaining = (windowSeconds - (epochSeconds % windowSeconds)).toInt()

        val salt = "OBSIDIAN_SECRET_SALT_2026"
        val rawInput = "$eventId:$currentWindow:$salt"
        val digest = MessageDigest.getInstance("SHA-256").digest(rawInput.toByteArray())
        val hash = digest.fold("") { str, it -> str + "%02x".format(it) }.take(16).uppercase()
        return Pair("OBS-$hash", secondsRemaining)
    }

    fun performCheckIn(eventId: String, method: CheckInMethod): Boolean {
        val updated = _events.value.map { ev ->
            if (ev.id == eventId) ev.copy(isCheckedIn = true, attendeesCount = ev.attendeesCount + 1) else ev
        }
        _events.value = updated

        // Reward OQUANTUM for physical presence check-in
        val user = _currentUser.value
        val rewardAmount = 5.0 // 5 OQUANTUM for proof of presence
        _currentUser.value = user.copy(
            oquantumBalance = user.oquantumBalance + rewardAmount,
            checkInsCount = user.checkInsCount + 1
        )
        return true
    }

    fun submitEventFeedback(eventId: String, stars: Int, selectedTags: List<String>) {
        val updated = _events.value.map { ev ->
            if (ev.id == eventId) {
                val newCount = ev.ratingsCount + 1
                val newAvg = ((ev.ratingAvg * ev.ratingsCount) + stars) / newCount
                ev.copy(ratingAvg = String.format(Locale.US, "%.1f", newAvg).toDouble(), ratingsCount = newCount)
            } else ev
        }
        _events.value = updated

        // Reward for contributing with feedback
        val user = _currentUser.value
        _currentUser.value = user.copy(oquantumBalance = user.oquantumBalance + 1.0)
    }

    fun awardBadgeToPeer(recipientName: String, badge: SocialBadge, tier: UserTier): Boolean {
        // User gets OQUANTUM reward for validating / awarding badges (RN-040)
        val user = _currentUser.value
        val reward = 0.50
        _currentUser.value = user.copy(oquantumBalance = user.oquantumBalance + reward)

        // Check reciprocity: if tier matches, establish connection (RN-028)
        val newConnection = Connection(
            id = "conn_${System.currentTimeMillis()}",
            peerName = recipientName,
            peerHandle = "@${recipientName.lowercase().replace(" ", "")}",
            eventName = "Presença Validada no Local",
            matchedTier = tier,
            date = "Agora"
        )
        _connections.value = listOf(newConnection) + _connections.value
        return true
    }

    fun createEvent(
        title: String,
        description: String,
        category: EventCategory,
        locationName: String,
        visibility: VisibilityTier,
        checkInMethod: CheckInMethod,
        mediaReel: List<String> = emptyList()
    ) {
        val sdf = SimpleDateFormat("dd/MM", Locale.getDefault())
        val newEv = SocialEvent(
            id = "ev_${System.currentTimeMillis()}",
            title = title,
            description = description,
            category = category,
            locationName = locationName,
            distanceKm = 0.1,
            dateDisplay = sdf.format(Date()),
            timeDisplay = "Em breve",
            attendeesCount = 1,
            ratingAvg = 5.0,
            ratingsCount = 1,
            visibilityTier = visibility,
            checkInMethod = checkInMethod,
            isCheckedIn = true,
            tags = listOf("Novidade", "Criado por Você"),
            hostName = "${_currentUser.value.name} (Você) • 🛡️",
            hostId = _currentUser.value.id,
            isUserHost = true,
            mediaReel = mediaReel
        )
        _events.value = listOf(newEv) + _events.value
    }

    fun addMediaToEvent(eventId: String, mediaUris: List<String>) {
        _events.value = _events.value.map { ev ->
            if (ev.id == eventId) {
                ev.copy(mediaReel = (ev.mediaReel + mediaUris).distinct())
            } else {
                ev
            }
        }
    }

    fun removeMediaFromEvent(eventId: String, mediaUri: String) {
        _events.value = _events.value.map { ev ->
            if (ev.id == eventId) {
                ev.copy(mediaReel = ev.mediaReel.filter { it != mediaUri })
            } else {
                ev
            }
        }
    }

    fun toggleLikeEvent(eventId: String) {
        _events.value = _events.value.map { ev ->
            if (ev.id == eventId) {
                val newLiked = !ev.isLiked
                val newCount = if (newLiked) ev.likesCount + 1 else (ev.likesCount - 1).coerceAtLeast(0)
                ev.copy(isLiked = newLiked, likesCount = newCount)
            } else {
                ev
            }
        }
    }

    fun submitEventRating(eventId: String, stars: Int, tags: List<String>) {
        _events.value = _events.value.map { ev ->
            if (ev.id == eventId) {
                val totalRatingScore = (ev.ratingAvg * ev.ratingsCount) + stars
                val newCount = ev.ratingsCount + 1
                val newAvg = (totalRatingScore / newCount).let { Math.round(it * 10.0) / 10.0 }
                val updatedTags = (ev.tags + tags).distinct()
                ev.copy(ratingAvg = newAvg, ratingsCount = newCount, tags = updatedTags)
            } else {
                ev
            }
        }
    }
}
