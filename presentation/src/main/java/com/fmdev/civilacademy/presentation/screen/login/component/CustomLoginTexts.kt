package com.fmdev.civilacademy.presentation.screen.login.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomTitleText(text: String, isDarkTheme: Boolean, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontSize = if (isDarkTheme) 36.sp else 50.sp,
        color = if (isDarkTheme) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier
    )
}

@Composable
fun CustomSubtitleText(text: String, isDarkTheme: Boolean) {
    Text(
        text = text.uppercase(),
        fontSize = 13.sp,
        fontWeight = FontWeight.W400,
        color = if (isDarkTheme) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(top = 4.dp),
        letterSpacing = 3.sp
    )
}

@Composable
fun CustomBodyText(text: String, textSize: TextUnit) {
    Text(
        text = text,
        fontSize = textSize,
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.bodyMedium,
    )
}
