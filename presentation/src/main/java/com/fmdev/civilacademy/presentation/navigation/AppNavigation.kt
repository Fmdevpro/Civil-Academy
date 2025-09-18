package com.fmdev.civilacademy.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fmdev.civilacademy.presentation.MainViewModel
import com.fmdev.civilacademy.presentation.screen.login.screen.LoginScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    isDarkTheme: Boolean,
    mainViewModel: MainViewModel = hiltViewModel()
    ) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = LoginNavigation,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable<LoginNavigation> {
                    LoginScreen(
                        hideSystemUI = { mainViewModel.hideSystemUI() },
                    )
                }
            }
        }
    }
}