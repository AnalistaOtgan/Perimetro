package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*

/**
 * ObsidianModalDialog: Componente de modal exclusivo com a identidade visual do app.
 *
 * Características de design:
 * - Linha de acento superior com gradiente tricolor Obsidian (Laranja Queimado -> Mostarda -> Teal).
 * - Cabeçalho escuro escultural em gradiente de Obsidiana Profunda (ColorDarkObsidian).
 * - Ícone temático em container geométrico arredondado com tintura de marca.
 * - Tipografia de alto contraste com título nítido e subtítulo contextual.
 * - Botão de fechar minimalista e tátil no canto superior direito.
 * - Corpo em superfície quente acolhedora (BgSurface) com bordas orgânicas (BorderWarm) e cantos de 28.dp.
 * - Sombra difusa suave com dispersão sutil em tom de terracota/obsidiana.
 */
@Composable
fun ObsidianModalDialog(
    onDismissRequest: () -> Unit,
    title: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    iconTint: Color = ColorBurntOrange,
    iconBgColor: Color = ColorBurntOrangeLight,
    customIcon: (@Composable () -> Unit)? = null,
    headerAccentGradient: List<Color> = listOf(ColorBurntOrange, ColorMustard, ColorTeal),
    maxWidth: Dp = 440.dp,
    maxHeightRatio: Float = 0.88f,
    wrapHeight: Boolean = true,
    scrollable: Boolean = true,
    buttons: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        // Scrim escuro translúcido com toque externo para fechar
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.72f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismissRequest
                ),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .widthIn(max = maxWidth)
                    .then(
                        if (wrapHeight) {
                            Modifier
                                .wrapContentHeight()
                                .heightIn(max = 660.dp)
                        } else {
                            Modifier.fillMaxHeight(maxHeightRatio)
                        }
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = false,
                        onClick = {}
                    )
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(28.dp),
                        spotColor = ColorBurntOrange.copy(alpha = 0.22f)
                    ),
                shape = RoundedCornerShape(28.dp),
                color = BgSurface,
                border = androidx.compose.foundation.BorderStroke(1.2.dp, BorderWarm)
            ) {
                Column(
                    modifier = if (wrapHeight) Modifier.wrapContentHeight() else Modifier.fillMaxSize()
                ) {
                    // 1. Linha de acento de marca no topo extremo
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.5.dp)
                            .background(Brush.horizontalGradient(headerAccentGradient))
                    )

                    // 2. Cabeçalho Escultural Obsidiana Profunda
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        ColorDarkObsidian,
                                        Color(0xFF161E25),
                                        Color(0xFF1C2730)
                                    )
                                )
                            )
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f, fill = false),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Ícone do Cabeçalho
                                if (customIcon != null) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(iconBgColor, RoundedCornerShape(12.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        customIcon()
                                    }
                                } else if (icon != null) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(iconBgColor, RoundedCornerShape(12.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = iconTint,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }

                                // Título e Subtítulo
                                Column(
                                    modifier = Modifier.weight(1f, fill = false),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = title,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (!subtitle.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = subtitle,
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = ColorBurntOrangeLight,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Botão de Fechar no topo direito
                            IconButton(
                                onClick = onDismissRequest,
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color.White.copy(alpha = 0.12f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Fechar Modal",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // 3. Conteúdo Principal
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .then(if (wrapHeight) Modifier.weight(1f, fill = false) else Modifier.weight(1f))
                            .fillMaxWidth()
                            .then(if (scrollable) Modifier.verticalScroll(scrollState) else Modifier)
                            .padding(20.dp)
                    ) {
                        content()
                    }

                    // 4. Rodapé / Botões de Ação (se houver)
                    if (buttons != null) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = BgSecondary,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                buttons()
                            }
                        }
                    }
                }
            }
        }
    }
}
