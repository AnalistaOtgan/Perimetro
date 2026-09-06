package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserProfile
import com.example.model.getAvatarVector
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    user: UserProfile,
    onBack: () -> Unit,
    onSave: (UserProfile) -> Unit
) {
    var name by remember { mutableStateOf(user.name) }
    var handle by remember { mutableStateOf(user.handle) }
    var bio by remember { mutableStateOf(user.bio) }
    var selectedEmoji by remember { mutableStateOf(user.avatarEmoji) }
    var selectedUri by remember { mutableStateOf(user.avatarUri) }

    val avatarOptions: List<Pair<String, ImageVector>> = listOf(
        "auto_awesome" to Icons.Default.AutoAwesome,
        "bolt" to Icons.Default.Bolt,
        "music" to Icons.Default.MusicNote,
        "headphones" to Icons.Default.Headphones,
        "sports_esports" to Icons.Default.SportsEsports,
        "pets" to Icons.Default.Pets,
        "face" to Icons.Default.Face,
        "star" to Icons.Default.Star,
        "fire" to Icons.Default.LocalFireDepartment,
        "rocket" to Icons.Default.RocketLaunch
    )

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
                            .padding(horizontal = 8.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = ColorDarkObsidian)
                        }
                        
                        Text(
                            text = "Editar Perfil",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorDarkObsidian,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                // Avatar Selector
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(ObsidianTeal, ObsidianBurntOrange))),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedUri != null) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(70.dp))
                        } else {
                            Icon(
                                imageVector = getAvatarVector(selectedEmoji),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(56.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    Text("Escolha seu Avatar", style = MaterialTheme.typography.titleMedium, color = ColorDarkObsidian, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar icons grid
                        androidx.compose.foundation.lazy.LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(avatarOptions.size) { index ->
                                val (key, iconVector) = avatarOptions[index]
                                val isSelected = selectedEmoji == key && selectedUri == null
                                Surface(
                                    shape = CircleShape,
                                    color = if (isSelected) ColorTealLight else BgSurface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) ColorTeal else BorderWarm),
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clickable {
                                            selectedEmoji = key
                                            selectedUri = null
                                        }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = iconVector,
                                            contentDescription = null,
                                            tint = if (isSelected) ColorTeal else TextSecondary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                            item {
                                Surface(
                                    shape = CircleShape,
                                    color = if (selectedUri != null) ColorTealLight else BgSurface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedUri != null) ColorTeal else BorderWarm),
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clickable {
                                            selectedUri = "fake_uri_for_photo"
                                        }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.PhotoCamera, contentDescription = "Usar Foto", tint = ColorTeal, modifier = Modifier.size(24.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // Text Fields
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nome Completo") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ColorTeal,
                            unfocusedBorderColor = BorderWarm,
                            focusedContainerColor = BgSurface,
                            unfocusedContainerColor = BgSurface
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = handle,
                        onValueChange = { handle = it },
                        label = { Text("Nome de Usuário (@)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ColorTeal,
                            unfocusedBorderColor = BorderWarm,
                            focusedContainerColor = BgSurface,
                            unfocusedContainerColor = BgSurface
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("Biografia") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ColorTeal,
                            unfocusedBorderColor = BorderWarm,
                            focusedContainerColor = BgSurface,
                            unfocusedContainerColor = BgSurface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 4,
                        minLines = 3
                    )
                }

                Spacer(modifier = Modifier.weight(1f, fill = false))

                Button(
                    onClick = {
                        onSave(
                            user.copy(
                                name = name,
                                handle = handle,
                                bio = bio,
                                avatarEmoji = selectedEmoji,
                                avatarUri = selectedUri
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ColorTeal),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Salvar Perfil", 
                        modifier = Modifier.padding(vertical = 8.dp), 
                        fontSize = 16.sp, 
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
