package com.fmdev.civilacademy.presentation.screen.login.screen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun LoginScreen(
    hideSystemUI: () -> Unit,
) {
    LaunchedEffect(Unit) {
        hideSystemUI()
    }
    Text("Login")
}