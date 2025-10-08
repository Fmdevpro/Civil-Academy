package com.fmdev.civilacademy.domain.usecase.login

import com.fmdev.civilacademy.domain.errors.FirebaseError
import com.fmdev.civilacademy.domain.errors.LoginError
import com.fmdev.civilacademy.domain.model.User
import com.fmdev.civilacademy.domain.repository.AuthRepository
import com.fmdev.civilacademy.shared.model.DataResult
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

class LoginUseCaseTest {

    @Mock
    private lateinit var authRepository: AuthRepository
    private lateinit var loginUseCase: LoginUseCase

    @Before
    fun setup() {
        authRepository = mock()
        loginUseCase = LoginUseCase(authRepository)
    }

    // ==================== SUCCESS CASES ====================

    @Test
    fun `invoke with verified email should return Success`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val verifiedUser = User(
            uid = "uid123",
            displayName = "Test User",
            email = email,
            isEmailVerified = true,
            isGoogleUser = false
        )
        whenever(authRepository.login(email, password))
            .thenReturn(DataResult.Success(verifiedUser))

        // When
        val result = loginUseCase(email, password)

        // Then
        assertTrue(result is DataResult.Success)
        val user = (result as DataResult.Success).data
        assertEquals(email, user.email)
        assertTrue(user.isEmailVerified)

