package com.fmdev.civilacademy.presentation.screen.login.model

sealed class UiResult {
    data object Idle : UiResult()
    data object Loading : UiResult()
    data object Success : UiResult()
    data object Error : UiResult()
}