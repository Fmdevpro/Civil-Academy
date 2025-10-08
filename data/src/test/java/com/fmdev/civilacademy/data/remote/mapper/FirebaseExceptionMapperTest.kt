package com.fmdev.civilacademy.data.remote.mapper

import com.fmdev.civilacademy.domain.errors.FirebaseError
import com.fmdev.civilacademy.shared.model.DataError
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock

class FirebaseExceptionMapperTest {

    private val defaultError = object : DataError {}

    @Test
    fun `mapFirebaseException should return InvalidUser when exception is FirebaseAuthInvalidUserException`() {
        // Given
        val exception = mock(FirebaseAuthInvalidUserException::class.java)

        // When
        val result = mapFirebaseException(exception, defaultError)

        // Then
        assertEquals(FirebaseError.InvalidUser, result)
    }

    @Test
    fun `mapFirebaseException should return InvalidCredentials when exception is FirebaseAuthInvalidCredentialsException`() {
        // Given
        val exception = mock(FirebaseAuthInvalidCredentialsException::class.java)

        // When
        val result = mapFirebaseException(exception, defaultError)

        // Then
        assertEquals(FirebaseError.InvalidCredentials, result)
    }

    @Test
    fun `mapFirebaseException should return UserCollision when exception is FirebaseAuthUserCollisionException`() {
        // Given
        val exception = mock(FirebaseAuthUserCollisionException::class.java)

        // When
        val result = mapFirebaseException(exception, defaultError)

        // Then
        assertEquals(FirebaseError.UserCollision, result)
    }

    @Test
    fun `mapFirebaseException should return NetworkError when exception is FirebaseNetworkException`() {
        // Given
        val exception = mock(FirebaseNetworkException::class.java)

        // When
        val result = mapFirebaseException(exception, defaultError)

        // Then
        assertEquals(FirebaseError.NetworkError, result)
    }

    @Test
    fun `mapFirebaseException should return default error for unhandled FirebaseAuthException`() {
        // Given
        val exception = mock(FirebaseAuthException::class.java)

        // When
        val result = mapFirebaseException(exception, defaultError)

        // Then
        assertEquals(defaultError, result)
    }

    @Test
    fun `mapFirebaseException should return default error for unhandled exception`() {
        // Given
        val exception = IllegalArgumentException("Unexpected error")

        // When
        val result = mapFirebaseException(exception, defaultError)

        // Then
        assertEquals(defaultError, result)
    }

    @Test
    fun `mapFirebaseException should return default error when exception is null`() {
        // Given
        val exception = null

        // When
        val result = mapFirebaseException(exception, defaultError)

        // Then
        assertEquals(defaultError, result)
    }
}