        verify(authRepository, times(1)).login(email, password)
    }

    @Test
    fun `invoke should call repository login exactly once`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val verifiedUser = User(
            uid = "uid123",
            displayName = "Test User",
            email = email,
            isEmailVerified = true,
            isGoogleUser = false
        )
        whenever(authRepository.login(email, password))
            .thenReturn(DataResult.Success(verifiedUser))

        // When
        loginUseCase(email, password)

        // Then
        verify(authRepository, times(1)).login(email, password)
        verifyNoMoreInteractions(authRepository)
    }

    @Test
    fun `invoke should pass exact parameters to repository`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        whenever(authRepository.login(any(), any()))
            .thenReturn(DataResult.Success(
                User("uid", "Test", email, isEmailVerified = true, isGoogleUser = false)
            ))

        // When
        loginUseCase(email, password)

        // Then
        verify(authRepository).login(eq(email), eq(password))
    }

    @Test
    fun `invoke with different emails should call repository with correct params`() = runTest {
        // Given
        val email1 = "test1@example.com"
        val email2 = "test2@example.com"
        val password = "password123"
        whenever(authRepository.login(any(), any()))
            .thenReturn(DataResult.Success(
                User("uid", "Test", "test@test.com", isEmailVerified = true, isGoogleUser = false)
            ))

        // When
        loginUseCase(email1, password)
        loginUseCase(email2, password)

        // Then
        verify(authRepository).login(email1, password)
        verify(authRepository).login(email2, password)
        verify(authRepository, times(2)).login(any(), any())
    }

    // ==================== EMAIL VERIFICATION ERRORS ====================

    @Test
    fun `invoke with unverified email should return EmailNotVerified error`() = runTest {
        // Given
        val email = "unverified@example.com"
        val password = "password123"
        whenever(authRepository.login(email, password))
            .thenReturn(DataResult.Error(LoginError.EmailNotVerified))

        // When
        val result = loginUseCase(email, password)

        // Then
        assertTrue(result is DataResult.Error)
        val error = (result as DataResult.Error).dataError
        assertTrue(error is LoginError.EmailNotVerified)

        verify(authRepository).login(email, password)
    }

    // ==================== AUTHENTICATION ERRORS ====================

    @Test
    fun `invoke with wrong password should return WrongPassword error`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "wrongpassword"
        whenever(authRepository.login(email, password))
            .thenReturn(DataResult.Error(LoginError.WrongPassword))

        // When
        val result = loginUseCase(email, password)

        // Then
        assertTrue(result is DataResult.Error)
        val error = (result as DataResult.Error).dataError
        assertTrue(error is LoginError.WrongPassword)

        verify(authRepository).login(email, password)
    }

    @Test
    fun `invoke with unregistered email should return EmailNotRegistered error`() = runTest {
        // Given
        val email = "notfound@example.com"
        val password = "password123"
        whenever(authRepository.login(email, password))
            .thenReturn(DataResult.Error(LoginError.EmailNotRegistered))

        // When
        val result = loginUseCase(email, password)

        // Then
        assertTrue(result is DataResult.Error)
        val error = (result as DataResult.Error).dataError
        assertTrue(error is LoginError.EmailNotRegistered)
    }

    @Test
    fun `invoke with Firebase InvalidCredentials should return error`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "wrongpass"
        whenever(authRepository.login(email, password))
            .thenReturn(DataResult.Error(FirebaseError.InvalidCredentials))

        // When
        val result = loginUseCase(email, password)

        // Then
        assertTrue(result is DataResult.Error)
        val error = (result as DataResult.Error).dataError
        assertTrue(error is FirebaseError.InvalidCredentials)
    }

    @Test
    fun `invoke with disabled user should return UserDisabled error`() = runTest {
        // Given
        val email = "disabled@example.com"
        val password = "password123"
        whenever(authRepository.login(email, password))
            .thenReturn(DataResult.Error(FirebaseError.UserDisabled))

        // When
        val result = loginUseCase(email, password)

        // Then
        assertTrue(result is DataResult.Error)
        val error = (result as DataResult.Error).dataError
        assertTrue(error is FirebaseError.UserDisabled)
    }

    // ==================== VALIDATION ERRORS ====================

    @Test
    fun `invoke with empty email should return EmptyEmail error`() = runTest {
        // Given
        val email = ""
        val password = "password123"
        whenever(authRepository.login(email, password))
            .thenReturn(DataResult.Error(LoginError.EmptyEmail))

        // When
        val result = loginUseCase(email, password)

        // Then
        assertTrue(result is DataResult.Error)
        val error = (result as DataResult.Error).dataError
        assertTrue(error is LoginError.EmptyEmail)
    }

    @Test
    fun `invoke with empty password should return EmptyPassword error`() = runTest {
        // Given
        val email = "test@example.com"
        val password = ""
        whenever(authRepository.login(email, password))
            .thenReturn(DataResult.Error(LoginError.EmptyPassword))

        // When
        val result = loginUseCase(email, password)

        // Then
        assertTrue(result is DataResult.Error)
        val error = (result as DataResult.Error).dataError
        assertTrue(error is LoginError.EmptyPassword)
    }

    // ==================== NETWORK ERRORS ====================

    @Test
    fun `invoke with network error should return NetworkError`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        whenever(authRepository.login(email, password))
            .thenReturn(DataResult.Error(FirebaseError.NetworkError))

        // When
        val result = loginUseCase(email, password)

        // Then
        assertTrue(result is DataResult.Error)
        val error = (result as DataResult.Error).dataError
        assertTrue(error is FirebaseError.NetworkError)
    }

    @Test
    fun `invoke with login network error should return NetworkError`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        whenever(authRepository.login(email, password))
            .thenReturn(DataResult.Error(LoginError.NetworkError))

        // When
        val result = loginUseCase(email, password)

        // Then
        assertTrue(result is DataResult.Error)
        val error = (result as DataResult.Error).dataError
        assertTrue(error is LoginError.NetworkError)
    }

    // ==================== UNKNOWN ERRORS ====================

    @Test
    fun `invoke with unknown login error should return UnknownError`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        whenever(authRepository.login(email, password))
            .thenReturn(DataResult.Error(LoginError.UnknownError))

        // When
        val result = loginUseCase(email, password)

        // Then
        assertTrue(result is DataResult.Error)
        val error = (result as DataResult.Error).dataError
        assertTrue(error is LoginError.UnknownError)
    }

    @Test
    fun `invoke with unknown firebase error should return UnknownError`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        whenever(authRepository.login(email, password))
            .thenReturn(DataResult.Error(FirebaseError.UnknownError))

        // When
        val result = loginUseCase(email, password)

        // Then
        assertTrue(result is DataResult.Error)
        val error = (result as DataResult.Error).dataError
        assertTrue(error is FirebaseError.UnknownError)
    }

    // ==================== LOADING STATE ====================

    @Test
    fun `invoke should return Loading when repository returns Loading`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        whenever(authRepository.login(email, password))
            .thenReturn(DataResult.Loading)

        // When
        val result = loginUseCase(email, password)

        // Then
        assertTrue(result is DataResult.Loading)
    }
}