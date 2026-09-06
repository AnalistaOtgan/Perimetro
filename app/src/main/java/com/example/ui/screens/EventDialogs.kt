package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.model.*
import com.example.ui.components.ObsidianLogoEmblem
import com.example.ui.components.ObsidianModalDialog
import com.example.ui.components.ObsidianPrismRatingIcon
import com.example.ui.components.ObsidianResonanceIcon
import com.example.ui.components.ObsidianStationPortalIcon
import com.example.ui.components.DynamicQRCodeCard
import com.example.ui.theme.*

/**
 * RITUAL DE RESSONÂNCIA & SELO DE REPUTAÇÃO DA TRIBO (Event Resonance Dialog)
 * 
 * Substitui o antigo AlertDialog cinza genérico por uma peça escultórica sob medida Obsidian:
 * - Painel em cerâmica quente/obsidiana com facetamento de 28.dp e bordas com infusão âmbar/dourada
 * - Mini-Hero lapidado com avatar do anfitrião e selo de presença física real
 * - Seletor de Selos Prismáticos de Reputação com Vibe Dinâmica animada (Fria -> Boa -> Épica da Tribo)
 * - Pílulas táteis de Frequência da Tribo com micro-ícones de acústica, drinks, segurança e vibe
 * - Amuleto de Forja de Quorum (+1.0 OQ) e protocolo de integridade criptográfica
 */
