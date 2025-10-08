package com.fmdev.civilacademy.data.remote.helper

import com.fmdev.civilacademy.domain.errors.FirebaseError
import com.fmdev.civilacademy.shared.model.DataError
import com.fmdev.civilacademy.shared.model.DataResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class SuspendFirebaseCallTest {

    @Test
    fun `returns Success when task is successful and onSuccess succeeds`() = runTest {
        // Given
        val mockTask = mock<Task<String>> {
            on { isSuccessful } doReturn true
            on { result } doReturn "firebase_result"
        }

        val taskProvider = { mockTask }
        val onSuccess: (String) -> Int = { it.length }
        val onError: (Exception?) -> DataError = { FirebaseError.UnknownError }

        whenever(mockTask.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<String>>(0)
            listener.onComplete(mockTask)
            mockTask
        }

        // When
        val result = suspendFirebaseCall(
            taskProvider = taskProvider,
            onSuccess = onSuccess,
            onError = onError
        )

        // Then
        assertTrue(result is DataResult.Success)
        assertEquals(15, (result as DataResult.Success).data)    }

    @Test
    fun `returns Error when task is successful but onSuccess throws`() = runTest {
        // Given
        val mockTask = mock<Task<String>> {
            on { isSuccessful } doReturn true
            on { result } doReturn "some_result"
        }

        val exceptionInSuccess = IllegalStateException("Boom")
        val taskProvider = { mockTask }
        val onSuccess: (String) -> Int = { throw exceptionInSuccess }
        val onError: (Exception?) -> DataError = {
            assertEquals(exceptionInSuccess, it)
            FirebaseError.NetworkError
        }

        whenever(mockTask.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<String>>(0)
            listener.onComplete(mockTask)
            mockTask
        }

        // When
        val result = suspendFirebaseCall(
            taskProvider = taskProvider,
            onSuccess = onSuccess,
            onError = onError
        )

        // Then
        assertTrue(result is DataResult.Error)
        val error = (result as DataResult.Error)
        assertEquals(FirebaseError.NetworkError, error.dataError)
    }

    @Test
    fun `returns Error when task is not successful`() = runTest {
        // Given
        val firebaseException = RuntimeException("Firebase failed")
        val mockTask = mock<Task<String>> {
            on { isSuccessful } doReturn false
            on { exception } doReturn firebaseException
        }

        val taskProvider = { mockTask }
        val onSuccess: (String) -> Int = { it.length }
        val onError: (Exception?) -> DataError = {
            assertEquals(firebaseException, it)
            FirebaseError.InvalidUser
        }

        whenever(mockTask.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<String>>(0)
            listener.onComplete(mockTask)
            mockTask
        }

        // When
        val result = suspendFirebaseCall(
            taskProvider = taskProvider,
            onSuccess = onSuccess,
            onError = onError
        )

        // Then
        assertTrue(result is DataResult.Error)
        val error = result as DataResult.Error
        assertEquals(FirebaseError.InvalidUser, error.dataError)
    }
}