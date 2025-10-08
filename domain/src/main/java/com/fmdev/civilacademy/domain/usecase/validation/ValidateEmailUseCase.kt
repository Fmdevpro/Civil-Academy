package com.fmdev.civilacademy.domain.usecase.validation

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValidateEmailUseCase @Inject constructor() {

    operator fun invoke(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult.Invalid(ValidationError.EMPTY_FIELD)
            email.length > MAX_EMAIL_LENGTH -> ValidationResult.Invalid(ValidationError.EMAIL_TOO_LONG)
            !isEmailFormatValid(email) -> ValidationResult.Invalid(ValidationError.INVALID_EMAIL_FORMAT)
            else -> ValidationResult.Valid
        }
    }

    private fun isEmailFormatValid(email: String): Boolean {
        // W3C HTML5 email validation regex
        val emailRegex = ("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@" +
                "[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?" +
                "(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$").toRegex()
        return emailRegex.matches(email)
    }

    companion object {
        private const val MAX_EMAIL_LENGTH = 320
    }
}