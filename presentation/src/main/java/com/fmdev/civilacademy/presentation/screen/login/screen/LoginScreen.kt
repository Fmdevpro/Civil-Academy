package com.fmdev.civilacademy.presentation.screen.login.screen

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.fmdev.civilacademy.presentation.R
import com.fmdev.civilacademy.presentation.screen.component.ConfirmDialogWithFields
import com.fmdev.civilacademy.presentation.screen.login.component.CustomLoginLogo
import com.fmdev.civilacademy.presentation.screen.login.component.CustomLoginTextButton
import com.fmdev.civilacademy.presentation.screen.login.component.CustomLoginTextField
import com.fmdev.civilacademy.presentation.screen.login.component.KeyboardAwareScreen
import com.fmdev.civilacademy.presentation.screen.login.component.LoginHeader
import com.fmdev.civilacademy.presentation.screen.login.component.LoginLoadingButton
import com.fmdev.civilacademy.presentation.screen.login.model.UiResult
import com.fmdev.civilacademy.presentation.screen.login.validation.FieldValidationType
import com.fmdev.civilacademy.presentation.screen.login.viewmodel.LoginSideEffect
import com.fmdev.civilacademy.presentation.screen.login.viewmodel.LoginUiEvent
import com.fmdev.civilacademy.presentation.screen.login.viewmodel.LoginUiState
import com.fmdev.civilacademy.presentation.screen.login.viewmodel.LoginViewModel
import com.fmdev.civilacademy.presentation.theme.Gray200
import com.fmdev.civilacademy.shared.constant.StringConstants.EMPTY_STRING

