package com.example.model

enum class UserTier(val title: String, val threshold: Int, val storyPhotos: Int, val storyDuration: String) {
    BRONZE("Bronze", 5, 1, "3 dias"),
    PRATA("Prata", 15, 3, "7 dias"),
    OURO("Ouro", 40, 7, "30 dias"),
    OBSIDIAN("Obsidiana", 100, 30, "Tempo Ilimitado")
}

data class UserProfile(
    val id: String,
    val name: String,
    val handle: String,
    val bio: String,
    val avatarEmoji: String = "✨",
    val avatarUri: String? = null,
    val tier: UserTier = UserTier.PRATA,
    val oquantumBalance: Double = 142.50,
    val badgesCount: Int = 18,
    val checkInsCount: Int = 12,
    val connectionsCount: Int = 9
)

data class Connection(
    val id: String,
    val peerName: String,
    val peerHandle: String,
    val eventName: String,
    val matchedTier: UserTier,
    val date: String
)
