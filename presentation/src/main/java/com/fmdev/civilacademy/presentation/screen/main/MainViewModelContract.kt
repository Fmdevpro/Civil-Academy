package com.fmdev.civilacademy.presentation.screen.main

data class MainUiState(
    val isSystemUIVisible: Boolean = false
)

sealed interface MainUiEvent {
    data object HideSystemUI : MainUiEvent
    data object ShowSystemUI : MainUiEvent
}

sealed interface MainSideEffect {
    data object HideSystemUI : MainSideEffect
    data object ShowSystemUI : MainSideEffect
}