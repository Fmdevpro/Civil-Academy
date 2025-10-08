package com.fmdev.civilacademy.domain.usecase.session

import com.fmdev.civilacademy.domain.model.User
import com.fmdev.civilacademy.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<User?> {
        return authRepository.getCurrentUser()
    }
}
