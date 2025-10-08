package com.fmdev.civilacademy.domain.usecase.login

import com.fmdev.civilacademy.domain.repository.AuthRepository
import com.fmdev.civilacademy.shared.model.DataError
import com.fmdev.civilacademy.shared.model.DataResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResetPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): DataResult<Any, DataError> =
        authRepository.sendPasswordResetEmail(email)
}