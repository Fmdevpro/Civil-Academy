package com.fmdev.civilacademy.data.repository

import com.fmdev.civilacademy.data.local.datasource.SessionLocalDataSource
import com.fmdev.civilacademy.data.remote.datasource.AuthRemoteDataSource
import com.fmdev.civilacademy.domain.errors.GoogleSignInError
import com.fmdev.civilacademy.domain.errors.LoginError
import com.fmdev.civilacademy.domain.model.User
import com.fmdev.civilacademy.shared.model.DataResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class AuthRepositoryFirebaseImplTest {

    private val authRemoteDataSource: AuthRemoteDataSource = mock()
    private val sessionLocalDataSource: SessionLocalDataSource = mock()
    private lateinit var authRepository: AuthRepositoryFirebaseImpl

    @Before
    fun setup() {
        authRepository = AuthRepositoryFirebaseImpl(authRemoteDataSource, sessionLocalDataSource)
    }

    @Test
    fun `login should save user and return success when email is verified`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val user = User(
            uid = "123",
            displayName = "Test User",
            email = email,
            isEmailVerified = true,
            isGoogleUser = false
        )
        val expectedResult = DataResult.Success(user)
        whenever(authRemoteDataSource.login(email, password)).thenReturn(expectedResult)

        // When
        val result = authRepository.login(email, password)

        // Then
        assertEquals(expectedResult, result)
        verify(authRemoteDataSource).login(email, password)
        verify(sessionLocalDataSource).saveUser(user)
    }

    @Test
    fun `login should not save user and return error when email is not verified`() = runTest {
        // Given
        val email = "unverified@example.com"
        val password = "password123"
        val user = User(
            uid = "456",
            displayName = "Unverified User",
            email = email,
            isEmailVerified = false,
            isGoogleUser = false
        )
        val remoteResult = DataResult.Success(user)
        whenever(authRemoteDataSource.login(email, password)).thenReturn(remoteResult)

        // When
        val result = authRepository.login(email, password)

        // Then
        verify(authRemoteDataSource).login(email, password)
        verify(sessionLocalDataSource, never()).saveUser(any())
        assertTrue(result is DataResult.Error && result.dataError is LoginError.EmailNotVerified)
    }

    @Test
    fun `login should return Loading when remote returns Loading`() = runTest {
        // Given
        val email = "any@example.com"
        val password = "password"
        val loadingResult = DataResult.Loading
        whenever(authRemoteDataSource.login(email, password)).thenReturn(loadingResult)

        // When
        val result = authRepository.login(email, password)

        // Then
        assertEquals(loadingResult, result)
        verify(authRemoteDataSource).login(email, password)
        verify(sessionLocalDataSource, never()).saveUser(any())
    }

    @Test
    fun `signInWithGoogle should save user when success`() = runTest {
        // Given
        val idToken = "fake_id_token"
        val user = User(
            uid = "123",
            displayName = "Google User",
            email = "google@example.com",
            isEmailVerified = true,
            isGoogleUser = true
        )
        val expectedResult = DataResult.Success(user)
        whenever(authRemoteDataSource.signInWithGoogle(idToken)).thenReturn(expectedResult)

        // When
        val result = authRepository.signInWithGoogle(idToken)

        // Then
        assertEquals(expectedResult, result)
        verify(authRemoteDataSource).signInWithGoogle(idToken)
        verify(sessionLocalDataSource).saveUser(user)
    }

    @Test
    fun `signInWithGoogle should not save user and return error when failure`() = runTest {
        // Given
        val idToken = "fake_id_token"
        val expectedError = GoogleSignInError.UnknownError
        val expectedResult = DataResult.Error(expectedError)
        whenever(authRemoteDataSource.signInWithGoogle(idToken)).thenReturn(expectedResult)

        // When
        val result = authRepository.signInWithGoogle(idToken)

        // Then
        assertEquals(expectedResult, result)
        verify(authRemoteDataSource).signInWithGoogle(idToken)
        verify(sessionLocalDataSource, never()).saveUser(any())
        assertTrue(result is DataResult.Error && result.dataError == expectedError)
    }

    @Test
    fun `signInWithGoogle should return Loading when remote returns Loading`() = runTest {
        // Given
        val idToken = "any_token"
        val loadingResult = DataResult.Loading
        whenever(authRemoteDataSource.signInWithGoogle(idToken)).thenReturn(loadingResult)

        // When
        val result = authRepository.signInWithGoogle(idToken)

        // Then
        assertEquals(loadingResult, result)
        verify(authRemoteDataSource).signInWithGoogle(idToken)
        verify(sessionLocalDataSource, never()).saveUser(any())
    }

    @Test
    fun `sendPasswordResetEmail should delegate to authRemoteDataSource`() = runTest {
        // Given
        val email = "reset@example.com"
        val expectedResult = DataResult.Success(Any())
        whenever(authRemoteDataSource.sendPasswordResetEmail(email)).thenReturn(expectedResult)

        // When
        val result = authRepository.sendPasswordResetEmail(email)

        // Then
        assertEquals(expectedResult, result)
        verify(authRemoteDataSource).sendPasswordResetEmail(email)
    }

    @Test
    fun `sendUserEmailVerification should delegate to authRemoteDataSource`() = runTest {
        // Given
        val expectedResult = DataResult.Success(Any())
        whenever(authRemoteDataSource.sendUserEmailVerification()).thenReturn(expectedResult)

        // When
        val result = authRepository.sendUserEmailVerification()

        // Then
        assertEquals(expectedResult, result)
        verify(authRemoteDataSource).sendUserEmailVerification()
    }

    @Test
    fun `isEmailVerified should delegate to authRemoteDataSource`() = runTest {
        // Given
        val expectedResult = DataResult.Success(true)
        whenever(authRemoteDataSource.isEmailVerified()).thenReturn(expectedResult)

        // When
        val result = authRepository.isEmailVerified()

        // Then
        assertEquals(expectedResult, result)
        verify(authRemoteDataSource).isEmailVerified()
    }

    @Test
    fun `signOut should clear session and call remote signOut`() = runTest {
        // When
        authRepository.signOut()

        // Then
        verify(authRemoteDataSource).signOut()
        verify(sessionLocalDataSource).clearUser()
    }

    @Test
    fun `getCurrentUser should return flow from sessionLocalDataSource`() = runTest {
        // Given
        val userFlow = mock<kotlinx.coroutines.flow.Flow<User?>>()
        whenever(sessionLocalDataSource.getUser()).thenReturn(userFlow)

        // When
        val result = authRepository.getCurrentUser()

        // Then
        assertEquals(userFlow, result)
    }

    @Test
    fun `isUserLoggedIn should return flow from sessionLocalDataSource`() = runTest {
        // Given
        val loggedInFlow = mock<kotlinx.coroutines.flow.Flow<Boolean>>()
        whenever(sessionLocalDataSource.isUserLoggedIn()).thenReturn(loggedInFlow)

        // When
        val result = authRepository.isUserLoggedIn()

        // Then
        assertEquals(loggedInFlow, result)
    }
}
