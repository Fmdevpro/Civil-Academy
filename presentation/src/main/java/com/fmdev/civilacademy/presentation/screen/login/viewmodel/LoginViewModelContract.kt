package com.fmdev.civilacademy.presentation.screen.login.viewmodel

import android.content.Intent
import com.fmdev.civilacademy.domain.usecase.validation.ValidationResult
import com.fmdev.civilacademy.presentation.screen.login.model.UiResult
import com.fmdev.civilacademy.shared.constant.StringConstants.EMPTY_STRING

data class LoginUiState(
    val email: String = EMPTY_STRING,
    val password: String = EMPTY_STRING,
    val isEmailFocused: Boolean = false,
    val isPasswordFocused: Boolean = false,
    val emailValidationResult: ValidationResult = ValidationResult.Empty,
    val passwordValidationResult: ValidationResult = ValidationResult.Empty,
    val showResetPasswordDialog: Boolean = false,
    val loginState: UiResult = UiResult.Idle,
    val googleLoginState: UiResult = UiResult.Idle,
    val updatePasswordState: UiResult = UiResult.Idle,
    val resendVerificationEmailState: UiResult = UiResult.Idle,
    val showEmailNotVerifiedDialog: Boolean = false,
    val areFieldsValid: Boolean = false
)

sealed interface LoginUiEvent {
    data class EmailChanged(val email: String) : LoginUiEvent
    data class PasswordChanged(val password: String) : LoginUiEvent
    data class EmailFocusChanged(val isFocused: Boolean) : LoginUiEvent
    data class PasswordFocusChanged(val isFocused: Boolean) : LoginUiEvent
    data class ToggleResetPasswordDialog(val show: Boolean) : LoginUiEvent
    data class ToggleEmailNotVerifiedDialog(val show: Boolean) : LoginUiEvent
    data class HandleGoogleSignInResult(val intent: Intent?) : LoginUiEvent
    data class UpdatePassword(val email: String) : LoginUiEvent
    data object Login : LoginUiEvent
    data object GetGoogleSignInIntent : LoginUiEvent
    data object ResendVerificationEmail : LoginUiEvent
    data object ValidateEmail : LoginUiEvent
    data object ValidatePassword : LoginUiEvent
    data object CleanUpState : LoginUiEvent
}

sealed interface LoginSideEffect {
    data class ShowToast(val message: String) : LoginSideEffect
    data class LaunchGoogleSignIn(val intent: Intent) : LoginSideEffect
    data object NavigateToHome : LoginSideEffect
    data object PasswordResetEmailSent : LoginSideEffect
    data object VerificationEmailSent : LoginSideEffect
}
