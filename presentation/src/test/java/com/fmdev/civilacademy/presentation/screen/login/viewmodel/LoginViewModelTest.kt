package com.fmdev.civilacademy.presentation.screen.login.viewmodel

import android.content.Intent
import app.cash.turbine.test
import com.fmdev.civilacademy.androidshared.provider.GoogleSignInClientProvider
import com.fmdev.civilacademy.androidshared.provider.GoogleSignInResultHandler
import com.fmdev.civilacademy.domain.errors.FirebaseError
import com.fmdev.civilacademy.domain.errors.GoogleSignInError
import com.fmdev.civilacademy.domain.errors.LoginError
import com.fmdev.civilacademy.domain.errors.ResetPasswordError
import com.fmdev.civilacademy.domain.errors.SendUserEmailVerificationError
import com.fmdev.civilacademy.domain.model.User
import com.fmdev.civilacademy.domain.usecase.login.LoginUseCase
import com.fmdev.civilacademy.domain.usecase.login.ResetPasswordUseCase
import com.fmdev.civilacademy.domain.usecase.login.SendVerificationEmailUseCase
import com.fmdev.civilacademy.domain.usecase.login.SignInWithGoogleUseCase
import com.fmdev.civilacademy.domain.usecase.login.SignOutUseCase
import com.fmdev.civilacademy.domain.usecase.validation.AreFieldsValidUseCase
import com.fmdev.civilacademy.domain.usecase.validation.ValidateEmailUseCase
import com.fmdev.civilacademy.domain.usecase.validation.ValidatePasswordUseCase
import com.fmdev.civilacademy.domain.usecase.validation.ValidationError
import com.fmdev.civilacademy.domain.usecase.validation.ValidationResult
import com.fmdev.civilacademy.presentation.R
import com.fmdev.civilacademy.presentation.screen.login.model.UiResult
import com.fmdev.civilacademy.shared.model.DataResult
import com.fmdev.civilacademy.shared.provider.ResourceProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel
    private lateinit var resourceProvider: ResourceProvider
    private lateinit var loginUseCase: LoginUseCase
    private lateinit var resetPasswordUseCase: ResetPasswordUseCase
    private lateinit var resendVerificationEmailUseCase: SendVerificationEmailUseCase
    private lateinit var signOutUseCase: SignOutUseCase
    private lateinit var signInWithGoogleUseCase: SignInWithGoogleUseCase
    private lateinit var googleSignInClientProvider: GoogleSignInClientProvider
    private lateinit var googleSignInResultHandler: GoogleSignInResultHandler
    private lateinit var validateEmailUseCase: ValidateEmailUseCase
    private lateinit var validatePasswordUseCase: ValidatePasswordUseCase
    private lateinit var areFieldsValidUseCase: AreFieldsValidUseCase

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        resourceProvider = mock()
        loginUseCase = mock()
        resetPasswordUseCase = mock()
        resendVerificationEmailUseCase = mock()
        signOutUseCase = mock()
        signInWithGoogleUseCase = mock()
        googleSignInClientProvider = mock()
        googleSignInResultHandler = mock()
        validateEmailUseCase = mock()
        validatePasswordUseCase = mock()
        areFieldsValidUseCase = mock()

        setupDefaultMockBehavior()

        viewModel = LoginViewModel(
            resourceProvider = resourceProvider,
            loginUseCase = loginUseCase,
            resetPasswordUseCase = resetPasswordUseCase,
            resendVerificationEmailUseCase = resendVerificationEmailUseCase,
            signOutUseCase = signOutUseCase,
            signInWithGoogleUseCase = signInWithGoogleUseCase,
            googleSignInClientProvider = googleSignInClientProvider,
            googleSignInResultHandler = googleSignInResultHandler,
            validateEmailUseCase = validateEmailUseCase,
            validatePasswordUseCase = validatePasswordUseCase,
            areFieldsValidUseCase = areFieldsValidUseCase,
            ioDispatcher = testDispatcher
        )
    }

    private fun setupDefaultMockBehavior() {
        whenever(resourceProvider.getString(any())).thenReturn("Error message")
        whenever(validateEmailUseCase(any())).thenReturn(ValidationResult.Valid)
        whenever(validatePasswordUseCase(any())).thenReturn(ValidationResult.Valid)
        whenever(areFieldsValidUseCase(any(), any())).thenReturn(true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have empty values`() {
        // When
        val state = viewModel.uiState.value

        // Then
        assertEquals("", state.email)
        assertEquals("", state.password)
        assertFalse(state.isEmailFocused)
        assertFalse(state.isPasswordFocused)
        assertEquals(ValidationResult.Empty, state.emailValidationResult)
        assertEquals(ValidationResult.Empty, state.passwordValidationResult)
        assertFalse(state.showResetPasswordDialog)
        assertFalse(state.showEmailNotVerifiedDialog)
        assertEquals(UiResult.Idle, state.loginState)
        assertEquals(UiResult.Idle, state.googleLoginState)
        assertEquals(UiResult.Idle, state.updatePasswordState)
        assertFalse(state.areFieldsValid)
    }

    @Test
    fun `email changed should update state and validate`() = runTest {
        // Given
        val email = "test@example.com"
        whenever(validateEmailUseCase(email)).thenReturn(ValidationResult.Valid)
        whenever(areFieldsValidUseCase(ValidationResult.Valid, ValidationResult.Empty)).thenReturn(false)

        // When
        viewModel.onEvent(LoginUiEvent.EmailChanged(email))
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(email, state.email)
        verify(validateEmailUseCase).invoke(email)
    }

    @Test
    fun `password changed should update state and validate`() = runTest {
        // Given
        val password = "Password123!"
        whenever(validatePasswordUseCase(password)).thenReturn(ValidationResult.Valid)
        whenever(areFieldsValidUseCase(ValidationResult.Empty, ValidationResult.Valid)).thenReturn(false)

        // When
        viewModel.onEvent(LoginUiEvent.PasswordChanged(password))
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(password, state.password)
        verify(validatePasswordUseCase).invoke(password)
    }

    @Test
    fun `email focus changed should update state`() = runTest {
        // Given - Estado inicial
        assertFalse(viewModel.uiState.value.isEmailFocused)

        // When - Gana foco
        viewModel.onEvent(LoginUiEvent.EmailFocusChanged(true))
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.isEmailFocused)

        // When - Pierde foco
        viewModel.onEvent(LoginUiEvent.EmailFocusChanged(false))
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.isEmailFocused)
    }

    @Test
    fun `password focus changed should update state`() = runTest {
        // Given - Estado inicial
        assertFalse(viewModel.uiState.value.isPasswordFocused)

        // When
        viewModel.onEvent(LoginUiEvent.PasswordFocusChanged(true))
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.isPasswordFocused)
    }

    @Test
    fun `toggle reset password dialog should update state`() = runTest {
        // Given - Estado inicial
        assertFalse(viewModel.uiState.value.showResetPasswordDialog)

        // When - Muestra diálogo
        viewModel.onEvent(LoginUiEvent.ToggleResetPasswordDialog(true))
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.showResetPasswordDialog)

        // When - Oculta diálogo
        viewModel.onEvent(LoginUiEvent.ToggleResetPasswordDialog(false))
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.showResetPasswordDialog)
    }

    @Test
    fun `toggle email not verified dialog should update state`() = runTest {
        // Given - Estado inicial
        assertFalse(viewModel.uiState.value.showEmailNotVerifiedDialog)

        // When
        viewModel.onEvent(LoginUiEvent.ToggleEmailNotVerifiedDialog(true))
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.showEmailNotVerifiedDialog)
    }

    @Test
    fun `successful login should navigate to home`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "Password123!"
        val user = User("123", "User", email,
            isEmailVerified = true,
            isGoogleUser = false
        )

        whenever(loginUseCase(email, password)).thenReturn(DataResult.Success(user))
        whenever(areFieldsValidUseCase(any(), any())).thenReturn(true)

        viewModel.onEvent(LoginUiEvent.EmailChanged(email))
        viewModel.onEvent(LoginUiEvent.PasswordChanged(password))
        advanceUntilIdle()

        // When
        viewModel.sideEffect.test {
            viewModel.onEvent(LoginUiEvent.Login)

            advanceUntilIdle()
            // Se avanza el tiempo para asegurar que el StateFlow ha emitido el resultado y el sideEffect se ha recolectado
            advanceTimeBy(1500)

            // Then
            val sideEffect = awaitItem()
            assertTrue(sideEffect is LoginSideEffect.NavigateToHome)
            assertEquals(UiResult.Success, viewModel.uiState.value.loginState)

            verify(signOutUseCase).invoke()
            verify(loginUseCase).invoke(email, password)
        }
    }

    @Test
    fun `login with invalid fields should not call login use case`() = runTest {
        // Given
        whenever(areFieldsValidUseCase(any(), any())).thenReturn(false)

        // When
        viewModel.onEvent(LoginUiEvent.Login)
        advanceUntilIdle()

        // Then
        verify(loginUseCase, never()).invoke(any(), any())
    }

    @Test
    fun `login error should show toast and update state`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "wrongpassword"
        val errorMessage = "Wrong password"

        whenever(loginUseCase(email, password))
            .thenReturn(DataResult.Error(LoginError.WrongPassword))
        whenever(resourceProvider.getString(R.string.error_message__login__wrong_password))
            .thenReturn(errorMessage)
        whenever(areFieldsValidUseCase(any(), any())).thenReturn(true)

        viewModel.onEvent(LoginUiEvent.EmailChanged(email))
        viewModel.onEvent(LoginUiEvent.PasswordChanged(password))
        advanceUntilIdle()

        // When
        viewModel.sideEffect.test {
            viewModel.onEvent(LoginUiEvent.Login)
            advanceUntilIdle()

            // Then
            val sideEffect = awaitItem()
            assertTrue(sideEffect is LoginSideEffect.ShowToast)
            assertEquals(errorMessage, (sideEffect as LoginSideEffect.ShowToast).message)
            assertEquals(UiResult.Error, viewModel.uiState.value.loginState)
        }
    }

    @Test
    fun `email not verified error should show dialog`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "Password123!"

        whenever(loginUseCase(email, password))
            .thenReturn(DataResult.Error(LoginError.EmailNotVerified))
        whenever(areFieldsValidUseCase(any(), any())).thenReturn(true)

        viewModel.onEvent(LoginUiEvent.EmailChanged(email))
        viewModel.onEvent(LoginUiEvent.PasswordChanged(password))
        advanceUntilIdle()

        // When
        viewModel.sideEffect.test {
            viewModel.onEvent(LoginUiEvent.Login)
            advanceUntilIdle()

            // Then
            awaitItem() // Toast message
            assertTrue(viewModel.uiState.value.showEmailNotVerifiedDialog)
        }
    }

    @Test
    fun `get google sign in intent should emit launch side effect`() = runTest {
        // Given
        val mockIntent = mock<Intent>()
        whenever(googleSignInClientProvider.getSignInIntent()).thenReturn(mockIntent)

        // When
        viewModel.sideEffect.test {
            viewModel.onEvent(LoginUiEvent.GetGoogleSignInIntent)
            advanceUntilIdle()

            // Then
            val sideEffect = awaitItem()
            assertTrue(sideEffect is LoginSideEffect.LaunchGoogleSignIn)
            assertEquals(mockIntent, (sideEffect as LoginSideEffect.LaunchGoogleSignIn).intent)
        }
    }

    @Test
    fun `handle google sign in result with valid token should sign in`() = runTest {
        // Given
        val mockIntent = mock<Intent>()
        val idToken = "valid-id-token"
        val user = User("123", "User", "test@example.com",
            isEmailVerified = true,
            isGoogleUser = false
        )

        whenever(googleSignInResultHandler.getIdToken(mockIntent)).thenReturn(idToken)
        whenever(signInWithGoogleUseCase(idToken)).thenReturn(DataResult.Success(user))

        // When
        viewModel.sideEffect.test {
            viewModel.onEvent(LoginUiEvent.HandleGoogleSignInResult(mockIntent))

            advanceUntilIdle()
            // Avanzar el tiempo para asegurar la recolección del side effect.
            advanceTimeBy(1500)

            // Then
            val sideEffect = awaitItem()
            assertTrue(sideEffect is LoginSideEffect.NavigateToHome)
            assertEquals(UiResult.Success, viewModel.uiState.value.googleLoginState)
        }
    }

    @Test
    fun `handle google sign in result with null token should show error`() = runTest {
        // Given
        val mockIntent = mock<Intent>()
        whenever(googleSignInResultHandler.getIdToken(mockIntent)).thenReturn(null)

        // When
        viewModel.sideEffect.test {
            viewModel.onEvent(LoginUiEvent.HandleGoogleSignInResult(mockIntent))
            advanceUntilIdle()

            // Then
            val sideEffect = awaitItem()
            assertTrue(sideEffect is LoginSideEffect.ShowToast)
            assertEquals(UiResult.Error, viewModel.uiState.value.googleLoginState)
        }
    }

    @Test
    fun `google sign in error should show toast`() = runTest {
        // Given
        val mockIntent = mock<Intent>()
        val idToken = "valid-id-token"
        val errorMessage = "Google sign in error"

        whenever(googleSignInResultHandler.getIdToken(mockIntent)).thenReturn(idToken)
        whenever(signInWithGoogleUseCase(idToken))
            .thenReturn(DataResult.Error(GoogleSignInError.UnknownError))
        whenever(resourceProvider.getString(R.string.error_message__google_sign_in__unknown_error))
            .thenReturn(errorMessage)

        // When
        viewModel.sideEffect.test {
            viewModel.onEvent(LoginUiEvent.HandleGoogleSignInResult(mockIntent))
            advanceUntilIdle()

            // Then
            val sideEffect = awaitItem()
            assertTrue(sideEffect is LoginSideEffect.ShowToast)
            assertEquals(errorMessage, (sideEffect as LoginSideEffect.ShowToast).message)
            assertEquals(UiResult.Error, viewModel.uiState.value.googleLoginState)
        }
    }

    @Test
    fun `update password success should send side effect`() = runTest {
        // Given
        val email = "test@example.com"
        whenever(resetPasswordUseCase(email)).thenReturn(DataResult.Success(Unit))

        // When
        viewModel.sideEffect.test {
            viewModel.onEvent(LoginUiEvent.UpdatePassword(email))
            advanceUntilIdle()

            // Then
            val sideEffect = awaitItem()
            assertTrue(sideEffect is LoginSideEffect.PasswordResetEmailSent)
            assertEquals(UiResult.Success, viewModel.uiState.value.updatePasswordState)
        }
    }

    @Test
    fun `update password error should show toast`() = runTest {
        // Given
        val email = ""
        val errorMessage = "Empty email"

        whenever(resetPasswordUseCase(email))
            .thenReturn(DataResult.Error(ResetPasswordError.EmptyEmail))
        whenever(resourceProvider.getString(R.string.error_message__reset_password__empty_email))
            .thenReturn(errorMessage)

        // When
        viewModel.sideEffect.test {
            viewModel.onEvent(LoginUiEvent.UpdatePassword(email))
            advanceUntilIdle()

            // Then
            val sideEffect = awaitItem()
            assertTrue(sideEffect is LoginSideEffect.ShowToast)
            assertEquals(errorMessage, (sideEffect as LoginSideEffect.ShowToast).message)
            assertEquals(UiResult.Error, viewModel.uiState.value.updatePasswordState)
        }
    }

    @Test
    fun `resend verification email success should send side effect`() = runTest {
        // Given
        whenever(resendVerificationEmailUseCase()).thenReturn(DataResult.Success(Unit))

        // When
        viewModel.sideEffect.test {
            viewModel.onEvent(LoginUiEvent.ResendVerificationEmail)
            advanceUntilIdle()

            // Then
            val sideEffect = awaitItem()
            assertTrue(sideEffect is LoginSideEffect.VerificationEmailSent)
            assertEquals(UiResult.Success, viewModel.uiState.value.resendVerificationEmailState)
        }
    }

    @Test
    fun `resend verification email error should show toast`() = runTest {
        // Given
        val errorMessage = "Unknown error"

        whenever(resendVerificationEmailUseCase())
            .thenReturn(DataResult.Error(SendUserEmailVerificationError.UnknownError))
        whenever(resourceProvider.getString(R.string.error_message__resend_verification_email__unknown_error))
            .thenReturn(errorMessage)

        // When
        viewModel.sideEffect.test {
            viewModel.onEvent(LoginUiEvent.ResendVerificationEmail)
            advanceUntilIdle()

            // Then
            val sideEffect = awaitItem()
            assertTrue(sideEffect is LoginSideEffect.ShowToast)
            assertEquals(errorMessage, (sideEffect as LoginSideEffect.ShowToast).message)
            assertEquals(UiResult.Error, viewModel.uiState.value.resendVerificationEmailState)
        }
    }

    @Test
    fun `validate email should update validation result`() = runTest {
        // Given
        val email = "test@example.com"
        whenever(validateEmailUseCase(email)).thenReturn(ValidationResult.Valid)
        whenever(areFieldsValidUseCase(ValidationResult.Valid, ValidationResult.Empty)).thenReturn(false)

        viewModel.onEvent(LoginUiEvent.EmailChanged(email))
        advanceUntilIdle()

        // When
        viewModel.onEvent(LoginUiEvent.ValidateEmail)
        advanceUntilIdle()

        // Then
        assertEquals(ValidationResult.Valid, viewModel.uiState.value.emailValidationResult)
        // Se llama 2 veces: una en EmailChanged y otra en ValidateEmail
        verify(validateEmailUseCase, times(2)).invoke(email)
    }

    @Test
    fun `validate password should update validation result`() = runTest {
        // Given
        val password = "Password123!"
        whenever(validatePasswordUseCase(password)).thenReturn(ValidationResult.Valid)
        whenever(areFieldsValidUseCase(ValidationResult.Empty, ValidationResult.Valid)).thenReturn(false)

        viewModel.onEvent(LoginUiEvent.PasswordChanged(password))
        advanceUntilIdle()

        // When
        viewModel.onEvent(LoginUiEvent.ValidatePassword)
        advanceUntilIdle()

        // Then
        assertEquals(ValidationResult.Valid, viewModel.uiState.value.passwordValidationResult)
        // Se llama 2 veces: una en PasswordChanged y otra en ValidatePassword
        verify(validatePasswordUseCase, times(2)).invoke(password)
    }

    @Test
    fun `clean up state should reset UI states to idle`() = runTest {
        // Given
        // Establecer un estado distinto a Idle para las pruebas
        viewModel.onEvent(LoginUiEvent.EmailChanged("test@example.com")) // Cambia email
        whenever(loginUseCase(any(), any())).thenReturn(DataResult.Success(User("id", "name", "email",
            isEmailVerified = true,
            isGoogleUser = false
        )))
        viewModel.onEvent(LoginUiEvent.Login)
        advanceUntilIdle()

        // When
        viewModel.onEvent(LoginUiEvent.CleanUpState)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(UiResult.Idle, state.loginState)
        assertEquals(UiResult.Idle, state.googleLoginState)
        assertEquals(UiResult.Idle, state.updatePasswordState)
    }

    @Test
    fun `firebase network error should map correctly`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "Password123!"
        val errorMessage = "Network error"

        whenever(loginUseCase(email, password))
            .thenReturn(DataResult.Error(FirebaseError.NetworkError))
        whenever(resourceProvider.getString(R.string.error_message__firebase__network_error))
            .thenReturn(errorMessage)
        whenever(areFieldsValidUseCase(any(), any())).thenReturn(true)

        viewModel.onEvent(LoginUiEvent.EmailChanged(email))
        viewModel.onEvent(LoginUiEvent.PasswordChanged(password))
        advanceUntilIdle()

        // When
        viewModel.sideEffect.test {
            viewModel.onEvent(LoginUiEvent.Login)
            advanceUntilIdle()

            // Then
            val sideEffect = awaitItem()
            assertTrue(sideEffect is LoginSideEffect.ShowToast)
            assertEquals(errorMessage, (sideEffect as LoginSideEffect.ShowToast).message)
        }
    }

    @Test
    fun `firebase invalid credentials error should map correctly`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "Password123!"
        val errorMessage = "Invalid credentials"

        whenever(loginUseCase(email, password))
            .thenReturn(DataResult.Error(FirebaseError.InvalidCredentials))
        whenever(resourceProvider.getString(R.string.error_message__firebase__invalid_credentials))
            .thenReturn(errorMessage)
        whenever(areFieldsValidUseCase(any(), any())).thenReturn(true)

        viewModel.onEvent(LoginUiEvent.EmailChanged(email))
        viewModel.onEvent(LoginUiEvent.PasswordChanged(password))
        advanceUntilIdle()

        // When
        viewModel.sideEffect.test {
            viewModel.onEvent(LoginUiEvent.Login)
            advanceUntilIdle()

            // Then
            val sideEffect = awaitItem()
            assertTrue(sideEffect is LoginSideEffect.ShowToast)
            assertEquals(errorMessage, (sideEffect as LoginSideEffect.ShowToast).message)
        }
    }

    @Test
    fun `firebase user disabled error should map correctly`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "Password123!"
        val errorMessage = "User disabled"

        whenever(loginUseCase(email, password))
            .thenReturn(DataResult.Error(FirebaseError.UserDisabled))
        whenever(resourceProvider.getString(R.string.error_message__firebase__user_disabled))
            .thenReturn(errorMessage)
        whenever(areFieldsValidUseCase(any(), any())).thenReturn(true)

        viewModel.onEvent(LoginUiEvent.EmailChanged(email))
        viewModel.onEvent(LoginUiEvent.PasswordChanged(password))
        advanceUntilIdle()

        // When
        viewModel.sideEffect.test {
            viewModel.onEvent(LoginUiEvent.Login)
            advanceUntilIdle()

            // Then
            val sideEffect = awaitItem()
            assertTrue(sideEffect is LoginSideEffect.ShowToast)
            assertEquals(errorMessage, (sideEffect as LoginSideEffect.ShowToast).message)
        }
    }

    @Test
    fun `firebase too many requests error should map correctly`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "Password123!"
        val errorMessage = "Too many requests"

        whenever(loginUseCase(email, password))
            .thenReturn(DataResult.Error(FirebaseError.TooManyRequests))
        whenever(resourceProvider.getString(R.string.error_message__firebase__too_many_requests))
            .thenReturn(errorMessage)
        whenever(areFieldsValidUseCase(any(), any())).thenReturn(true)

        viewModel.onEvent(LoginUiEvent.EmailChanged(email))
        viewModel.onEvent(LoginUiEvent.PasswordChanged(password))
        advanceUntilIdle()

        // When
        viewModel.sideEffect.test {
            viewModel.onEvent(LoginUiEvent.Login)
            advanceUntilIdle()

            // Then
            val sideEffect = awaitItem()
            assertTrue(sideEffect is LoginSideEffect.ShowToast)
            assertEquals(errorMessage, (sideEffect as LoginSideEffect.ShowToast).message)
        }
    }

    @Test
    fun `email and password validation should update areFieldsValid`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "Password123!"

        whenever(validateEmailUseCase(email)).thenReturn(ValidationResult.Valid)
        whenever(validatePasswordUseCase(password)).thenReturn(ValidationResult.Valid)
        whenever(areFieldsValidUseCase(ValidationResult.Valid, ValidationResult.Valid)).thenReturn(true)

        // When
        viewModel.onEvent(LoginUiEvent.EmailChanged(email))
        advanceUntilIdle()
        viewModel.onEvent(LoginUiEvent.PasswordChanged(password))
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.areFieldsValid)
    }

    @Test
    fun `invalid email should make fields invalid`() = runTest {
        // Given
        val email = "invalid-email"
        val password = "Password123!"

        whenever(validateEmailUseCase(email))
            .thenReturn(ValidationResult.Invalid(ValidationError.INVALID_EMAIL_FORMAT))
        whenever(validatePasswordUseCase(password)).thenReturn(ValidationResult.Valid)
        whenever(areFieldsValidUseCase(
            ValidationResult.Invalid(ValidationError.INVALID_EMAIL_FORMAT),
            ValidationResult.Valid
        )).thenReturn(false)

        // When
        viewModel.onEvent(LoginUiEvent.EmailChanged(email))
        advanceUntilIdle()
        viewModel.onEvent(LoginUiEvent.PasswordChanged(password))
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.areFieldsValid)
        assertTrue(viewModel.uiState.value.emailValidationResult is ValidationResult.Invalid)
    }

    @Test
    fun `invalid password should make fields invalid`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "short"

        whenever(validateEmailUseCase(email)).thenReturn(ValidationResult.Valid)
        whenever(validatePasswordUseCase(password))
            .thenReturn(ValidationResult.Invalid(ValidationError.PASSWORD_TOO_SHORT))
        whenever(areFieldsValidUseCase(
            ValidationResult.Valid,
            ValidationResult.Invalid(ValidationError.PASSWORD_TOO_SHORT)
        )).thenReturn(false)

        // When
        viewModel.onEvent(LoginUiEvent.EmailChanged(email))
        advanceUntilIdle()
        viewModel.onEvent(LoginUiEvent.PasswordChanged(password))
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.areFieldsValid)
        assertTrue(viewModel.uiState.value.passwordValidationResult is ValidationResult.Invalid)
    }
}