package com.shuham.urbaneats.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 1. Define Light Color Scheme
private val LightColorScheme = lightColorScheme(
    primary = UrbanOrange,
    onPrimary = UrbanWhite,

    // FIX: Explicitly map the container colors you defined
    primaryContainer = UrbanOrangeLight,
    onPrimaryContainer = UrbanDarkBrown,

    background = UrbanCream,
    onBackground = UrbanDarkBrown,

    surface = UrbanWhite,
    onSurface = UrbanDarkBrown,
    onSurfaceVariant = UrbanGray,

    secondary = UrbanGray,
    onSecondary = UrbanWhite,

    outlineVariant = LightOutline,
    error = UrbanError
)

private val DarkColorScheme = darkColorScheme(
    primary = UrbanOrangeDark,
    onPrimary = DarkBackground, // Black text on bright orange button

    // FIX: Dark Mode container mapping
    primaryContainer = DarkOrangeContainer,
    onPrimaryContainer = UrbanOrangeDark, // Text matches primary color

    background = DarkBackground,
    onBackground = DarkTextPrimary,

    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    onSurfaceVariant = DarkTextSecondary,

    secondary = DarkTextSecondary,
    onSecondary = DarkBackground,

    outlineVariant = DarkOutline,
    error = UrbanError
)
@Composable
fun UrbanEatsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // 3. Status Bar Color Logic
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Set Status Bar color to match background
            window.statusBarColor = colorScheme.background.toArgb()
            // Dark icons in Light Mode, Light icons in Dark Mode
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}