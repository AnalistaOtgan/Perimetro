package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.HelpCenterDialog
import com.example.ui.components.ObsidianModalDialog
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadgesScreen(
    badges: List<SocialBadge>,
    connections: List<Connection>,
    onAwardBadgeToPeer: (recipient: String, badge: SocialBadge, tier: UserTier) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Selos da Tribo, 1 = Minhas Conexões
    var selectedCategory by remember { mutableStateOf<BadgeCategory?>(null) }
    var badgeToAward by remember { mutableStateOf<SocialBadge?>(null) }
    var showAwardDialog by remember { mutableStateOf(false) }
    var showHelpCenter by remember { mutableStateOf(false) }

    val filteredBadges = remember(badges, selectedCategory) {
        if (selectedCategory == null) badges else badges.filter { it.category == selectedCategory }
    }

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
                            text = "Tribos & Selos",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorDarkObsidian
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = { showHelpCenter = true },
                                modifier = Modifier.testTag("badges_help_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HelpOutline,
                                    contentDescription = "Ajuda dos Selos",
                                    tint = ObsidianTeal,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Button(
                                onClick = { showAwardDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ColorBurntOrange,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(9999.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("award_badge_top_btn")
                            ) {
                                Icon(Icons.Default.VolunteerActivism, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Dar Selo (+OQ)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
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
            ) {
                // Hero Title enxuto e visual
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Galeria de Reconhecimento",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = ColorDarkObsidian,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${badges.count { it.isUnlocked }} de ${badges.size} selos desbloqueados na comunidade",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }

                // Tab Selector: Selos vs Conexões
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = BgSurface,
                    contentColor = ColorTeal,
                    modifier = Modifier.border(1.dp, BorderWarm)
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Catálogo de Selos (${badges.size})", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Conexões Ativas (${connections.size})", fontWeight = FontWeight.Bold) }
                    )
                }

            if (selectedTab == 0) {
                // Category Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { selectedCategory = null },
                            label = { Text("Todas") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ObsidianTeal,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    items(BadgeCategory.values()) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = if (selectedCategory == cat) null else cat },
                            label = { Text(cat.title) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ObsidianTeal,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // Badges Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredBadges) { badge ->
                        BadgeItemCard(
                            badge = badge,
                            onClick = {
                                badgeToAward = badge
                                showAwardDialog = true
                            }
                        )
                    }
                }
            } else {
                // Connections Tab
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = ObsidianTealContainer),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Handshake,
                                    contentDescription = null,
                                    tint = ObsidianTeal,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Uma Conexão é selada quando duas pessoas trocam selos do mesmo nível em um evento físico presencial.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ObsidianTealDark
                                )
                            }
                        }
                    }

                    items(connections) { conn ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(ObsidianOrangeContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = conn.peerName.take(1),
                                            fontWeight = FontWeight.Bold,
                                            color = ObsidianBurntOrange,
                                            fontSize = 18.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = conn.peerName,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${conn.peerHandle} • ${conn.eventName}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = when (conn.matchedTier) {
                                        UserTier.BRONZE -> BadgeBronze.copy(alpha = 0.2f)
                                        UserTier.PRATA -> BadgeSilver.copy(alpha = 0.3f)
                                        UserTier.OURO -> BadgeGold.copy(alpha = 0.3f)
                                        UserTier.OBSIDIAN -> DarkObsidian
                                    }
                                ) {
                                    Text(
                                        text = conn.matchedTier.title,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (conn.matchedTier == UserTier.OBSIDIAN) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Award Badge Dialog
        if (showAwardDialog) {
            AwardBadgeDialog(
                defaultBadge = badgeToAward ?: badges.first(),
                allBadges = badges,
                onDismiss = {
                    showAwardDialog = false
                    badgeToAward = null
                },
                onConfirm = { recipient, badge, tier ->
                    onAwardBadgeToPeer(recipient, badge, tier)
                    showAwardDialog = false
                    badgeToAward = null
                }
            )
        }

        if (showHelpCenter) {
            HelpCenterDialog(
                onDismiss = { showHelpCenter = false }
            )
        }
    }
}

@Composable
fun BadgeItemCard(
    badge: SocialBadge,
    onClick: () -> Unit
) {
    val tierColor = when (badge.tier) {
        UserTier.BRONZE -> BadgeBronze
        UserTier.PRATA -> BadgeSilver
        UserTier.OURO -> BadgeGold
        UserTier.OBSIDIAN -> DarkObsidian
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("badge_card_${badge.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ObsidianTealContainer
                ) {
                    Text(
                        text = badge.category.title,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        color = ObsidianTeal,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = tierColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = badge.tier.title,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        color = if (badge.tier == UserTier.OBSIDIAN) DarkObsidian else tierColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Large icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getBadgeVector(badge.id),
                    contentDescription = badge.name,
                    tint = if (badge.isUnlocked) ObsidianTeal else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = badge.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { badge.progressCurrent.toFloat() / badge.progressTarget.coerceAtLeast(1) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (badge.isUnlocked) ObsidianTeal else ObsidianBurntOrange,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${badge.progressCurrent} / ${badge.progressTarget}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (badge.isUnlocked) ObsidianTeal else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AwardBadgeDialog(
    defaultBadge: SocialBadge,
    allBadges: List<SocialBadge>,
    onDismiss: () -> Unit,
    onConfirm: (recipient: String, badge: SocialBadge, tier: UserTier) -> Unit
) {
    var recipientName by remember { mutableStateOf("") }
    var selectedBadge by remember { mutableStateOf(defaultBadge) }
    var selectedTier by remember { mutableStateOf(UserTier.BRONZE) }

    ObsidianModalDialog(
        onDismissRequest = onDismiss,
        title = "Conceder Selo de Tribo",
        subtitle = "+0.50 OQUANTUM ao reconhecer presença",
        icon = getBadgeVector(selectedBadge.id),
        iconTint = ColorBurntOrange,
        iconBgColor = ColorBurntOrangeLight,
        headerAccentGradient = listOf(ColorBurntOrange, ColorMustard, ColorTeal),
        wrapHeight = true,
        buttons = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(9999.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
            ) {
                Text("Cancelar", fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = {
                    val finalName = if (recipientName.isBlank()) "Participante Presencial" else recipientName
                    onConfirm(finalName, selectedBadge, selectedTier)
                },
                modifier = Modifier
                    .weight(1.3f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ColorBurntOrange),
                shape = RoundedCornerShape(9999.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Stars,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Conceder Selo", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Input participante
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Participante",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = ColorDarkObsidian
                )
                OutlinedTextField(
                    value = recipientName,
                    onValueChange = { recipientName = it },
                    placeholder = { Text("Ex: Marina Luz ou @marina", color = TextMuted) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ColorBurntOrange,
                        unfocusedBorderColor = BorderWarm,
                        focusedContainerColor = BgSurface,
                        unfocusedContainerColor = BgSecondary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("peer_name_input")
                )
            }

            // Preview do Selo Selecionado
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = BgSecondary,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(ColorBurntOrangeLight, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getBadgeVector(selectedBadge.id),
                                contentDescription = null,
                                tint = ColorBurntOrange,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = selectedBadge.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = ColorDarkObsidian
                            )
                            Text(
                                text = selectedBadge.description,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                                color = TextMuted,
                                maxLines = 1
                            )
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = ColorTealLight
                    ) {
                        Text(
                            text = "+0.50 OQ",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = ColorTeal,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Seleção de Nível / Tier
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Nível do Selo Concedido",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = ColorDarkObsidian
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    UserTier.values().forEach { tier ->
                        val isSelected = selectedTier == tier
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp)),
                            color = if (isSelected) ColorBurntOrange else BgSecondary,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) ColorBurntOrange else BorderWarm
                            ),
                            onClick = { selectedTier = tier }
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tier.title,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else ColorDarkObsidian
                                )
                            }
                        }
                    }
                }
            }

            // Nota de Privacidade e Reciprocidade
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = BgSecondary,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Privacidade: O selo é discreto. Se a pessoa também te conceder um selo de nível ${selectedTier.title}, uma Conexão é criada automaticamente.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
