package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

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
    val avatarEmoji: String = "auto_awesome",
    val avatarUri: String? = null,
    val tier: UserTier = UserTier.PRATA,
    val oquantumBalance: Double = 142.50,
    val badgesCount: Int = 18,
    val checkInsCount: Int = 12,
    val connectionsCount: Int = 9
)

fun getAvatarVector(avatarKey: String): ImageVector {
    return when (avatarKey) {
        "auto_awesome" -> Icons.Default.AutoAwesome
        "bolt" -> Icons.Default.Bolt
        "music" -> Icons.Default.MusicNote
        "headphones" -> Icons.Default.Headphones
        "sports_esports" -> Icons.Default.SportsEsports
        "pets" -> Icons.Default.Pets
        "face" -> Icons.Default.Face
        "star" -> Icons.Default.Star
        "fire" -> Icons.Default.LocalFireDepartment
        "rocket" -> Icons.Default.RocketLaunch
        else -> Icons.Default.Person
    }
}

data class Connection(
    val id: String,
    val peerName: String,
    val peerHandle: String,
    val eventName: String,
    val matchedTier: UserTier,
    val date: String
)