@Composable
fun EventFeedbackDialog(
    event: SocialEvent,
    onDismiss: () -> Unit,
    onSubmitFeedback: (stars: Int, tags: List<String>) -> Unit
) {
    var stars by remember { mutableIntStateOf(5) }

    data class TribeTag(val name: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
    val availableTags = listOf(
        TribeTag("Acústica & Som", Icons.Default.MusicNote),
        TribeTag("Sintonia da Tribo", Icons.Default.Groups),
        TribeTag("Alquimia & Drinks", Icons.Default.LocalBar),
        TribeTag("Proteção & Respeito", Icons.Default.Shield),
        TribeTag("Atmosfera & Luz", Icons.Default.AutoAwesome),
        TribeTag("Espaço & Conforto", Icons.Default.Weekend),
        TribeTag("Acessibilidade", Icons.Default.Place),
        TribeTag("Alta Energia", Icons.Default.Bolt)
    )
    val selectedTags = remember { mutableStateListOf<String>("Sintonia da Tribo", "Acústica & Som") }

    // Rótulos e estados de Vibe da Tribo baseados no nível de estrelas
    val (vibeTitle, vibeSubtitle, vibeColor) = when (stars) {
        1 -> Triple("Presença Fria", "A atmosfera não atingiu a ressonância esperada.", Color(0xFF9E9E9E))
        2 -> Triple("Vibe Discreta", "Encontro calmo, com pouca sintonia no local.", Color(0xFF78909C))
        3 -> Triple("Boa Atmosfera", "Boa presença real e momentos agradáveis.", ColorTeal)
        4 -> Triple("Conexão Autêntica", "Sintonia vibrante e forte engajamento da tribo.", ColorBurntOrange)
        else -> Triple("Ressonância Épica da Tribo", "Experiência memorável! Ponto alto no Radar de Quorum.", Color(0xFFD97706))
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .widthIn(max = 430.dp)
                    .wrapContentHeight()
                    .clickable(enabled = false) {}
                    .shadow(elevation = 24.dp, shape = RoundedCornerShape(28.dp), spotColor = ColorBurntOrange.copy(alpha = 0.25f)),
                shape = RoundedCornerShape(28.dp),
                color = BgSurface,
                border = androidx.compose.foundation.BorderStroke(1.2.dp, BorderWarm)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    // ====================================================================
                    // 1. TOPO ESCULPIDO: MINI-HERO DA EXPERIÊNCIA COM GRADIENTE OBSIDIANA
                    // ====================================================================
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        ColorDarkObsidian,
                                        Color(0xFF1E232A),
                                        Color(0xFF262C36)
                                    )
                                )
                            )
                    ) {
                        // Fundo com imagem sutil do evento (se existir)
                        if (event.mediaReel.isNotEmpty()) {
                            AsyncImage(
                                model = event.mediaReel.first(),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer { alpha = 0.25f }
                            )
                        }

                        // Botão de fechar translúcido
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                                .size(32.dp)
                                .background(Color.White.copy(alpha = 0.15f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fechar",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Conteúdo do Mini Hero
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(9999.dp),
                                    color = ColorTeal.copy(alpha = 0.25f),
                                    border = androidx.compose.foundation.BorderStroke(0.8.dp, ColorTealBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(ColorTealLight, CircleShape)
                                        )
                                        Text(
                                            text = "VOCÊ ESTEVE AQUI!",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Black,
                                            color = ColorTealLight,
                                            letterSpacing = 0.8.sp
                                        )
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            modifier = Modifier.size(11.dp),
                                            tint = ColorTealLight
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(9999.dp),
                                    color = ColorMustard.copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(0.8.dp, ColorMustardBorder)
                                ) {
                                    Text(
                                        text = event.category.label,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ColorMustard
                                    )
                                }
                            }

                            Text(
                                text = event.title,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                maxLines = 1
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Anfitrião: ${event.hostName.substringBefore(" •")}",
                                    fontSize = 11.5.sp,
                                    color = Color.White.copy(alpha = 0.75f)
                                )
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = "Verificado",
                                    tint = ColorTealLight,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }

                    // ====================================================================
                    // 2. CORPO DO MODAL: RITUAL DE RESSONÂNCIA E SELOS PRISMÁTICOS
                    // ====================================================================
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        // Título do Ritual
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                ObsidianLogoEmblem(size = 20.dp)
                                Text(
                                    text = "Como foi a vibe do encontro?",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ColorDarkObsidian,
                                    letterSpacing = (-0.3).sp
                                )
                            }
                            Text(
                                text = "Conta pra galera o que você achou dessa experiência!",
                                fontSize = 12.5.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }

                        // BARRA DE SELOS PRISMÁTICOS (1 a 5) COM ILUMINAÇÃO TÁTIL
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = BgSecondary,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp, horizontal = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    for (i in 1..5) {
                                        val isFilled = i <= stars
                                        val scale by animateFloatAsState(
                                            targetValue = if (isFilled) 1.12f else 0.92f,
                                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                            label = "prism_scale"
                                        )

                                        IconButton(
                                            onClick = { stars = i },
                                            modifier = Modifier
                                                .size(46.dp)
                                                .graphicsLayer {
                                                    scaleX = scale
                                                    scaleY = scale
                                                }
                                                .testTag("rate_star_$i")
                                        ) {
                                            ObsidianPrismRatingIcon(
                                                isFilled = isFilled,
                                                size = 36.dp,
                                                tint = if (isFilled) ColorMustard else TextMuted.copy(alpha = 0.4f)
                                            )
                                        }
                                    }
                                }

                                // Selo dinâmico de Vibe
                                AnimatedContent(
                                    targetState = stars,
                                    transitionSpec = {
                                        fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(180))
                                    },
                                    label = "vibe_anim"
                                ) { _ ->
                                    Surface(
                                        shape = RoundedCornerShape(9999.dp),
                                        color = vibeColor.copy(alpha = 0.12f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, vibeColor.copy(alpha = 0.35f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (stars == 5) Icons.Default.LocalFireDepartment else Icons.Default.AutoAwesome,
                                                contentDescription = null,
                                                modifier = Modifier.size(13.dp),
                                                tint = vibeColor
                                            )
                                            Text(
                                                text = vibeTitle,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Black,
                                                color = vibeColor
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = vibeSubtitle,
                                    fontSize = 11.5.sp,
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // ====================================================================
                        // 3. FREQUÊNCIA DA TRIBO (Pílulas de Sintonia com Ícones)
                        // ====================================================================
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Frequências & Destaques:",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ColorDarkObsidian
                                )
                                Text(
                                    text = "${selectedTags.size} selecionados",
                                    fontSize = 11.sp,
                                    color = ColorTeal,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Grade de tags com visual lapidado
                            availableTags.chunked(2).forEach { pair ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    pair.forEach { tagItem ->
                                        val isSelected = selectedTags.contains(tagItem.name)
                                        Surface(
                                            onClick = {
                                                if (isSelected) selectedTags.remove(tagItem.name)
                                                else selectedTags.add(tagItem.name)
                                            },
                                            shape = RoundedCornerShape(12.dp),
                                            color = if (isSelected) ColorBurntOrangeLight else BgSecondary,
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                if (isSelected) ColorBurntOrange else BorderWarm
                                            ),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(38.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(horizontal = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Icon(
                                                    imageVector = tagItem.icon,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(14.dp),
                                                    tint = if (isSelected) ColorBurntOrangeHover else ColorTeal
                                                )
                                                Text(
                                                    text = tagItem.name,
                                                    fontSize = 11.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSelected) ColorBurntOrangeHover else ColorDarkObsidian,
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // ====================================================================
                        // 4. CÉLULA DE RECOMPENSA DE QUORUM & PROTOCOLO ANTIFRAUDE
                        // ====================================================================
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = ColorTealLight.copy(alpha = 0.7f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ColorTealBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = ColorTeal,
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Bolt,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "+1.0 OQ Concedido",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = ObsidianTealDark
                                    )
                                     Text(
                                         text = "Sua avaliação ajuda os amigos da tribo a escolher os melhores encontros!",
                                         fontSize = 10.5.sp,
                                         color = TextSecondary,
                                         lineHeight = 14.sp
                                     )
                                }
                            }
                        }

                        // ====================================================================
                        // 5. BOTÕES DE AÇÃO: AVALIAR ENCONTRO OU ADIAR
                        // ====================================================================
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    onSubmitFeedback(stars, selectedTags.toList())
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ColorBurntOrange,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .shadow(4.dp, RoundedCornerShape(14.dp), spotColor = Color(0x47D87A56))
                                    .testTag("submit_feedback_btn")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    ObsidianPrismRatingIcon(
                                        isFilled = true,
                                        size = 18.dp,
                                        tint = Color(0xFFFFF0B3)
                                    )
                                     Text(
                                         text = "Avaliar Encontro (+1 OQ)",
                                         fontSize = 14.sp,
                                         fontWeight = FontWeight.Black
                                     )
                                }
                            }

                            TextButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp)
                            ) {
                                Text(
                                    text = "Avaliar em outro momento",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun AuditInspectionDialog(
    event: SocialEvent,
    onDismiss: () -> Unit
) {
    ObsidianModalDialog(
        onDismissRequest = onDismiss,
        title = "Auditoria Criptográfica",
        subtitle = event.title,
        icon = Icons.Default.VerifiedUser,
        iconTint = ColorTeal,
        iconBgColor = ColorTealLight,
        headerAccentGradient = listOf(ColorTeal, ObsidianTealLight, ColorMustard),
        wrapHeight = true,
        buttons = {
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ColorTeal),
                shape = RoundedCornerShape(9999.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Concluir Auditoria", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // PostGIS Geofence Info
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = BgSecondary,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(ColorTeal, CircleShape)
                        )
                        Text(
                            text = "Geofence PostGIS:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = ColorTeal
                        )
                        Text(
                            text = "Ativo (${event.geofenceRadiusMeters}m)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = ColorDarkObsidian
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Ponto de ancoragem: -23.5955, -46.6853\nProtocolo: 2-Fatores (GPS Geofence + QR HMAC 15s)",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, lineHeight = 16.sp),
                        color = TextSecondary
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Presenças Auditadas no Local (${event.attendeesCount}):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = ColorDarkObsidian
                )
                Surface(
                    shape = RoundedCornerShape(9999.dp),
                    color = ColorTealLight
                ) {
                    Text(
                        text = "100% On-site",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorTeal
                    )
                }
            }

            // Audit records list
            listOf(
                Triple("Alex Silva", "42m do centroide • 08:32", "Validado 2FA"),
                Triple("Beatriz Lima", "18m do centroide • 08:35", "Validado 2FA"),
                Triple("Carlos Eduardo", "65m do centroide • 08:41", "Validado 2FA")
            ).take(event.attendeesCount.coerceAtLeast(1)).forEach { (name, meta, status) ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(ColorTealLight, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = name.first().toString(),
                                    fontWeight = FontWeight.Bold,
                                    color = ColorTeal
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = ColorDarkObsidian
                                )
                                Text(
                                    text = meta,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                                    color = TextMuted
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = ColorTealLight
                        ) {
                            Text(
                                text = status,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = ColorTeal,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Integrity Summary Card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = ColorBurntOrangeLight,
                border = androidx.compose.foundation.BorderStroke(1.dp, ColorBurntOrangeBorder)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = ColorBurntOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Integridade 100%: Nenhuma validação remota fora do geofence.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, lineHeight = 16.sp),
                        color = ColorDarkObsidian,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun CheckInStationDialog(
    event: SocialEvent,
    onDismiss: () -> Unit
) {
    ObsidianModalDialog(
        onDismissRequest = onDismiss,
        title = "Estação de Check-in",
        subtitle = event.title,
        customIcon = {
            ObsidianStationPortalIcon(
                size = 24.dp,
                tint = ColorBurntOrange,
                animated = true
            )
        },
        iconBgColor = ColorBurntOrangeLight,
        headerAccentGradient = listOf(ColorBurntOrange, ColorMustard, ColorTeal),
        wrapHeight = true,
        buttons = {
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("close_station_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = ColorBurntOrange),
                shape = RoundedCornerShape(9999.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Concluir Estação",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Explanatory Banner Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = BgSecondary,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(ColorTealLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = ColorTeal,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = "Posicione este painel na recepção. Os participantes devem aproximar a câmera para validação cruzada com o GPS.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 16.sp),
                        color = TextSecondary
                    )
                }
            }

            // Dynamic rotating QR Code Card
            DynamicQRCodeCard(
                eventId = event.id,
                modifier = Modifier.fillMaxWidth()
            )

            // Geofence & Location Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = BgSecondary,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(ColorTealLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = ColorTeal,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = event.locationName,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = ColorDarkObsidian,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Geofence ativo • Raio de precisão ${event.geofenceRadiusMeters}m",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                            color = ColorTeal,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
