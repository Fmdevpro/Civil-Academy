package com.fmdev.civilacademy.domain.usecase.validation

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AreFieldsValidUseCase @Inject constructor() {

    operator fun invoke(
        emailValidation: ValidationResult,
        passwordValidation: ValidationResult
    ): Boolean {
        return emailValidation is ValidationResult.Valid &&
                passwordValidation is ValidationResult.Valid
    }
}