package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.model.SocialEvent
import com.example.ui.theme.*
import kotlinx.coroutines.launch

/**
 * COMPONENTE DE CARRETEL DE MÍDIA DO ENCONTRO (Event Media Reel)
 * 
 * Permite que participantes naveguem pelas fotos do encontro e que o DONO (Anfitrião)
 * faça upload de fotos e vídeos através do Android Photo Picker nativo sem permissões invasivas.
 */
@Composable
fun EventMediaReelBanner(
    event: SocialEvent,
    isOwner: Boolean,
    onAddMedia: (List<String>) -> Unit,
    onManageMedia: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 175.dp
) {
    val coroutineScope = rememberCoroutineScope()
    val mediaList = event.mediaReel

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 10)
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            onAddMedia(uris.map { it.toString() })
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
            .background(Color(0xFF14171A))
    ) {
        if (mediaList.isNotEmpty()) {
            val pagerState = rememberPagerState(pageCount = { mediaList.size })

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val mediaUrl = mediaList[page]
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = mediaUrl,
                        contentDescription = "Mídia ${page + 1} do encontro ${event.title}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Gradiente suave no topo e na base para legibilidade dos badges
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.55f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.70f)
                                    )
                                )
                            )
                    )
                }
            }

            // Indicador de Posição do Carretel (Ex: 1/4)
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 12.dp, bottom = 10.dp),
                shape = RoundedCornerShape(9999.dp),
                color = Color.Black.copy(alpha = 0.65f),
                border = androidx.compose.foundation.BorderStroke(0.8.dp, Color.White.copy(alpha = 0.25f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Collections,
                        contentDescription = null,
                        tint = ColorMustard,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "${pagerState.currentPage + 1}/${mediaList.size}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Botões de navegação rápida (anterior / próximo) se houver mais de 1 foto
            if (mediaList.size > 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (pagerState.currentPage > 0) {
                        Surface(
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            },
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.5f),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Anterior",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.size(28.dp))
                    }

                    if (pagerState.currentPage < mediaList.size - 1) {
                        Surface(
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            },
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.5f),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Próximo",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.size(28.dp))
                    }
                }
            }
        } else {
            // Se não houver fotos ainda, exibe a ilustração vetorial estilizada do tema
            EventCardBanner(event = event, modifier = Modifier.fillMaxSize())
        }

        // BADGE DO TOPO ESQUERDO: CATEGORIA
        Surface(
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.TopStart),
            shape = RoundedCornerShape(9999.dp),
            color = ColorTealLight.copy(alpha = 0.95f),
            border = androidx.compose.foundation.BorderStroke(1.dp, ColorTealBorder)
        ) {
            Text(
                text = event.category.label,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = ColorTeal,
                fontWeight = FontWeight.Bold
            )
        }

        // BADGES DO TOPO DIREITO: AVALIAÇÃO E BOTÃO DO ANFITRIÃO
        Row(
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.TopEnd),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Se for o dono do encontro: BOTÃO EXCLUSIVO DE SUBIR / GERENCIAR MÍDIA DO CARRETEL
            if (isOwner) {
                Surface(
                    onClick = onManageMedia,
                    shape = RoundedCornerShape(9999.dp),
                    color = ColorBurntOrange,
                    shadowElevation = 3.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ColorBurntOrangeLight),
                    modifier = Modifier.testTag("host_upload_media_btn_${event.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = "Subir Mídia",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (mediaList.isEmpty()) "Subir Carretel" else "Carretel (${mediaList.size})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }

            // Nota Prismática
            Surface(
                shape = RoundedCornerShape(9999.dp),
                color = Color.White.copy(alpha = 0.95f),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ObsidianPrismRatingIcon(
                        isFilled = true,
                        size = 14.dp,
                        tint = ColorMustard
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${event.ratingAvg}",
                        style = MaterialTheme.typography.labelSmall,
                        color = ColorDarkObsidian,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // BADGE DO RODAPÉ ESQUERDO: DATA & HORA
        Surface(
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.BottomStart),
            shape = RoundedCornerShape(9999.dp),
            color = Color(0xCC0B0C10),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📅 ${event.dateDisplay} às ${event.timeDisplay}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * DIÁLOGO / MODAL DE GERENCIAMENTO E UPLOAD DO CARRETEL DE MÍDIA DO ENCONTRO
 * 
 * Exclusivo para o Anfitrião / Dono do evento adicionar novas fotos da galeria ou remover fotos existentes.
 */
@Composable
fun ManageEventMediaDialog(
    event: SocialEvent,
    onDismiss: () -> Unit,
    onAddMedia: (List<String>) -> Unit,
    onRemoveMedia: (String) -> Unit
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 10)
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            onAddMedia(uris.map { it.toString() })
        }
    }

    // Amostras prontas de imagens para teste imediato no emulador
    val samplePresetPhotos = listOf(
        "https://images.unsplash.com/photo-1523580494863-6f3031224c94?w=800&q=80",
        "https://images.unsplash.com/photo-1511578314322-379afb476865?w=800&q=80",
        "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=800&q=80",
        "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?w=800&q=80"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = BgSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm),
            shadowElevation = 10.dp,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top Header do Diálogo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Carretel de Mídia",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = ColorDarkObsidian
                            )
                            Surface(
                                shape = RoundedCornerShape(9999.dp),
                                color = ColorBurntOrange.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Dono",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ColorBurntOrange,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = event.title,
                            fontSize = 12.5.sp,
                            color = TextSecondary,
                            maxLines = 1
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Fechar",
                            tint = ColorDarkObsidian
                        )
                    }
                }

                Divider(color = BorderWarm, thickness = 0.8.dp)

                // BOTÕES DE AÇÃO: ESCOLHER DA GALERIA (Photo Picker) OU AMOSTRA RÁPIDA
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ColorBurntOrange,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .shadow(3.dp, RoundedCornerShape(14.dp), spotColor = Color(0x47D87A56))
                            .testTag("open_photo_picker_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Selecionar Fotos da Galeria",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    // Opção de adicionar fotos de demonstração com um toque
                    OutlinedButton(
                        onClick = {
                            val nextSample = samplePresetPhotos.firstOrNull { it !in event.mediaReel }
                                ?: samplePresetPhotos.random()
                            onAddMedia(listOf(nextSample))
                        },
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ColorTeal),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorTeal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Adicionar Foto de Amostra em Alta Resolução",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // LISTA DAS FOTOS ATUAIS DO CARRETEL COM OPÇÃO DE EXCLUIR
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Fotos no Carretel:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorDarkObsidian
                        )
                        Text(
                            text = "${event.mediaReel.size} mídias",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    if (event.mediaReel.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = BgSecondary,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(32.dp)
                                )
                                Text(
                                    text = "Nenhuma foto no carretel ainda",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "Suba fotos para destacar seu encontro no Radar.",
                                    fontSize = 11.5.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            itemsIndexed(event.mediaReel) { index, url ->
                                Box(
                                    modifier = Modifier
                                        .size(90.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(1.dp, BorderWarm, RoundedCornerShape(12.dp))
                                ) {
                                    AsyncImage(
                                        model = url,
                                        contentDescription = "Foto $index",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )

                                    // Botão Excluir Foto
                                    Surface(
                                        onClick = { onRemoveMedia(url) },
                                        shape = CircleShape,
                                        color = Color.Black.copy(alpha = 0.7f),
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(4.dp)
                                            .size(24.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Remover foto",
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }

                                    // Indicador de número
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color.Black.copy(alpha = 0.6f),
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(4.dp)
                                    ) {
                                        Text(
                                            text = "#${index + 1}",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Rodapé com botão Concluído
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BgSecondary,
                        contentColor = ColorDarkObsidian
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Concluído", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
