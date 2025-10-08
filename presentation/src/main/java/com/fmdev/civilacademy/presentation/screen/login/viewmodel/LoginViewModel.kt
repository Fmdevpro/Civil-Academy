package com.fmdev.civilacademy.presentation.screen.login.viewmodel

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fmdev.civilacademy.androidshared.provider.GoogleSignInClientProvider
import com.fmdev.civilacademy.androidshared.provider.GoogleSignInResultHandler
import com.fmdev.civilacademy.di.IoDispatcher
import com.fmdev.civilacademy.domain.errors.FirebaseError
import com.fmdev.civilacademy.domain.errors.GoogleSignInError
import com.fmdev.civilacademy.domain.errors.LoginError
import com.fmdev.civilacademy.domain.errors.ResetPasswordError
import com.fmdev.civilacademy.domain.errors.SendUserEmailVerificationError
import com.fmdev.civilacademy.domain.usecase.login.LoginUseCase
import com.fmdev.civilacademy.domain.usecase.login.ResetPasswordUseCase
import com.fmdev.civilacademy.domain.usecase.login.SendVerificationEmailUseCase
import com.fmdev.civilacademy.domain.usecase.login.SignInWithGoogleUseCase
import com.fmdev.civilacademy.domain.usecase.login.SignOutUseCase
import com.fmdev.civilacademy.domain.usecase.validation.AreFieldsValidUseCase
import com.fmdev.civilacademy.domain.usecase.validation.ValidateEmailUseCase
import com.fmdev.civilacademy.domain.usecase.validation.ValidatePasswordUseCase
import com.fmdev.civilacademy.presentation.R
import com.fmdev.civilacademy.presentation.screen.login.model.UiResult
import com.fmdev.civilacademy.shared.model.DataError
import com.fmdev.civilacademy.shared.model.DataResult
import com.fmdev.civilacademy.shared.provider.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val loginUseCase: LoginUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val resendVerificationEmailUseCase: SendVerificationEmailUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val googleSignInClientProvider: GoogleSignInClientProvider,
    private val googleSignInResultHandler: GoogleSignInResultHandler,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val areFieldsValidUseCase: AreFieldsValidUseCase,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _sideEffect = Channel<LoginSideEffect>(Channel.BUFFERED)
    val sideEffect: Flow<LoginSideEffect> = _sideEffect.receiveAsFlow()

    fun onEvent(event: LoginUiEvent) {
        when (event) {
            is LoginUiEvent.EmailChanged -> handleEmailChanged(event.email)
            is LoginUiEvent.PasswordChanged -> handlePasswordChanged(event.password)
            is LoginUiEvent.EmailFocusChanged -> handleEmailFocusChanged(event.isFocused)
            is LoginUiEvent.PasswordFocusChanged -> handlePasswordFocusChanged(event.isFocused)
            is LoginUiEvent.ToggleResetPasswordDialog -> handleToggleResetPasswordDialog(event.show)
            is LoginUiEvent.ToggleEmailNotVerifiedDialog -> handleToggleEmailNotVerifiedDialog(event.show)
            is LoginUiEvent.Login -> handleLogin()
            is LoginUiEvent.GetGoogleSignInIntent -> handleGetGoogleSignInIntent()
            is LoginUiEvent.HandleGoogleSignInResult -> handleGoogleSignInResult(event.intent)
            is LoginUiEvent.UpdatePassword -> handleUpdatePassword(event.email)
            is LoginUiEvent.ResendVerificationEmail -> handleResendVerificationEmail()
            is LoginUiEvent.ValidateEmail -> handleValidateEmail()
            is LoginUiEvent.ValidatePassword -> handleValidatePassword()
            is LoginUiEvent.CleanUpState -> handleCleanUpState()
        }
    }

    private fun handleEmailChanged(newEmail: String) {
        _uiState.update { it.copy(email = newEmail) }
        handleValidateEmail()
    }

    private fun handlePasswordChanged(newPassword: String) {
        _uiState.update { it.copy(password = newPassword) }
        handleValidatePassword()
    }

    private fun handleEmailFocusChanged(isFocused: Boolean) {
        _uiState.update { it.copy(isEmailFocused = isFocused) }
    }

    private fun handlePasswordFocusChanged(isFocused: Boolean) {
        _uiState.update { it.copy(isPasswordFocused = isFocused) }
    }

    private fun handleToggleResetPasswordDialog(show: Boolean) {
        _uiState.update { it.copy(showResetPasswordDialog = show) }
    }

    private fun handleToggleEmailNotVerifiedDialog(show: Boolean) {
        _uiState.update { it.copy(showEmailNotVerifiedDialog = show) }
    }

    private fun handleLogin() {
        if (!areFieldsValid()) return

        viewModelScope.launch(ioDispatcher) {
            signOut()
            _uiState.update { it.copy(loginState = UiResult.Loading) }
            val result = withContext(ioDispatcher) {
                loginUseCase(
                    email = _uiState.value.email.trim(),
                    password = _uiState.value.password.trim()
                )
            }
            when (result) {
                is DataResult.Success -> {
                    _uiState.update { it.copy(loginState = UiResult.Success) }
                    delay(1500)
                    _sideEffect.send(LoginSideEffect.NavigateToHome)
                }
                is DataResult.Error -> {
                    val message = mapFirebaseError(result.dataError)
                        ?: when (result.dataError) {
                            is LoginError.EmptyEmail -> resourceProvider.getString(R.string.error_message__login__empty_email)
                            is LoginError.EmptyPassword -> resourceProvider.getString(R.string.error_message__login__empty_password)
                            is LoginError.UnknownError -> resourceProvider.getString(R.string.error_message__login__unknown_error)
                            is LoginError.EmailNotRegistered -> resourceProvider.getString(R.string.error_message__login__email_not_registered)
                            is LoginError.WrongPassword -> resourceProvider.getString(R.string.error_message__login__wrong_password)
                            is LoginError.NetworkError -> resourceProvider.getString(R.string.error_message__login__network_error)
                            is LoginError.EmailNotVerified -> {
                                _uiState.update { it.copy(showEmailNotVerifiedDialog = true) }
                                resourceProvider.getString(R.string.error_message__login__email_not_verified)
                            }
                            else -> resourceProvider.getString(R.string.error_message__login__unknown_error)
                        }
                    _uiState.update { it.copy(loginState = UiResult.Error) }
                    _sideEffect.send(LoginSideEffect.ShowToast(message))
                }
                is DataResult.Loading -> {
                    _uiState.update { it.copy(loginState = UiResult.Loading) }
                }
            }
        }
    }

    private fun handleGetGoogleSignInIntent() {
        viewModelScope.launch {
            val intent = withContext(ioDispatcher) {
                googleSignInClientProvider.getSignInIntent()
            }
            _sideEffect.send(LoginSideEffect.LaunchGoogleSignIn(intent))
        }
    }

    private fun handleGoogleSignInResult(intent: Intent?) {
        val idToken = googleSignInResultHandler.getIdToken(intent)
        if (!idToken.isNullOrBlank()) {
            signInWithGoogle(idToken)
        } else {
            _uiState.update { it.copy(googleLoginState = UiResult.Error) }
            viewModelScope.launch {
                _sideEffect.send(LoginSideEffect.ShowToast(
                    resourceProvider.getString(R.string.error_message__google_sign_in__invalid_id_token))
                )
            }
        }
    }

    private fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(googleLoginState = UiResult.Loading) }
            val result = withContext(ioDispatcher) {
                signInWithGoogleUseCase(idToken)
            }
            when (result) {
                is DataResult.Success -> {
                    _uiState.update { it.copy(googleLoginState = UiResult.Success) }
                    delay(1500)
                    _sideEffect.send(LoginSideEffect.NavigateToHome)
                }
                is DataResult.Error -> {
                    val message = mapFirebaseError(result.dataError)
                        ?: when (result.dataError) {
                            is GoogleSignInError.EmptyIdToken -> resourceProvider.getString(R.string.error_message__google_sign_in__empty_id_token)
                            is GoogleSignInError.UnknownError -> resourceProvider.getString(R.string.error_message__google_sign_in__unknown_error)
                            else -> resourceProvider.getString(R.string.error_message__google_sign_in__unknown_error)
                        }
                    _uiState.update { it.copy(googleLoginState = UiResult.Error) }
                    _sideEffect.send(LoginSideEffect.ShowToast(message))
                }
                is DataResult.Loading -> {
                    _uiState.update { it.copy(googleLoginState = UiResult.Loading) }
                }
            }
        }
    }

    private fun handleUpdatePassword(email: String) {
        _uiState.update { it.copy(updatePasswordState = UiResult.Loading) }

        viewModelScope.launch {
            val result = withContext(ioDispatcher) {
                resetPasswordUseCase(email)
            }
            when (result) {
                is DataResult.Success -> {
                    _uiState.update { it.copy(updatePasswordState = UiResult.Success) }
                    _sideEffect.send(LoginSideEffect.PasswordResetEmailSent)
                }
                is DataResult.Error -> {
                    val message = mapFirebaseError(result.dataError)
                        ?: when (result.dataError) {
                            is ResetPasswordError.EmptyEmail -> resourceProvider.getString(R.string.error_message__reset_password__empty_email)
                            is ResetPasswordError.UnknownError -> resourceProvider.getString(R.string.error_message__reset_password__unknown_error)
                            else -> resourceProvider.getString(R.string.error_message__reset_password__unknown_error)
                        }
                    _uiState.update { it.copy(updatePasswordState = UiResult.Error) }
                    _sideEffect.send(LoginSideEffect.ShowToast(message))
                }
                is DataResult.Loading -> {
                    _uiState.update { it.copy(updatePasswordState = UiResult.Loading) }
                }
            }
        }
    }

    private fun handleResendVerificationEmail() {
        _uiState.update { it.copy(resendVerificationEmailState = UiResult.Loading) }

        viewModelScope.launch {
            val result = withContext(ioDispatcher) {
                resendVerificationEmailUseCase()
            }
            when (result) {
                is DataResult.Success -> {
                    _uiState.update { it.copy(resendVerificationEmailState = UiResult.Success) }
                    _sideEffect.send(LoginSideEffect.VerificationEmailSent)
                }
                is DataResult.Error -> {
                    val message = mapFirebaseError(result.dataError)
                        ?: when (result.dataError) {
                            is SendUserEmailVerificationError.EmptyEmail -> resourceProvider.getString(R.string.error_message__resend_verification_email__empty_email)
                            is SendUserEmailVerificationError.UnknownError -> resourceProvider.getString(R.string.error_message__resend_verification_email__unknown_error)
                            else -> resourceProvider.getString(R.string.error_message__resend_verification_email__unknown_error)
                        }
                    _uiState.update { it.copy(resendVerificationEmailState = UiResult.Error) }
                    _sideEffect.send(LoginSideEffect.ShowToast(message))
                }
                is DataResult.Loading -> {
                    _uiState.update { it.copy(resendVerificationEmailState = UiResult.Loading) }
                }
            }
        }
    }

    private fun handleValidateEmail() {
        _uiState.update {
            val emailValidation = validateEmailUseCase(it.email)
            it.copy(
                emailValidationResult = emailValidation,
                areFieldsValid = areFieldsValidUseCase(emailValidation, it.passwordValidationResult)
            )
        }
        _uiState.update { it.copy(areFieldsValid = areFieldsValid()) }
    }

    private fun handleValidatePassword() {
        _uiState.update {
            val passwordValidation = validatePasswordUseCase(it.password)
            it.copy(
                passwordValidationResult = passwordValidation,
                areFieldsValid = areFieldsValidUseCase(it.emailValidationResult, passwordValidation)
            )
        }
        _uiState.update { it.copy(areFieldsValid = areFieldsValid()) }
    }

    private fun handleCleanUpState() {
        _uiState.update {
            it.copy(
                loginState = UiResult.Idle,
                googleLoginState = UiResult.Idle,
                updatePasswordState = UiResult.Idle
            )
        }
    }

    private fun signOut() {
        viewModelScope.launch(ioDispatcher) {
            signOutUseCase()
        }
    }

    private fun areFieldsValid(): Boolean {
        return areFieldsValidUseCase(
            _uiState.value.emailValidationResult,
            _uiState.value.passwordValidationResult
        )
    }

    private fun mapFirebaseError(dataError: DataError): String? {
        return when (dataError) {
            is FirebaseError.NetworkError -> resourceProvider.getString(R.string.error_message__firebase__network_error)
            is FirebaseError.InvalidUser -> resourceProvider.getString(R.string.error_message__firebase__invalid_user)
            is FirebaseError.InvalidCredentials -> resourceProvider.getString(R.string.error_message__firebase__invalid_credentials)
            is FirebaseError.UserCollision -> resourceProvider.getString(R.string.error_message__firebase__user_collision)
            is FirebaseError.UserDisabled -> resourceProvider.getString(R.string.error_message__firebase__user_disabled)
            is FirebaseError.TooManyRequests -> resourceProvider.getString(R.string.error_message__firebase__too_many_requests)
            is FirebaseError.UnknownError -> resourceProvider.getString(R.string.error_message__firebase__unknown_error)
            else -> null
        }
    }
}