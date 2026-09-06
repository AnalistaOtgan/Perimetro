package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserProfile
import com.example.model.UserTier
import com.example.model.getAvatarVector
import com.example.ui.components.HelpCenterDialog
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: UserProfile,
    onCreateEventClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var showHelpCenter by remember { mutableStateOf(false) }
    var showLevelDetails by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCanvas)
    ) {
        com.example.ui.components.OrganicCirclesBackground()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = BgSurface.copy(alpha = 0.95f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Meu Perfil",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorDarkObsidian
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { showHelpCenter = true },
                                modifier = Modifier.testTag("profile_help_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HelpOutline,
                                    contentDescription = "Central de Ajuda",
                                    tint = ObsidianTeal
                                )
                            }

                            IconButton(
                                onClick = onLogoutClick,
                                modifier = Modifier.testTag("logout_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Logout,
                                    contentDescription = "Sair",
                                    tint = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
            // User Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .then(
                            if (user.avatarUri != null) Modifier.background(Color.Transparent)
                            else Modifier.background(Brush.linearGradient(listOf(ObsidianTeal, ObsidianBurntOrange)))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (user.avatarUri != null) {
                        // Simulating an Image for now with an Icon if we don't have coil, but we'll use a placeholder
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            tint = Color.White,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.linearGradient(listOf(ObsidianTeal, ObsidianBurntOrange)))
                                .padding(16.dp)
                        )
                    } else {
                        Icon(
                            imageVector = getAvatarVector(user.avatarEmoji),
                            contentDescription = "Avatar",
                            tint = Color.White,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.linearGradient(listOf(ObsidianTeal, ObsidianBurntOrange)))
                                .padding(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Presença Verificada",
                            tint = ObsidianTeal,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = user.handle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        onClick = { showLevelDetails = true },
                        shape = RoundedCornerShape(12.dp),
                        color = when (user.tier) {
                            UserTier.BRONZE -> BadgeBronze.copy(alpha = 0.2f)
                            UserTier.PRATA -> BadgeSilver.copy(alpha = 0.3f)
                            UserTier.OURO -> BadgeGold.copy(alpha = 0.3f)
                            UserTier.OBSIDIAN -> DarkObsidian
                        }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                            Text(
                                text = "Nível ${user.tier.title}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (user.tier == UserTier.OBSIDIAN) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Info, 
                                contentDescription = "Ver Detalhes", 
                                tint = if (user.tier == UserTier.OBSIDIAN) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            Text(
                text = user.bio,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Button(
                onClick = onEditProfileClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = BgSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Editar Perfil", color = ColorDarkObsidian, fontWeight = FontWeight.Bold)
            }

            // OQUANTUM Wallet Card (RN-039, RN-042)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("oquantum_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = DarkObsidian)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = ObsidianMustardYellow,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Saldo OQUANTUM",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = ObsidianOrangeLight
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = ObsidianTealDark
                        ) {
                            Text(
                                text = "Quântico",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                color = ObsidianTealLight,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "%.2f OQ".format(user.oquantumBalance),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            onClick = { showHelpCenter = true },
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF222B2C),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF324142)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.HelpOutline, contentDescription = null, tint = ObsidianOrangeLight, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Regras & Benefícios OQ", fontSize = 11.5.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // Photo Stories Privileges Card (RN-037)
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = null,
                            tint = ObsidianTeal
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Benefícios de Photo Stories",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Capacidade",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${user.tier.storyPhotos} fotos ativas",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column {
                            Text(
                                text = "Permanência",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = user.tier.storyDuration,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = ObsidianBurntOrange
                            )
                        }

                        Column {
                            Text(
                                text = "Próximo Nível",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (user.tier == UserTier.OBSIDIAN) "Nível Máximo" else "Ouro (7 fotos/30d)",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Stats Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${user.checkInsCount}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = ObsidianTeal
                        )
                        Text(
                            text = "Presenças Reais",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${user.badgesCount}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = ObsidianBurntOrange
                        )
                        Text(
                            text = "Selos Tribo",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${user.connectionsCount}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = ObsidianMustardYellow
                        )
                        Text(
                            text = "Conexões",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Atalho para Central de Ajuda & Diretrizes
            Card(
                onClick = { showHelpCenter = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_help_center_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(ColorTealLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.HelpOutline,
                                contentDescription = null,
                                tint = ObsidianTeal,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Central de Ajuda & Diretrizes",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorDarkObsidian
                            )
                            Text(
                                text = "Regras de presença, OQUANTUM, selos e encontros",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowForwardIos,
                        contentDescription = null,
                        tint = ObsidianTeal,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

    if (showHelpCenter) {
        HelpCenterDialog(
            onDismiss = { showHelpCenter = false }
        )
    }

    if (showLevelDetails) {
        LevelDetailsDialog(
            user = user,
            onDismiss = { showLevelDetails = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelDetailsDialog(
    user: UserProfile,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = BgCanvas,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = ObsidianMustardYellow, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Seu Nível: ${user.tier.title}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = ColorDarkObsidian
            )
            Text(
                text = "${user.badgesCount} Selos • ${user.checkInsCount} Presenças",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Tier Timeline
            val tiers = UserTier.values()
            
            tiers.forEachIndexed { index, tier ->
                val isAchieved = user.tier.ordinal >= tier.ordinal
                val isCurrent = user.tier == tier
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(40.dp)) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (isAchieved) ColorTeal else BorderLight),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isAchieved) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }
                        
                        if (index < tiers.size - 1) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(60.dp)
                                    .background(if (user.tier.ordinal > tier.ordinal) ColorTeal else BorderLight)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column(modifier = Modifier.padding(bottom = 24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Nível ${tier.title}",
                                fontWeight = FontWeight.Bold,
                                color = if (isAchieved) ColorDarkObsidian else TextMuted,
                                fontSize = 16.sp
                            )
                            if (isCurrent) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(shape = RoundedCornerShape(8.dp), color = ColorTealLight) {
                                    Text("Atual", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ColorTeal, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }
                        Text(
                            text = "Requer: ${tier.threshold} pontos/selos",
                            fontSize = 12.sp,
                            color = if (isAchieved) TextSecondary else TextMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Vantagens: ${tier.storyPhotos} fotos no Stories (duração: ${tier.storyDuration})",
                            fontSize = 13.sp,
                            color = if (isAchieved) ObsidianTeal else TextMuted,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
