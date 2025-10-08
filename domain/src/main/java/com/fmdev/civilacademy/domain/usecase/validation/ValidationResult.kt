package com.fmdev.civilacademy.domain.usecase.validation

sealed interface ValidationResult {
    data object Empty : ValidationResult
    data object Valid : ValidationResult
    data class Invalid(val error: ValidationError) : ValidationResult
}