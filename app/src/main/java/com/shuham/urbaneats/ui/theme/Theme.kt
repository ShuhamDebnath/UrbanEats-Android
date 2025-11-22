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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 1. Define Light Color Scheme
private val LightColorScheme = lightColorScheme(
    primary = UrbanOrange,
    onPrimary = Color.White,
    primaryContainer = UrbanOrangeLight,
    onPrimaryContainer = UrbanDarkBrown,

    background = UrbanCream,
    onBackground = UrbanDarkBrown,

    surface = UrbanWhite,
    onSurface = UrbanDarkBrown,

    secondary = UrbanGray,
    onSecondary = Color.White,

    error = UrbanError
)

// 2. Define Dark Color Scheme
private val DarkColorScheme = darkColorScheme(
    primary = DarkOrange,
    onPrimary = DarkBackground, // Black text on Orange button looks sharp
    primaryContainer = UrbanOrange,
    onPrimaryContainer = Color.White,

    background = DarkBackground,
    onBackground = DarkTextPrimary,

    surface = DarkSurface,
    onSurface = DarkTextPrimary,

    secondary = DarkTextSecondary,
    onSecondary = DarkBackground,

    error = UrbanError
)

@Composable
fun UrbanEatsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
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