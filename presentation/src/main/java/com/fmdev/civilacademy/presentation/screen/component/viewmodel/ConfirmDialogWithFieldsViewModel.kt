package com.fmdev.civilacademy.presentation.screen.component.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fmdev.civilacademy.presentation.screen.login.validation.FieldValidationType
import com.fmdev.civilacademy.domain.usecase.validation.ValidateEmailUseCase
import com.fmdev.civilacademy.domain.usecase.validation.ValidatePasswordUseCase
import com.fmdev.civilacademy.domain.usecase.validation.ValidationError
import com.fmdev.civilacademy.domain.usecase.validation.ValidationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfirmDialogWithFieldsViewModel @Inject constructor(
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ConfirmDialogWithFieldsUiState())
    val uiState: StateFlow<ConfirmDialogWithFieldsUiState> = _uiState.asStateFlow()

    private val _sideEffect = Channel<ConfirmDialogWithFieldsSideEffect>(Channel.BUFFERED)
    val sideEffect: Flow<ConfirmDialogWithFieldsSideEffect> = _sideEffect.receiveAsFlow()

    fun onEvent(event: ConfirmDialogWithFieldsUiEvent) {
        when (event) {
            is ConfirmDialogWithFieldsUiEvent.ConfirmButtonClicked -> handleConfirmButtonClicked(
                event.firstFieldType,
                event.secondFieldType
            )
            is ConfirmDialogWithFieldsUiEvent.FirstFieldChanged -> handleFirstFieldChanged(event.newValue, event.firstFieldType)
            is ConfirmDialogWithFieldsUiEvent.SecondFieldChanged -> handleSecondFieldChanged(event.newValue, event.secondFieldType)
            is ConfirmDialogWithFieldsUiEvent.FirstFieldFocusChanged -> handleFirstFieldFocusChanged(event.isFocused)
            is ConfirmDialogWithFieldsUiEvent.SecondFieldFocusChanged -> handleSecondFieldFocusChanged(event.isFocused)
            is ConfirmDialogWithFieldsUiEvent.PasswordVisibilityToggle -> handlePasswordVisibilityToggle()
            is ConfirmDialogWithFieldsUiEvent.CloseDialog -> handleCloseDialog()
        }
    }

    private fun handleFirstFieldChanged(newValue: String, firstFieldType: FieldValidationType) {
        _uiState.update { it.copy(firstFieldValue = newValue) }
        _uiState.update { it.copy(
            firstFieldValidationError = validateField(newValue, firstFieldType)
        ) }
    }

    private fun handleSecondFieldChanged(newValue: String, secondFieldType: FieldValidationType) {
        _uiState.update { it.copy(secondFieldValue = newValue) }
        _uiState.update { it.copy(
            secondFieldValidationError = validateField(newValue, secondFieldType)
        ) }
    }

    private fun handleFirstFieldFocusChanged(isFocused: Boolean) {
        _uiState.update { it.copy(isFirstFieldFocused = isFocused) }
    }

    private fun handleSecondFieldFocusChanged(isFocused: Boolean) {
        _uiState.update { it.copy(isSecondFieldFocused = isFocused) }
    }

    private fun handlePasswordVisibilityToggle() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    private fun validateFields(
        firstFieldType: FieldValidationType,
        secondFieldType: FieldValidationType
    ): Boolean {
        val firstFieldError = validateField(
            fieldValue = _uiState.value.firstFieldValue,
            validationType = firstFieldType
        )
        val secondFieldError = validateField(
            fieldValue = _uiState.value.secondFieldValue,
            validationType = secondFieldType
        )
        _uiState.update { it.copy(
            firstFieldValidationError = firstFieldError,
            secondFieldValidationError = secondFieldError
        ) }

        if (_uiState.value.firstFieldValue != _uiState.value.secondFieldValue) {
            _uiState.update { it.copy(areFieldsValid = false) }
            return false
        }
        if (firstFieldError == ValidationResult.Valid &&
            secondFieldError == ValidationResult.Valid) {
            _uiState.update { it.copy(areFieldsValid = true) }
            return true
        }
        _uiState.update { it.copy(areFieldsValid = false) }
        return false
    }

    private fun handleConfirmButtonClicked(
        firstFieldType: FieldValidationType,
        secondFieldType: FieldValidationType
    ) {
        val areFieldsValid = validateFields(firstFieldType, secondFieldType)
        if (areFieldsValid) {
            viewModelScope.launch {
                _sideEffect.send(
                    ConfirmDialogWithFieldsSideEffect.Confirm(
                        _uiState.value.firstFieldValue
                    )
                )
            }
        }
    }

    private fun validateField(
        fieldValue: String,
        validationType: FieldValidationType
    ): ValidationResult {
        return when (validationType) {
            FieldValidationType.EMAIL -> validateEmailUseCase(fieldValue)
            FieldValidationType.PASSWORD -> validatePasswordUseCase(fieldValue)
            FieldValidationType.TEXT -> when {
                fieldValue.isBlank() -> ValidationResult.Invalid(ValidationError.EMPTY_FIELD)
                else -> ValidationResult.Valid
            }
            FieldValidationType.NONE -> ValidationResult.Empty
        }
    }

    private fun handleCloseDialog() {
        cleanUpState()
        viewModelScope.launch {
            _sideEffect.send(
                ConfirmDialogWithFieldsSideEffect.Close
            )
        }
    }

    private fun cleanUpState() {
        _uiState.update { ConfirmDialogWithFieldsUiState(
            firstFieldValue = "",
            secondFieldValue = "",
            isFirstFieldFocused = false,
            isSecondFieldFocused = false,
            firstFieldValidationError = ValidationResult.Empty,
            secondFieldValidationError = ValidationResult.Empty,
            isPasswordVisible = false
        ) }
    }
}