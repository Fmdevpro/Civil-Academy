package com.fmdev.civilacademy.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val LightColorScheme = lightColorScheme(
    // Surface Colors
    background = Primary,
    onBackground = TextPrimaryLight,
    surface = Gray100,
    onSurface = TextSecondaryLight,
    primaryContainer = Gray100,
    onPrimaryContainer = TextPrimaryLight,
    secondaryContainer = Gray300,
    onSecondaryContainer = Color.Black,
    tertiaryContainer = Gray500,
    outline = Gray600,
    outlineVariant = Gray800,
    // Accent Colors
    primary = Primary400,
    onPrimary = Gray900,
    secondary = Secondary400,
    onSecondary = Color.White,
    error = ErrorLight,
    onError = Color.White,
)

val DarkColorScheme = darkColorScheme(
    // Surface Colors
    background = Color.Black,
    onBackground = TextPrimaryDark,
    surface = Color(0xFF1E1E1E),
    onSurface = TextSecondaryDark,
    primaryContainer = Gray900,
    onPrimaryContainer = TextPrimaryDark,
    secondaryContainer = Gray850,
    onSecondaryContainer = Color.White,
    tertiaryContainer = Gray500,
    outline = Gray400,
    outlineVariant = Gray200,
    // Accent Colors
    primary = Primary300,
    onPrimary = TextPrimaryDark,
    secondary = Secondary300,
    onSecondary = TextPrimaryDark,
    error = ErrorDark,
    onError = Color.Black
)

@Composable
fun CivilAcademyTheme(
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}