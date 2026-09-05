package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = ObsidianBurntOrange,
    onPrimary = TextLight,
    primaryContainer = ObsidianOrangeDark,
    onPrimaryContainer = ObsidianOrangeLight,
    secondary = ObsidianTealLight,
    onSecondary = DarkObsidian,
    secondaryContainer = ObsidianTealDark,
    onSecondaryContainer = ObsidianTealContainer,
    tertiary = ObsidianMustardYellow,
    onTertiary = DarkObsidian,
    background = DarkObsidian,
    onBackground = TextLight,
    surface = DarkObsidianSurface,
    onSurface = TextLight,
    surfaceVariant = DarkObsidianBorder,
    onSurfaceVariant = TextMutedLight
)

private val LightColorScheme = lightColorScheme(
    primary = ObsidianBurntOrange,
    onPrimary = TextLight,
    primaryContainer = ObsidianOrangeContainer,
    onPrimaryContainer = ObsidianOrangeDark,
    secondary = ObsidianTeal,
    onSecondary = TextLight,
    secondaryContainer = ObsidianTealContainer,
    onSecondaryContainer = ObsidianTealDark,
    tertiary = ObsidianMustardYellow,
    onTertiary = DarkObsidian,
    background = WarmCanvas,
    onBackground = TextPrimaryDark,
    surface = WarmSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = WarmSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use Obsidian custom brand scheme by default
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
