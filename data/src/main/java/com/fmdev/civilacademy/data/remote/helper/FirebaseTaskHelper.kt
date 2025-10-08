package com.fmdev.civilacademy.data.remote.helper

import com.fmdev.civilacademy.shared.model.DataError
import com.fmdev.civilacademy.shared.model.DataResult
import com.google.android.gms.tasks.Task
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun <T : Any, R : Any> suspendFirebaseCall(
    taskProvider: () -> Task<T>,
    onSuccess: (T) -> R,
    onError: (Exception?) -> DataError
): DataResult<R, DataError> = suspendCoroutine { continuation ->
    taskProvider().addOnCompleteListener { task ->
        val exception = task.exception
        if (task.isSuccessful) {
            try {
                continuation.resume(DataResult.Success(onSuccess(task.result!!)))
            } catch (e: Exception) {
                continuation.resume(DataResult.Error(onError(e)))
            }
        } else {
            continuation.resume(DataResult.Error(onError(exception)))
        }
    }
}