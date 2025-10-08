package com.fmdev.civilacademy.domain.usecase.validation

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValidatePasswordUseCase @Inject constructor() {

    operator fun invoke(password: String): ValidationResult {
        return when {
            password.isBlank() -> ValidationResult.Invalid(ValidationError.EMPTY_FIELD)
            password.length < MIN_PASSWORD_LENGTH -> ValidationResult.Invalid(ValidationError.PASSWORD_TOO_SHORT)
            password.length > MAX_PASSWORD_LENGTH -> ValidationResult.Invalid(ValidationError.PASSWORD_TOO_LONG)
            else -> ValidationResult.Valid
        }
    }

    companion object {
        private const val MIN_PASSWORD_LENGTH = 6
        private const val MAX_PASSWORD_LENGTH = 128
    }
}