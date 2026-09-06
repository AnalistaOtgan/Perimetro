package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.ui.graphics.vector.ImageVector

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

fun getBadgeVector(id: String): ImageVector {
    return when (id) {
        "b_1" -> Icons.Default.MusicNote
        "b_2" -> Icons.Default.Equalizer
        "b_3" -> Icons.Default.Headphones
        "b_4" -> Icons.Default.WbSunny
        "b_5" -> Icons.Default.Groups
        "b_6" -> Icons.Default.Shield
        "b_7" -> Icons.Default.NightsStay
        "b_8" -> Icons.Default.DirectionsRun
        "b_9" -> Icons.Default.EventAvailable
        "b_10" -> Icons.Default.Explore
        "b_11" -> Icons.Default.FlightTakeoff
        "b_12" -> Icons.Default.WorkspacePremium
        "b_13" -> Icons.Default.LocalBar
        "b_14" -> Icons.Default.Restaurant
        "b_15" -> Icons.Default.Palette
        "b_16" -> Icons.Default.Assignment
        "b_17" -> Icons.Default.LocalFireDepartment
        "b_18" -> Icons.Default.Diamond
        else -> Icons.Default.Star
    }
}

data class BadgeGiftAction(
    val recipientName: String,
    val badgeName: String,
    val tier: UserTier,
    val oquantumRewarded: Double = 0.50,
    val timestamp: Long = System.currentTimeMillis()
)
