package com.example.english_app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.runtime.CompositionLocalProvider

private val DarkColorScheme = darkColorScheme(
    primary = VibrantBlue,
    secondary = VibrantGreen,
    tertiary = VibrantOrange,
    background = Color(0xFF1A1A2E),
    surface = Color(0xFF16213E),
    surfaceVariant = Color(0xFF0F3460),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFFE8E8E8)
)

private val LightColorScheme = lightColorScheme(
    primary = VibrantBlue,
    secondary = VibrantGreen,
    tertiary = VibrantOrange,
    background = LightBackground,
    surface = CardBackground,
    surfaceVariant = SurfaceVariant,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = PrimaryText,
    onSurface = PrimaryText,
    onSurfaceVariant = SecondaryText
)

@Composable
fun ENGLISH_APPTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    fontScale: Float = 1.0f,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled to use our custom vibrant colors
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

    val currentDensity = LocalDensity.current
    val customDensity = Density(density = currentDensity.density, fontScale = currentDensity.fontScale * fontScale)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = {
            CompositionLocalProvider(LocalDensity provides customDensity) {
                content()
            }
        }
    )
}