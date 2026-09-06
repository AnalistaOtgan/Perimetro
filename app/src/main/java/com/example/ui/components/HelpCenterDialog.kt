package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*

data class HelpTopic(
    val id: String,
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val description: String,
    val badge: String = "Guia Oficial"
)

/**
 * Central de Ajuda e Guia do Aplicativo
 * Move todas as explicações textuais detalhadas para a área de Configurações/Ajuda,
 * liberando as telas principais para uma experiência visual e focada em ação.
 */
@Composable
fun HelpCenterDialog(
    onDismiss: () -> Unit
) {
    val helpTopics = remember {
        listOf(
            HelpTopic(
                id = "radar",
                icon = Icons.Default.NearMe,
                title = "Radar de Encontros",
                subtitle = "Sonar de proximidade em tempo real",
                description = "O radar mapeia pessoas e encontros ao seu redor em um raio ajustável de 1km a 5km. Quando você entra no raio de 500m de um evento ativo, o botão de check-in é ativado instantaneamente."
            ),
            HelpTopic(
                id = "checkin",
                icon = Icons.Default.QrCodeScanner,
                title = "Presença Real & Totem",
                subtitle = "Validação física à prova de fraudes",
                description = "Você pode validar sua presença via geolocalização ao chegar ao local ou escanear o QR Code criptografado exibido no Totem físico na recepção do evento. Isso garante que todos no feed realmente estão presentes."
            ),
            HelpTopic(
                id = "oquantum",
                icon = Icons.Default.MonetizationOn,
                title = "Moeda OQUANTUM (OQ)",
                subtitle = "Pontuação de prestígio presencial",
                description = "OQ é a moeda conquistada por vivência real. Você ganha de +30 a +50 OQ a cada presença confirmada e ao doar ou receber selos de reconhecimento de amigos e novos conhecidos."
            ),
            HelpTopic(
                id = "badges",
                icon = Icons.Default.EmojiEvents,
                title = "Tribos & Selos de Reputação",
                subtitle = "Reconhecimento coletivo entre pessoas",
                description = "Em vez de curtidas vazias, a comunidade usa selos de vivência (Música, Comportamento, Frequência). Você pode conceder selos a quem conheceu nos eventos para subir o nível de ambos na rede."
            ),
            HelpTopic(
                id = "photo_stories",
                icon = Icons.Default.AutoStories,
                title = "Photo Stories & Níveis (Tiers)",
                subtitle = "Memórias fotográficas e alcance",
                description = "Usuários Bronze, Prata, Ouro e Obsidian possuem cotas progressivas de fotos e tempo de permanência das suas publicações nos eventos. Quanto mais presenças, maior seu prestígio e capacidade no feed."
            )
        )
    }

    var expandedTopicId by remember { mutableStateOf<String?>(null) }

    ObsidianModalDialog(
        onDismissRequest = onDismiss,
        title = "Central de Ajuda",
        subtitle = "Guia de Experiência & Presença Real",
        icon = Icons.Default.HelpCenter,
        iconTint = ColorTeal,
        iconBgColor = ColorTealLight,
        headerAccentGradient = listOf(ColorTeal, ColorBurntOrange, ColorMustard),
        wrapHeight = false,
        buttons = {
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(9999.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ColorTeal)
            ) {
                Text(
                    text = "Entendido",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
    ) {
        // Lista de tópicos explicativos em cartões expansíveis
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("help_center_dialog"),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            helpTopics.forEach { topic ->
                val isExpanded = expandedTopicId == topic.id

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            expandedTopicId = if (isExpanded) null else topic.id
                        },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isExpanded) BgSecondary else BgCanvas,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isExpanded) ColorTeal.copy(alpha = 0.5f) else BorderWarm
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            if (isExpanded) ColorTeal else ColorTealLight,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = topic.icon,
                                        contentDescription = null,
                                        tint = if (isExpanded) Color.White else ColorTeal,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = topic.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ColorDarkObsidian
                                    )
                                    Text(
                                        text = topic.subtitle,
                                        fontSize = 11.5.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        AnimatedVisibility(visible = isExpanded) {
                            Column(modifier = Modifier.padding(top = 10.dp)) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    color = BorderWarm
                                )
                                Text(
                                    text = topic.description,
                                    fontSize = 13.sp,
                                    color = ColorDarkObsidian,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
        }
    }
