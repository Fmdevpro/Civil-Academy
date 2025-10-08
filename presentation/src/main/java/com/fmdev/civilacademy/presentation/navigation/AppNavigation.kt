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
import com.fmdev.civilacademy.presentation.screen.login.screen.LoginScreen
import com.fmdev.civilacademy.presentation.screen.main.MainUiEvent
import com.fmdev.civilacademy.presentation.screen.main.MainViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    isDarkTheme: Boolean,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {}
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = LoginNavigation,
                modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
            ) {
                composable<LoginNavigation> {
                    LoginScreen(
                        navigateToRegister = { },
                        navigateToProfile = { },
                        hideSystemUI = { mainViewModel.onEvent(MainUiEvent.HideSystemUI) },
                        isDarkTheme = isDarkTheme,
                        loginViewModel = hiltViewModel()
                    )
                }
            }
        }
    }
}