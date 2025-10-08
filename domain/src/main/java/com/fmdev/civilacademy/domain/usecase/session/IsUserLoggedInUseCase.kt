package com.fmdev.civilacademy.domain.usecase.session

import com.fmdev.civilacademy.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsUserLoggedInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return authRepository.isUserLoggedIn()
    }
}
