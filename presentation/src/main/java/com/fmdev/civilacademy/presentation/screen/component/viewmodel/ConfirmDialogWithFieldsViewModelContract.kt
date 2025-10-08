package com.fmdev.civilacademy.presentation.screen.component.viewmodel

import com.fmdev.civilacademy.presentation.screen.login.validation.FieldValidationType
import com.fmdev.civilacademy.domain.usecase.validation.ValidationResult
import com.fmdev.civilacademy.shared.constant.StringConstants.EMPTY_STRING

data class ConfirmDialogWithFieldsUiState(
    val firstFieldValue: String = EMPTY_STRING,
    val secondFieldValue: String = EMPTY_STRING,
    val isFirstFieldFocused: Boolean = false,
    val isSecondFieldFocused: Boolean = false,
    val firstFieldValidationError: ValidationResult = ValidationResult.Empty,
    val secondFieldValidationError: ValidationResult = ValidationResult.Empty,
    val areFieldsValid: Boolean = false,
    val isPasswordVisible: Boolean = false
)

sealed interface ConfirmDialogWithFieldsUiEvent {
    data class FirstFieldChanged(val newValue: String, val firstFieldType: FieldValidationType) : ConfirmDialogWithFieldsUiEvent
    data class SecondFieldChanged(val newValue: String, val secondFieldType: FieldValidationType) : ConfirmDialogWithFieldsUiEvent
    data class ConfirmButtonClicked(val firstFieldType: FieldValidationType, val secondFieldType: FieldValidationType) : ConfirmDialogWithFieldsUiEvent
    data class FirstFieldFocusChanged(val isFocused: Boolean) : ConfirmDialogWithFieldsUiEvent
    data class SecondFieldFocusChanged(val isFocused: Boolean) : ConfirmDialogWithFieldsUiEvent
    data object PasswordVisibilityToggle : ConfirmDialogWithFieldsUiEvent
    data object CloseDialog : ConfirmDialogWithFieldsUiEvent
}

sealed interface ConfirmDialogWithFieldsSideEffect {
    data class Confirm(val firstFieldValue: String) : ConfirmDialogWithFieldsSideEffect
    data object Close : ConfirmDialogWithFieldsSideEffect
}