@Composable
fun LoginScreen(
    navigateToRegister: () -> Unit,
    navigateToProfile: () -> Unit,
    hideSystemUI: () -> Unit,
    isDarkTheme: Boolean,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val uiState by loginViewModel.uiState.collectAsState()

    var isKeyboardVisible by remember { mutableStateOf(false) }
    ViewCompat.setOnApplyWindowInsetsListener(LocalView.current) { _, insets ->
        isKeyboardVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
        insets
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loginViewModel.onEvent(LoginUiEvent.HandleGoogleSignInResult(result.data))
        }
    }

    LaunchedEffect(Unit) {
        hideSystemUI()

        loginViewModel.sideEffect.collect { effect ->
            when (effect) {
                is LoginSideEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is LoginSideEffect.NavigateToHome -> {
                    navigateToProfile()
                }
                is LoginSideEffect.LaunchGoogleSignIn -> {
                    launcher.launch(effect.intent)
                }
                is LoginSideEffect.PasswordResetEmailSent -> {
                    Toast.makeText(
                        context,
                        context.getString(R.string.login_screen__reset_password_success),
                        Toast.LENGTH_LONG
                    ).show()
                }
                is LoginSideEffect.VerificationEmailSent -> {
                    Toast.makeText(
                        context,
                        context.getString(R.string.login_screen__verification_email_sent),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
    ) {
        KeyboardAwareScreen()
        CustomLoginLogo(
            isDarkTheme = isDarkTheme,
            isKeyboardVisible = isKeyboardVisible,
            modifier = Modifier.align(Alignment.TopCenter)
        )
        LoginContent(
            uiState = uiState,
            onEvent = loginViewModel::onEvent,
            isDarkTheme = isDarkTheme,
            isKeyBoardVisible = isKeyboardVisible,
            clearFocus = { focusManager.clearFocus() },
            onForgotPasswordClick = {
                loginViewModel.onEvent(LoginUiEvent.ToggleResetPasswordDialog(true))
            },
            navigateToRegister = navigateToRegister
        )
        LoginEmailNotVerifiedDialog(
            showEmailNotVerifiedDialog = uiState.showEmailNotVerifiedDialog,
            focusManager = focusManager,
            onEvent = loginViewModel::onEvent,
            resendVerificationEmailUiResult = uiState.resendVerificationEmailState,
            isDarkTheme = isDarkTheme
        )
        LoginResetPasswordDialog(
            showResetPasswordDialog = uiState.showResetPasswordDialog,
            focusManager = focusManager,
            onEvent = loginViewModel::onEvent,
            updatePasswordUiResult = uiState.updatePasswordState,
            isDarkTheme = isDarkTheme
        )
    }
}

@Composable
private fun LoginContent(
    uiState: LoginUiState,
    onEvent: (LoginUiEvent) -> Unit,
    isDarkTheme: Boolean,
    isKeyBoardVisible: Boolean,
    onForgotPasswordClick: () -> Unit,
    clearFocus: () -> Unit,
    navigateToRegister: () -> Unit
) {
    val animatedPaddingTop by animateDpAsState(
        targetValue = if (isKeyBoardVisible) 50.dp else if (isDarkTheme) 280.dp else 240.dp,
        label = EMPTY_STRING
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = animatedPaddingTop),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LoginHeader(
            isDarkTheme = isDarkTheme,
            titleTextId = R.string.login_screen__title_text__welcome,
            subtitleTextId = R.string.login_screen__text__sign_in
        )
        LoginFields(
            uiState = uiState,
            onEvent = onEvent,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            isDarkTheme = isDarkTheme,
            onForgotPasswordClick = onForgotPasswordClick
        )
        LoginButtons(
            onEvent = onEvent,
            isDarkTheme = isDarkTheme,
            clearFocus = clearFocus,
            navigateToRegister = navigateToRegister,
            loginState = uiState.loginState,
            areFieldsValid = uiState.areFieldsValid
        )
    }
}

@Composable
private fun LoginFields(
    uiState: LoginUiState,
    onEvent: (LoginUiEvent) -> Unit,
    modifier: Modifier,
    isDarkTheme: Boolean,
    onForgotPasswordClick: () -> Unit
) {
    CustomLoginTextField(
        value = uiState.email,
        onValueChange = {
            onEvent(LoginUiEvent.EmailChanged(it))
        },
        label = stringResource(id = R.string.login_screen__label_text_field__email).uppercase(),
        isFocused = uiState.isEmailFocused,
        onFocusChange = { onEvent(LoginUiEvent.EmailFocusChanged(it)) },
        isPasswordField = false,
        supportingText = uiState.emailValidationResult,
        isDarkTheme = isDarkTheme
    )
    CustomLoginTextField(
        value = uiState.password,
        onValueChange = {
            onEvent(LoginUiEvent.PasswordChanged(it))
        },
        label = stringResource(id = R.string.login_screen__label_text_field__password).uppercase(),
        isFocused = uiState.isPasswordFocused,
        onFocusChange = { onEvent(LoginUiEvent.PasswordFocusChanged(it)) },
        isPasswordField = true,
        supportingText = uiState.passwordValidationResult,
        isDarkTheme = isDarkTheme
    )
    ForgotPasswordButton(modifier, isDarkTheme, onForgotPasswordClick)
}

@Composable
private fun ForgotPasswordButton(
    modifier: Modifier,
    isDarkTheme: Boolean,
    onForgotPasswordClick: () -> Unit
) {
    CustomLoginTextButton(
        onClick = onForgotPasswordClick,
        isDarkTheme = isDarkTheme,
        modifier = modifier.offset(y = (-14).dp),
        text = stringResource(id = R.string.login_screen__text__forget_your_password),
    )
}

@Composable
private fun LoginButtons(
    onEvent: (LoginUiEvent) -> Unit,
    isDarkTheme: Boolean,
    clearFocus: () -> Unit = {},
    navigateToRegister: () -> Unit,
    loginState: UiResult,
    areFieldsValid: Boolean,
) {
    Spacer(modifier = Modifier.height(16.dp))
    LoginLoadingButton(
        modifier = Modifier,
        loadingState = loginState,
        onclick = {
            clearFocus()
            onEvent(LoginUiEvent.Login)
        },
        onAnimationComplete = {
            onEvent(LoginUiEvent.CleanUpState)
        },
        isDarkTheme = isDarkTheme,
        text = stringResource(id = R.string.login_screen__login_text),
        paddingHorizontal = 32.dp,
        enabled = areFieldsValid
    )
    Spacer(modifier = Modifier.height(16.dp))
    GoogleLoginButton(
        modifier = Modifier,
        onClick = {
            clearFocus()
            onEvent(LoginUiEvent.GetGoogleSignInIntent)
        },
        isDarkTheme = isDarkTheme
    )
    Spacer(modifier = Modifier.height(12.dp))
    GoToRegisterButton(onClick = navigateToRegister, isDarkTheme)
}

@Composable
fun GoogleLoginButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isDarkTheme: Boolean
) {
    val backgroundColor = if (isDarkTheme) Color.White else Color.Black
    val textColor = if (isDarkTheme) Color.Black else Gray200

    val shape = RoundedCornerShape(13.dp)
    Surface(
        modifier = modifier
            .clip(shape)
            .clickable(onClick = onClick),
        color = backgroundColor,
        shape = shape
    ) {
        Row(
            modifier = Modifier
                .padding(start = 18.dp, end = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_google_icon),
                contentDescription = "Google Logo",
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                modifier = Modifier.padding(vertical = 12.dp),
                text = stringResource(id = R.string.login_screen__google_login_text),
                fontSize = if (isDarkTheme) 20.sp else 25.sp,
                style = MaterialTheme.typography.titleMedium,
                color = textColor
            )
        }
    }
}


