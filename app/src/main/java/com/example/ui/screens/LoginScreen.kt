package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ObsidianLogoEmblem
import com.example.ui.components.OrganicCirclesBackground
import com.example.ui.theme.*

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCanvas)
            .testTag("login_screen")
    ) {
        // Fluid Organic Pattern Background
        OrganicCirclesBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Brand Centerpiece
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = BgSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm),
                    modifier = Modifier.shadow(4.dp, RoundedCornerShape(24.dp), spotColor = Color(0x1F0B0C10))
                ) {
                    Box(modifier = Modifier.padding(20.dp)) {
                        ObsidianLogoEmblem(size = 96.dp)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Obsidian",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp,
                    color = ColorDarkObsidian
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "A Rede Social da Presença Real",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = ColorTeal
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Validação criptográfica por PostGIS & HMAC 15s",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }

            // Warm Card Auth Box
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(20.dp), spotColor = Color(0x1F0B0C10)),
                shape = RoundedCornerShape(20.dp),
                color = BgSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Conecte sua Presença",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorDarkObsidian
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Social Auth Buttons (Google & Apple)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Google Button
                        Surface(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .clickable { onLoginSuccess() }
                                .testTag("google_login_button"),
                            color = BgSecondary,
                            shape = CircleShape,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "G",
                                    color = ColorDarkObsidian,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp
                                )
                            }
                        }

                        // Apple Button
                        Surface(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .clickable { onLoginSuccess() }
                                .testTag("apple_login_button"),
                            color = BgSecondary,
                            shape = CircleShape,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "",
                                    color = ColorDarkObsidian,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Divider "ou"
                    Row(
                        modifier = Modifier.fillMaxWidth(0.85f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = BorderWarm)
                        Text(
                            text = "ou",
                            modifier = Modifier.padding(horizontal = 12.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = BorderWarm)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Primary Action Button (btn-primary / Burnt Orange Pill)
                    Button(
                        onClick = { onLoginSuccess() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .shadow(4.dp, RoundedCornerShape(9999.dp), spotColor = Color(0x47D87A56))
                            .testTag("email_phone_login_button"),
                        shape = RoundedCornerShape(9999.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ColorBurntOrange,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Entrar com Presença",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Outline Button
                    OutlinedButton(
                        onClick = { onLoginSuccess() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(9999.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderWarm),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorDarkObsidian)
                    ) {
                        Text(
                            text = "Acessar como Convidado",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Legal & Security Disclaimer
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = ColorTeal,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Proof of Presence Certificado",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ColorTeal
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ao continuar, você aceita as diretrizes comunitárias de presença física real.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    textAlign = TextAlign.Center,
                    color = TextMuted,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
    }
}
