package com.fmdev.civilacademy.presentation.screen.login.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fmdev.civilacademy.presentation.theme.Gray200

@Composable
fun CustomLoginButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    isDarkTheme: Boolean,
    icon: Painter? = null
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(13.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isDarkTheme) MaterialTheme.colorScheme.primary else Color.Black
        ),
        modifier = modifier
    ) {
        icon?.let {
            Icon(
                painter = it,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp).size(25.dp),
            )
        }
        Text(
            text = text.uppercase(),
            fontSize = if (isDarkTheme) 20.sp else 25.sp,
            style = MaterialTheme.typography.titleMedium,
            color = if (isDarkTheme) Color.Black else Gray200,
        )
    }
}

@Composable
fun CustomLoginTextButton(
    onClick: () -> Unit,
    text: String,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = text.uppercase(),
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            style = MaterialTheme.typography.bodyMedium,
            letterSpacing = 0.2.sp,
            lineHeight = 14.sp,
            fontWeight = if (isDarkTheme) FontWeight.Medium else FontWeight.Light,
            color = if (isDarkTheme) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
        )
    }
}