@Composable
private fun GoToRegisterButton(onClick: () -> Unit, isDarkTheme: Boolean) {
    CustomLoginTextButton(
        onClick = onClick,
        isDarkTheme = isDarkTheme,
        text = stringResource(id = R.string.login_screen__text_btn__new_user).uppercase()
    )
}

@Composable
private fun LoginEmailNotVerifiedDialog(
    showEmailNotVerifiedDialog: Boolean,
    focusManager: FocusManager,
    onEvent: (LoginUiEvent) -> Unit,
    resendVerificationEmailUiResult: UiResult,
    isDarkTheme: Boolean
) {
    if (showEmailNotVerifiedDialog) {
        clearFocus(focusManager, onEvent)
        ConfirmDialogWithFields(
            title = stringResource(R.string.login_screen__resend_verification_email_dialog__title),
            firstFieldLabel = stringResource(R.string.login_screen__resend_verification_email_dialog__first_field_label),
            secondFieldLabel = stringResource(R.string.login_screen__resend_verification_email_dialog__second_field_label),
            confirmButtonText = stringResource(R.string.login_screen__resend_verification_email_dialog__confirm_button_text),
            cancelButtonText = stringResource(R.string.login_screen__resend_verification_email_dialog__cancel_button_text),
            firstFieldType = FieldValidationType.EMAIL,
            secondFieldType = FieldValidationType.EMAIL,
            onConfirm = { onEvent(LoginUiEvent.ResendVerificationEmail) },
            onCancel = {
                onEvent(LoginUiEvent.ToggleEmailNotVerifiedDialog(false))
            },
            uiResult = resendVerificationEmailUiResult,
            isDarkTheme = isDarkTheme,
            onAnimationComplete = {
                onEvent(LoginUiEvent.CleanUpState)
                onEvent(LoginUiEvent.ToggleEmailNotVerifiedDialog(false))
            },
            viewModel = hiltViewModel(),
            explainingText = stringResource(R.string.login_screen__resend_verification_email_dialog__explaining_text)
        )
    }
}

@Composable
fun LoginResetPasswordDialog(
    showResetPasswordDialog: Boolean,
    focusManager: FocusManager,
    onEvent: (LoginUiEvent) -> Unit,
    updatePasswordUiResult: UiResult,
    isDarkTheme: Boolean
) {
    if (showResetPasswordDialog) {
        clearFocus(focusManager, onEvent)
        ConfirmDialogWithFields(
            title = stringResource(R.string.login_screen__reset_password_dialog__title),
            firstFieldLabel = stringResource(R.string.login_screen__reset_password_dialog__first_field_label),
            secondFieldLabel = stringResource(R.string.login_screen__reset_password_dialog__second_field_label),
            confirmButtonText = stringResource(R.string.login_screen__reset_password_dialog__confirm_button_text),
            cancelButtonText = stringResource(R.string.login_screen__reset_password_dialog__cancel_button_text),
            firstFieldType = FieldValidationType.EMAIL,
            secondFieldType = FieldValidationType.EMAIL,
            onConfirm = { email -> onEvent(LoginUiEvent.UpdatePassword(email)) },
            onCancel = {
                onEvent(LoginUiEvent.ToggleResetPasswordDialog(false))
            },
            uiResult = updatePasswordUiResult,
            isDarkTheme = isDarkTheme,
            onAnimationComplete = {
                onEvent(LoginUiEvent.CleanUpState)
                onEvent(LoginUiEvent.ToggleResetPasswordDialog(false))
            },
            viewModel = hiltViewModel(),
            explainingText = stringResource(R.string.login_screen__reset_password_dialog__explaining_text)
        )
    }
}

private fun clearFocus(focusManager: FocusManager, onEvent: (LoginUiEvent) -> Unit) {
    focusManager.clearFocus()
    onEvent(LoginUiEvent.EmailFocusChanged(false))
    onEvent(LoginUiEvent.PasswordFocusChanged(false))
}