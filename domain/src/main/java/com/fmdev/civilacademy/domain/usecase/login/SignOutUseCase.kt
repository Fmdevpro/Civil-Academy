package com.fmdev.civilacademy.domain.usecase.login

import com.fmdev.civilacademy.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() {
        authRepository.signOut()
    }
}