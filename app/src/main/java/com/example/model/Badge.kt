package com.example.model

enum class BadgeCategory(val title: String) {
    MUSICA("Música"),
    COMPORTAMENTO("Comportamento"),
    FREQUENCIA("Frequência"),
    EXPLORACAO("Exploração"),
    NICHO("Nicho"),
    CONQUISTA("Conquista")
}

data class SocialBadge(
    val id: String,
    val name: String,
    val category: BadgeCategory,
    val description: String,
    val iconEmoji: String,
    val tier: UserTier = UserTier.BRONZE,
    val progressCurrent: Int = 3,
    val progressTarget: Int = 5,
    val isUnlocked: Boolean = false
)

data class BadgeGiftAction(
    val recipientName: String,
    val badgeName: String,
    val tier: UserTier,
    val oquantumRewarded: Double = 0.50,
    val timestamp: Long = System.currentTimeMillis()
)
