package com.fmdev.civilacademy.presentation.screen.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fmdev.civilacademy.presentation.screen.login.validation.FieldValidationType
import com.fmdev.civilacademy.presentation.screen.component.viewmodel.ConfirmDialogWithFieldsSideEffect
import com.fmdev.civilacademy.presentation.screen.component.viewmodel.ConfirmDialogWithFieldsUiEvent
import com.fmdev.civilacademy.presentation.screen.component.viewmodel.ConfirmDialogWithFieldsViewModel
import com.fmdev.civilacademy.presentation.screen.login.component.CustomLoginTextField
import com.fmdev.civilacademy.presentation.screen.login.model.UiResult

@Composable
fun ConfirmDialogWithFields(
    viewModel: ConfirmDialogWithFieldsViewModel,
    title: String,
    firstFieldLabel: String,
    secondFieldLabel: String,
    confirmButtonText: String,
    cancelButtonText: String,
    firstFieldType: FieldValidationType = FieldValidationType.NONE,
    secondFieldType: FieldValidationType = FieldValidationType.NONE,
    onConfirm: (String) -> Unit,
    onCancel: () -> Unit,
    uiResult: UiResult,
    onAnimationComplete: () -> Unit,
    explainingText: String? = null,
    isDarkTheme: Boolean
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is ConfirmDialogWithFieldsSideEffect.Close -> onCancel()
                is ConfirmDialogWithFieldsSideEffect.Confirm -> onConfirm(effect.firstFieldValue)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = title.uppercase()
            )
        },
        text = {
            Column {
                explainingText?.let {
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = explainingText
                    )
                }
                CustomLoginTextField(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    value = uiState.firstFieldValue,
                    onValueChange = {
                        viewModel.onEvent(
                            ConfirmDialogWithFieldsUiEvent.FirstFieldChanged(it, firstFieldType)
                        )
                    },
                    label = firstFieldLabel.uppercase(),
                    isFocused = uiState.isFirstFieldFocused,
                    onFocusChange = {
                        viewModel.onEvent(
                            ConfirmDialogWithFieldsUiEvent.FirstFieldFocusChanged(it)
                        )
                    },
                    isPasswordField = firstFieldType == FieldValidationType.PASSWORD,
                    supportingText = uiState.firstFieldValidationError,
                    isDarkTheme = isDarkTheme,
                    textLetterSpacing = 0.sp,
                    labelFontSize = 13.sp,
                )
                CustomLoginTextField(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    value = uiState.secondFieldValue,
                    onValueChange = {
                        viewModel.onEvent(
                            ConfirmDialogWithFieldsUiEvent.SecondFieldChanged(it, secondFieldType)
                        )
                    },
                    label = secondFieldLabel.uppercase(),
                    isFocused = uiState.isSecondFieldFocused,
                    onFocusChange = {
                        viewModel.onEvent(
                            ConfirmDialogWithFieldsUiEvent.SecondFieldFocusChanged(it)
                        )
                    },
                    isPasswordField = secondFieldType == FieldValidationType.PASSWORD,
                    supportingText = uiState.secondFieldValidationError,
                    isDarkTheme = isDarkTheme,
                    textLetterSpacing = 0.sp,
                    labelFontSize = 13.sp,
                )
            }
        },
        confirmButton = {
            LoadingButton(
                state = uiResult,
                lambdas = LoadingButtonLambdas(
                    onClick = {
                        viewModel.onEvent(
                            ConfirmDialogWithFieldsUiEvent.ConfirmButtonClicked(
                                firstFieldType,
                                secondFieldType
                            )
                        )
                        if (uiState.areFieldsValid) onConfirm(uiState.firstFieldValue)
                    },
                    onAnimationComplete = {
                        onAnimationComplete()
                        viewModel.onEvent(
                            ConfirmDialogWithFieldsUiEvent.CloseDialog
                        )
                    }
                ),
                content = LoadingButtonContent(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    text = confirmButtonText
                )
            )
        },
        dismissButton = {
            TextButton(onClick = {
                viewModel.onEvent(
                    ConfirmDialogWithFieldsUiEvent.CloseDialog
                )
            }) {
                Text(text = cancelButtonText.uppercase())
            }
        }
    )
}