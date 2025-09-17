package com.fmdev.civilacademy.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.fmdev.civilacademy.R

val Gotham = FontFamily(
    Font(R.font.gotham_light, FontWeight.Light),
    Font(R.font.gotham_book, FontWeight.Normal),
    Font(R.font.gotham_black, FontWeight.Black),
)

val AkzidenzGroteskBQ = FontFamily(
    // Regular
    Font(R.font.akzidenz_grotesk_bq_reg, FontWeight.Light),
    Font(R.font.akzidenz_grotesk_bq_ext, FontWeight.Normal),

    // Medium
    Font(R.font.akzidenz_grotesk_bq_med_ext, FontWeight.Black),
)

val Typography = Typography(
    titleMedium = TextStyle(
        fontWeight = FontWeight.Black,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Light,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)