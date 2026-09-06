package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.*
import com.example.ui.components.ObsidianLogoEmblem
import com.example.ui.components.ObsidianPrismRatingIcon
import com.example.ui.components.ObsidianStationPortalIcon
import com.example.ui.components.OrganicCirclesBackground
import com.example.ui.theme.*

/**
 * PÁGINA EXCLUSIVA DE CRIAÇÃO E HOSPEDAGEM DE ENCONTROS (Obsidian Event Host Screen)
 * 
 * Substitui o antigo modal AlertDialog por uma página dedicada imersiva,
 * com suporte a definição de perímetro de geofence, método de validação de presença física (PoP),
 * carretel de fotos/mídia do encontro, categorização e visibilidade da tribo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    onNavigateBack: () -> Unit,
    onCreateEvent: (
        title: String,
        description: String,
        category: EventCategory,
        location: String,
        visibility: VisibilityTier,
        method: CheckInMethod,
        mediaReel: List<String>
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(EventCategory.MEETUP) }
    var selectedVisibility by remember { mutableStateOf(VisibilityTier.PUBLICO) }
    var selectedMethod by remember { mutableStateOf(CheckInMethod.QR_DYNAMIC) }
    var selectedRadiusMeters by remember { mutableIntStateOf(100) }
    var mediaList by remember { mutableStateOf<List<String>>(emptyList()) }

    val isFormValid = title.isNotBlank() && location.isNotBlank()

    // Android Photo Picker nativo sem permissões invasivas
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 10)
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            mediaList = (mediaList + uris.map { it.toString() }).distinct()
        }
    }

    val samplePresetPhotos = listOf(
        "https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=800&q=80",
        "https://images.unsplash.com/photo-1515187029135-18ee286d815b?w=800&q=80",
        "https://images.unsplash.com/photo-1511578314322-379afb476865?w=800&q=80",
        "https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=800&q=80"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCanvas)
    ) {
        // Padrão de Círculos Orgânicos de Fundo
        OrganicCirclesBackground()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = BgSurface.copy(alpha = 0.95f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Botão Voltar Circular
                            Surface(
                                onClick = onNavigateBack,
                                shape = CircleShape,
                                color = BgSecondary,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm),
                                modifier = Modifier
                                    .size(40.dp)
                                    .testTag("create_event_back_btn")
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Voltar",
                                        tint = ColorDarkObsidian,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = "Hospedar Encontro",
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ColorDarkObsidian
                                )
                                Text(
                                    text = "Radar & Presença Real da Tribo",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        // Emblema Obsidian no Header
                        ObsidianLogoEmblem(size = 32.dp)
                    }
                }
            },
            bottomBar = {
                // Barra de Ação Fixa de Publicação
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = BgSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onNavigateBack,
                            shape = RoundedCornerShape(9999.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorDarkObsidian),
                            modifier = Modifier
                                .weight(0.9f)
                                .height(50.dp)
                        ) {
                            Text(text = "Cancelar", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }

                        Button(
                            onClick = {
                                if (isFormValid) {
                                    onCreateEvent(
                                        title.trim(),
                                        description.trim().ifBlank { "Encontro presencial da Tribo Obsidian." },
                                        selectedCategory,
                                        location.trim(),
                                        selectedVisibility,
                                        selectedMethod,
                                        mediaList
                                    )
                                }
                            },
                            enabled = isFormValid,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ColorBurntOrange,
                                disabledContainerColor = ColorBurntOrange.copy(alpha = 0.4f),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(9999.dp),
                            modifier = Modifier
                                .weight(1.5f)
                                .height(50.dp)
                                .shadow(if (isFormValid) 4.dp else 0.dp, RoundedCornerShape(9999.dp), spotColor = Color(0x47D87A56))
                                .testTag("submit_create_event_btn")
                        ) {
                            ObsidianStationPortalIcon(size = 20.dp, tint = Color.White, animated = isFormValid)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Publicar no Radar",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // HERO BANNER DO ANFITRIÃO
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = Color(0xFF161F20),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, ColorTeal.copy(alpha = 0.5f)),
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(9999.dp),
                                color = ColorBurntOrange.copy(alpha = 0.25f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ColorBurntOrange)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(ColorBurntOrange, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "ANFITRIÃO OBSIDIAN",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = ColorBurntOrangeLight,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }

                            Text(
                                text = "+100 OQ Recompensa",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorMustard
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Hospede um Encontro com Prova de Presença Real",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Ao publicar no Radar, participantes próximos verão seu ponto no sonar físico e validarão a presença física no seu totem geofence.",
                            fontSize = 12.5.sp,
                            color = Color(0xFFB0BEC5),
                            lineHeight = 17.sp
                        )
                    }
                }

                // SEÇÃO 1: INFORMAÇÕES BÁSICAS
                SectionHeader(title = "1. Identidade & Proposta", subtitle = "Dê nome, categoria e a vibe do encontro")

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = BgSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm),
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Nome do Evento
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Nome do Encontro *") },
                            placeholder = { Text("Ex: Resenha de Design & Tech") },
                            leadingIcon = {
                                Icon(Icons.Default.Celebration, contentDescription = null, tint = ColorBurntOrange)
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ColorBurntOrange,
                                unfocusedBorderColor = BorderWarm,
                                focusedLabelColor = ColorBurntOrange
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("event_title_input")
                        )

                        // Descrição / Proposta
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Proposta & Programação") },
                            placeholder = { Text("Como será a dinâmica, quem tocará, assuntos a debater...") },
                            leadingIcon = {
                                Icon(Icons.Default.Notes, contentDescription = null, tint = ColorTeal)
                            },
                            minLines = 3,
                            maxLines = 5,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ColorTeal,
                                unfocusedBorderColor = BorderWarm,
                                focusedLabelColor = ColorTeal
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Seleção de Categoria
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Categoria do Encontro:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorDarkObsidian
                            )

                            // Grid/Row de Categorias Estilizadas
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                EventCategory.values().toList().chunked(2).forEach { rowCats ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        rowCats.forEach { cat ->
                                            val isSelected = selectedCategory == cat
                                            Surface(
                                                onClick = { selectedCategory = cat },
                                                shape = RoundedCornerShape(12.dp),
                                                color = if (isSelected) ColorTealLight else BgSecondary,
                                                border = androidx.compose.foundation.BorderStroke(
                                                    1.2.dp,
                                                    if (isSelected) ColorTeal else BorderWarm
                                                ),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    val categoryIcon = when (cat) {
                                                        EventCategory.TECNOLOGIA -> Icons.Default.Code
                                                        EventCategory.MEETUP -> Icons.Default.Groups
                                                        EventCategory.SHOW -> Icons.Default.MusicNote
                                                        EventCategory.BAR -> Icons.Default.LocalBar
                                                        EventCategory.ESPACO -> Icons.Default.AccountBalance
                                                        EventCategory.PAREDAO -> Icons.Default.VolumeUp
                                                        EventCategory.PRIVADO -> Icons.Default.Lock
                                                    }
                                                    Icon(
                                                        imageVector = categoryIcon,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(16.dp),
                                                        tint = if (isSelected) ColorTeal else TextSecondary
                                                    )
                                                    Text(
                                                        text = cat.label,
                                                        fontSize = 12.5.sp,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                        color = if (isSelected) ColorTeal else ColorDarkObsidian,
                                                        maxLines = 1
                                                    )
                                                }
                                            }
                                        }
                                        if (rowCats.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // SEÇÃO 2: LOCALIZAÇÃO & PERÍMETRO FÍSICO
                SectionHeader(title = "2. Localização & Raio de Geofence", subtitle = "Onde a presença física real será validada")

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = BgSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm),
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("Local ou Endereço Físico *") },
                            placeholder = { Text("Ex: Hub Criativo - Rua Mourato Coelho, 140") },
                            leadingIcon = {
                                Icon(Icons.Default.Place, contentDescription = null, tint = ColorBurntOrange)
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ColorBurntOrange,
                                unfocusedBorderColor = BorderWarm,
                                focusedLabelColor = ColorBurntOrange
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("event_location_input")
                        )

                        // Raio do Geofence
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Raio de Tolerância Geofence:",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ColorDarkObsidian
                                )
                                Text(
                                    text = "${selectedRadiusMeters}m de alcance",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ColorTeal
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(30, 50, 100, 150).forEach { radius ->
                                    val isSelected = selectedRadiusMeters == radius
                                    Surface(
                                        onClick = { selectedRadiusMeters = radius },
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) ColorTeal else BgSecondary,
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isSelected) ColorTeal else BorderWarm
                                        ),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${radius}m",
                                                fontSize = 12.5.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) Color.White else ColorDarkObsidian
                                            )
                                        }
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = ColorTeal,
                                    modifier = Modifier.size(15.dp).padding(top = 1.dp)
                                )
                                Text(
                                    text = "O sistema Obsidian impede check-in remoto e só ativa validação quando o participante estiver dentro deste raio físico.",
                                    fontSize = 11.5.sp,
                                    color = TextMuted,
                                    lineHeight = 15.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // SEÇÃO 3: PROTOCOLO DE VALIDAÇÃO DE PRESENÇA (PoP)
                SectionHeader(title = "3. Protocolo de Check-in", subtitle = "Como os participantes validarão que estão presentes")

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = BgSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm),
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CheckInMethod.values().forEach { method ->
                            val isSelected = selectedMethod == method
                            Surface(
                                onClick = { selectedMethod = method },
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) ColorTealLight else BgSecondary,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.2.dp,
                                    if (isSelected) ColorTeal else BorderWarm
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Ícone representativo do método
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) ColorTeal else BgSurface),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        when (method) {
                                            CheckInMethod.QR_DYNAMIC -> {
                                                ObsidianStationPortalIcon(
                                                    size = 20.dp,
                                                    tint = if (isSelected) Color.White else ColorTeal
                                                )
                                            }
                                            CheckInMethod.GPS_ONLY -> {
                                                Icon(
                                                    Icons.Default.NearMe,
                                                    contentDescription = null,
                                                    tint = if (isSelected) Color.White else ColorTeal,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                            CheckInMethod.BLE_BEACON -> {
                                                Icon(
                                                    Icons.Default.Podcasts,
                                                    contentDescription = null,
                                                    tint = if (isSelected) Color.White else ColorTeal,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = method.label,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) ObsidianTealDark else ColorDarkObsidian
                                        )
                                        Text(
                                            text = when (method) {
                                                CheckInMethod.QR_DYNAMIC -> "Token de 60 segundos exibido no totem ou tela do anfitrião."
                                                CheckInMethod.GPS_ONLY -> "Coordenadas com precisão e tolerância de geofence PostGIS."
                                                CheckInMethod.BLE_BEACON -> "Sinal de totem local por rádio frequência bluetooth física."
                                            },
                                            fontSize = 11.5.sp,
                                            color = TextSecondary,
                                            lineHeight = 15.sp
                                        )
                                    }

                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedMethod = method },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = ColorTeal,
                                            unselectedColor = TextMuted
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // SEÇÃO 4: VISIBILIDADE & NÍVEL DE TRIBALIDADE (RN-007)
                SectionHeader(title = "4. Acesso da Tribo (RN-007)", subtitle = "Restrinja o acesso por nível de prestígio e histórico real")

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = BgSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm),
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        VisibilityTier.values().forEach { vis ->
                            val isSelected = selectedVisibility == vis
                            Surface(
                                onClick = { selectedVisibility = vis },
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) ColorMustardLight else BgSecondary,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.2.dp,
                                    if (isSelected) ColorMustardHover else BorderWarm
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        ObsidianPrismRatingIcon(
                                            isFilled = isSelected,
                                            size = 18.dp,
                                            tint = if (isSelected) ColorMustardHover else TextMuted
                                        )
                                        Column {
                                            Text(
                                                text = vis.label,
                                                fontSize = 13.5.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) ColorDarkObsidian else TextSecondary
                                            )
                                            vis.minTier?.let { minTier ->
                                                Text(
                                                    text = "Requer histórico ${minTier.title} ou superior",
                                                    fontSize = 11.sp,
                                                    color = ColorMustardHover
                                                )
                                            }
                                        }
                                    }

                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedVisibility = vis },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = ColorMustardHover,
                                            unselectedColor = TextMuted
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // ====================================================================
                // 5. CARRETEL DE MÍDIA DO ENCONTRO (Fotos & Vídeos do Anfitrião)
                // ====================================================================
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionHeader(
                        title = "5. Carretel de Mídia do Encontro",
                        subtitle = "Suba fotos para atrair a tribo. O carretel aparecerá em destaque no Radar."
                    )

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = BgSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
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
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .height(46.dp)
                                        .testTag("create_event_pick_photos_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddPhotoAlternate,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Fotos da Galeria", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = {
                                        val nextSample = samplePresetPhotos.firstOrNull { it !in mediaList }
                                            ?: samplePresetPhotos.random()
                                        mediaList = mediaList + nextSample
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ColorTeal),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorTeal),
                                    modifier = Modifier
                                        .weight(1.1f)
                                        .height(46.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Foto Amostra", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }

                            if (mediaList.isNotEmpty()) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Fotos Selecionadas (${mediaList.size}):",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ColorDarkObsidian
                                        )
                                        Text(
                                            text = "Arraste para ver todas",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }

                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        itemsIndexed(mediaList) { index, url ->
                                            Box(
                                                modifier = Modifier
                                                    .size(85.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .border(1.dp, BorderWarm, RoundedCornerShape(12.dp))
                                            ) {
                                                AsyncImage(
                                                    model = url,
                                                    contentDescription = "Mídia $index",
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )

                                                // Botão Excluir Foto
                                                Surface(
                                                    onClick = {
                                                        mediaList = mediaList.filterIndexed { i, _ -> i != index }
                                                    },
                                                    shape = CircleShape,
                                                    color = Color.Black.copy(alpha = 0.7f),
                                                    modifier = Modifier
                                                        .align(Alignment.TopEnd)
                                                        .padding(4.dp)
                                                        .size(22.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Icon(
                                                            imageVector = Icons.Default.Close,
                                                            contentDescription = "Remover",
                                                            tint = Color.White,
                                                            modifier = Modifier.size(12.dp)
                                                        )
                                                    }
                                                }

                                                // Tag #1, #2
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
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = BgSecondary,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PhotoLibrary,
                                            contentDescription = null,
                                            tint = ColorTeal,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Text(
                                            text = "O carretel é opcional. Se não subir fotos agora, você ou sua equipe podem subir mídias diretamente pelo Radar a qualquer momento!",
                                            fontSize = 11.5.sp,
                                            color = TextSecondary,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Column {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            color = ColorDarkObsidian
        )
        Text(
            text = subtitle,
            fontSize = 12.sp,
            color = TextSecondary
        )
    }
}
