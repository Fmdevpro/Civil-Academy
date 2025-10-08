package com.fmdev.civilacademy.presentation.screen.login.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun LoginHeader(isDarkTheme: Boolean, @StringRes titleTextId: Int, @StringRes subtitleTextId: Int) {
    val text = stringResource(id = titleTextId)
    CustomTitleText(
        text = if (isDarkTheme) text.uppercase() else text,
        isDarkTheme = isDarkTheme,
        modifier = Modifier.padding(top = 24.dp)
    )
    CustomSubtitleText(
        text = stringResource(id = subtitleTextId),
        isDarkTheme = isDarkTheme
    )
    Spacer(modifier = Modifier.height(20.dp))
}