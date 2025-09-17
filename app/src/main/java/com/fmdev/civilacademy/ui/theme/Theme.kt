package com.fmdev.civilacademy.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

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
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme =
        if (darkTheme) DarkColorScheme
        else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = appTypography(darkTheme),
        content = content
    )
}

fun applyFontFamily(fontFamily: FontFamily): Typography {
    return Typography(
        displayLarge = Typography.displayLarge.copy(fontFamily = fontFamily),
        displayMedium = Typography.displayMedium.copy(fontFamily = fontFamily),
        displaySmall = Typography.displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = Typography.headlineLarge.copy(fontFamily = fontFamily),
        headlineMedium = Typography.headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = Typography.headlineSmall.copy(fontFamily = fontFamily),
        titleLarge = Typography.titleLarge.copy(fontFamily = fontFamily),
        titleMedium = Typography.titleMedium.copy(fontFamily = fontFamily),
        titleSmall = Typography.titleSmall.copy(fontFamily = fontFamily),
        bodyLarge = Typography.bodyLarge.copy(fontFamily = fontFamily),
        bodyMedium = Typography.bodyMedium.copy(fontFamily = fontFamily),
        bodySmall = Typography.bodySmall.copy(fontFamily = fontFamily),
        labelLarge = Typography.labelLarge.copy(fontFamily = fontFamily),
        labelMedium = Typography.labelMedium.copy(fontFamily = fontFamily),
        labelSmall = Typography.labelSmall.copy(fontFamily = fontFamily),
    )
}

@Composable
fun appTypography(darkTheme: Boolean): Typography {
    return if (darkTheme) {
        applyFontFamily(AkzidenzGroteskBQ)
    } else {
        applyFontFamily(Gotham)
    }
}