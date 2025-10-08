package com.fmdev.civilacademy.domain.usecase.login

import com.fmdev.civilacademy.domain.model.User
import com.fmdev.civilacademy.domain.repository.AuthRepository
import com.fmdev.civilacademy.shared.model.DataError
import com.fmdev.civilacademy.shared.model.DataResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignInWithGoogleUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): DataResult<User, DataError> =
        repository.signInWithGoogle(idToken)
}