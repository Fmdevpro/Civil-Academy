package com.fmdev.civilacademy.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.fmdev.civilacademy.presentation.navigation.AppNavigation
import com.fmdev.civilacademy.presentation.theme.CivilAcademyTheme

@Composable
fun AppContent() {
    val isDarkTheme = isSystemInDarkTheme()
    val navController = rememberNavController()

    CivilAcademyTheme(darkTheme = isDarkTheme) {
        AppNavigation(navController = navController, isDarkTheme = isDarkTheme)
    }
}