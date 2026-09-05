package com.example.model

enum class EventCategory(val label: String, val iconName: String) {
    TECNOLOGIA("Tecnologia", "Code"),
    MEETUP("Meetup", "Groups"),
    SHOW("Shows & Festas", "MusicNote"),
    BAR("Bares & Lounges", "LocalBar"),
    ESPACO("Espaços Culturais", "Place"),
    PAREDAO("Paredão & Rua", "Speaker"),
    PRIVADO("Encontros Privados", "Lock")
}

enum class VisibilityTier(val label: String, val minTier: UserTier?) {
    PUBLICO("Público (Todos)", null),
    BRONZE_PLUS("Exclusivo Bronze+", UserTier.BRONZE),
    PRATA_PLUS("Semi-Público (Prata+)", UserTier.PRATA),
    OURO_VIP("VIP & Gala Secreto (Ouro+)", UserTier.OURO),
    OBSIDIAN_ELITE("Elite Obsidian", UserTier.OBSIDIAN)
}

enum class CheckInMethod(val label: String) {
    GPS_ONLY("GPS / Geofence"),
    QR_DYNAMIC("QR Code Dinâmico"),
    BLE_BEACON("BLE Beacon Presencial")
}

data class SocialEvent(
    val id: String,
    val title: String,
    val description: String,
    val category: EventCategory,
    val locationName: String,
    val distanceKm: Double,
    val dateDisplay: String,
    val timeDisplay: String,
    val attendeesCount: Int,
    val ratingAvg: Double = 4.8,
    val ratingsCount: Int = 34,
    val visibilityTier: VisibilityTier = VisibilityTier.PUBLICO,
    val checkInMethod: CheckInMethod = CheckInMethod.QR_DYNAMIC,
    val isCheckedIn: Boolean = false,
    val tags: List<String> = listOf("Música", "Ambiente", "Segurança"),
    val hostName: String = "Obsidian Tech Hub • 🛡️",
    val hostId: String = "host_community",
    val isUserHost: Boolean = false,
    val mediaReel: List<String> = emptyList(),
    val geofenceRadiusMeters: Int = 150,
    val bannerTheme: String = "tech",
    val likesCount: Int = 42,
    val isLiked: Boolean = false
)
