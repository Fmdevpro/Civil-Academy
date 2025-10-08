package com.fmdev.civilacademy.domain.usecase.validation

enum class ValidationError {
    EMPTY_FIELD,
    EMAIL_TOO_LONG,
    INVALID_EMAIL_FORMAT,
    PASSWORD_TOO_SHORT,
    PASSWORD_TOO_LONG,
    FIELDS_DO_NOT_MATCH,
    WRONG_EMAIL,
    WRONG_PASSWORD
}