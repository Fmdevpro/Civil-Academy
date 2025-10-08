package com.fmdev.civilacademy.domain.usecase.login

import com.fmdev.civilacademy.domain.errors.FirebaseError
import com.fmdev.civilacademy.domain.errors.GoogleSignInError
import com.fmdev.civilacademy.domain.model.User
import com.fmdev.civilacademy.domain.repository.AuthRepository
import com.fmdev.civilacademy.shared.model.DataResult
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SignInWithGoogleUseCaseTest {

    @Mock
    private lateinit var authRepository: AuthRepository
    private lateinit var signInWithGoogleUseCase: SignInWithGoogleUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        signInWithGoogleUseCase = SignInWithGoogleUseCase(
            authRepository
        )
    }

    // ==================== SUCCESS CASES ====================

    @Test
    fun `invoke with valid idToken should return Success with User`() = runTest {
        // Given
        val idToken = "validGoogleIdToken"
        val email = "google@example.com"
        val mockUser = User(
            "googleUid",
            "Google User",
            email,
            isEmailVerified = true,
            isGoogleUser = true
        )
        whenever(authRepository.signInWithGoogle(idToken))
            .thenReturn(DataResult.Success(mockUser))

        // When
        val result = signInWithGoogleUseCase(idToken)

        // Then
        assertTrue(result is DataResult.Success)
        val user = result.data
        assertEquals(email, user.email)
        assertTrue(user.isGoogleUser)

        verify(authRepository).signInWithGoogle(idToken)
    }

    @Test
    fun `invoke should call repository login exactly once`() = runTest {
        // Given
        val idToken = "validGoogleIdToken"
        val email = "google@example.com"
        val mockUser = User(
            "googleUid",
            "Google User",
            email,
            isEmailVerified = true,
            isGoogleUser = true
        )
        whenever(authRepository.signInWithGoogle(idToken))
            .thenReturn(DataResult.Success(mockUser))

        // When
        signInWithGoogleUseCase(idToken)

        // Then
        verify(authRepository, times(1)).signInWithGoogle(idToken)
        verifyNoMoreInteractions(authRepository)
    }

    @Test
    fun `invoke should pass exact parameters to repository`() = runTest {
        // Given
        val idToken = "validGoogleIdToken"
        val email = "google@example.com"
        val mockUser = User(
            "googleUid",
            "Google User",
            email,
            isEmailVerified = true,
            isGoogleUser = true
        )
        whenever(authRepository.signInWithGoogle(idToken))
            .thenReturn(DataResult.Success(mockUser))

        // When
        signInWithGoogleUseCase(idToken)

        // Then
        verify(authRepository).signInWithGoogle(eq(idToken))
    }

    @Test
    fun `invoke with different emails should call repository with correct params`() = runTest {
        // Given
        val idToken1 = "validGoogleIdToken1"
        val idToken2 = "validGoogleIdToken2"
        val email1 = "google1@example.com"
        val email2 = "google2@example.com"
        val mockUser1 = User(
            "googleUid1",
            "Google User 1",
            email1,
            isEmailVerified = true,
            isGoogleUser = true
        )
        val mockUser2 = User(
            "googleUid2",
            "Google User 2",
            email2,
            isEmailVerified = true,
            isGoogleUser = true
        )
        whenever(authRepository.signInWithGoogle(idToken1))
            .thenReturn(DataResult.Success(mockUser1))
        whenever(authRepository.signInWithGoogle(idToken2))
            .thenReturn(DataResult.Success(mockUser2))

        // When
        signInWithGoogleUseCase(idToken1)
        signInWithGoogleUseCase(idToken2)

        // Then
        verify(authRepository).signInWithGoogle(eq(idToken1))
        verify(authRepository).signInWithGoogle(eq(idToken2))
        verify(authRepository, times(2)).signInWithGoogle(any())
    }

    // ==================== AUTHENTICATION ERRORS ====================

    @Test
    fun `invoke with user collision error should return UserCollision error`() = runTest {
        // Given
        val idToken = "anyIdToken"
        whenever(authRepository.signInWithGoogle(idToken))
            .thenReturn(DataResult.Error(FirebaseError.UserCollision))

        // When
        val result = signInWithGoogleUseCase(idToken)

        // Then
        assertTrue(result is DataResult.Error)
        val error = result.dataError
        assertTrue(error is FirebaseError.UserCollision)
        verify(authRepository).signInWithGoogle(idToken)
    }

    @Test
    fun `invoke with too many requests error should return TooManyRequests error`() = runTest {
        // Given
        val idToken = "anyIdToken"
        whenever(authRepository.signInWithGoogle(idToken))
            .thenReturn(DataResult.Error(FirebaseError.TooManyRequests))

        // When
        val result = signInWithGoogleUseCase(idToken)

        // Then
        assertTrue(result is DataResult.Error)
        val error = result.dataError
        assertTrue(error is FirebaseError.TooManyRequests)
        verify(authRepository).signInWithGoogle(idToken)
    }

    // ==================== VALIDATION ERRORS ====================

    @Test
    fun `invoke with empty idToken should return EmptyIdToken error`() = runTest {
        // Given
        val idToken = ""
        whenever(authRepository.signInWithGoogle(idToken))
            .thenReturn(DataResult.Error(GoogleSignInError.EmptyIdToken))

        // When
        val result = signInWithGoogleUseCase(idToken)

        // Then
        assertTrue(result is DataResult.Error)
        val error = result.dataError
        assertTrue(error is GoogleSignInError.EmptyIdToken)
        verify(authRepository).signInWithGoogle(idToken)
    }

    @Test
    fun `invoke with invalid idToken should return InvalidCredentials error`() = runTest {
        // Given
        val idToken = "invalidGoogleIdToken"
        whenever(authRepository.signInWithGoogle(idToken))
            .thenReturn(DataResult.Error(FirebaseError.InvalidCredentials))

        // When
        val result = signInWithGoogleUseCase(idToken)

        // Then
        assertTrue(result is DataResult.Error)
        val error = result.dataError
        assertTrue(error is FirebaseError.InvalidCredentials)
        verify(authRepository).signInWithGoogle(idToken)
    }

    // ==================== NETWORK ERRORS ====================

    @Test
    fun `invoke with network error should return NetworkError`() = runTest {
        // Given
        val idToken = "anyIdToken"
        whenever(authRepository.signInWithGoogle(idToken))
            .thenReturn(DataResult.Error(FirebaseError.NetworkError))

        // When
        val result = signInWithGoogleUseCase(idToken)

        // Then
        assertTrue(result is DataResult.Error)
        val error = result.dataError
        assertTrue(error is FirebaseError.NetworkError)
        verify(authRepository).signInWithGoogle(idToken)
    }

    // ==================== UNKNOWN ERRORS ====================

    @Test
    fun `invoke with google sign in unknown error should return UnknownError`() = runTest {
        // Given
        val idToken = "anyIdToken"
        whenever(authRepository.signInWithGoogle(idToken))
            .thenReturn(DataResult.Error(GoogleSignInError.UnknownError))

        // When
        val result = signInWithGoogleUseCase(idToken)

        // Then
        assertTrue(result is DataResult.Error)
        val error = result.dataError
        assertTrue(error is GoogleSignInError.UnknownError)
        verify(authRepository).signInWithGoogle(idToken)
    }

    // ==================== LOADING STATE ====================

    @Test
    fun `invoke should return Loading when repository returns Loading`() = runTest {
        // Given
        val idToken = "token"
        whenever(authRepository.signInWithGoogle(idToken))
            .thenReturn(DataResult.Loading)

        // When
        val result = signInWithGoogleUseCase(idToken)

        // Then
        assertTrue(result is DataResult.Loading)
        verify(authRepository).signInWithGoogle(idToken)
    }
}