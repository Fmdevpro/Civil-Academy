package com.fmdev.civilacademy.domain.usecase.validation


import org.junit.Before
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AreFieldsValidUseCaseTest {

    private lateinit var useCase: AreFieldsValidUseCase

    @Before
    fun setup() {
        useCase = AreFieldsValidUseCase()
    }

    @Test
    fun `should return true when both validations are valid`() {
        // Given
        val emailValidation = ValidationResult.Valid
        val passwordValidation = ValidationResult.Valid

        // When
        val result = useCase(emailValidation, passwordValidation)

        // Then
        assertTrue(result)
    }

    @Test
    fun `should return false when email validation is Empty`() {
        // Given
        val emailValidation = ValidationResult.Empty
        val passwordValidation = ValidationResult.Valid

        // When
        val result = useCase(emailValidation, passwordValidation)

        // Then
        assertFalse(result)
    }

    @Test
    fun `should return false when password validation is Empty`() {
        // Given
        val emailValidation = ValidationResult.Valid
        val passwordValidation = ValidationResult.Empty

        // When
        val result = useCase(emailValidation, passwordValidation)

        // Then
        assertFalse(result)
    }

    @Test
    fun `should return false when both validations are Empty`() {
        // Given
        val emailValidation = ValidationResult.Empty
        val passwordValidation = ValidationResult.Empty

        // When
        val result = useCase(emailValidation, passwordValidation)

        // Then
        assertFalse(result)
    }

    @Test
    fun `should return false when email is invalid`() {
        // Given
        val emailValidation = ValidationResult.Invalid(ValidationError.INVALID_EMAIL_FORMAT)
        val passwordValidation = ValidationResult.Valid

        // When
        val result = useCase(emailValidation, passwordValidation)

        // Then
        assertFalse(result)
    }

    @Test
    fun `should return false when password is invalid`() {
        // Given
        val emailValidation = ValidationResult.Valid
        val passwordValidation = ValidationResult.Invalid(ValidationError.PASSWORD_TOO_SHORT)

        // When
        val result = useCase(emailValidation, passwordValidation)

        // Then
        assertFalse(result)
    }

    @Test
    fun `should return false when both validations are invalid`() {
        // Given
        val emailValidation = ValidationResult.Invalid(ValidationError.EMPTY_FIELD)
        val passwordValidation = ValidationResult.Invalid(ValidationError.PASSWORD_TOO_SHORT)

        // When
        val result = useCase(
            emailValidation,
            passwordValidation
        )

        // Then
        assertFalse(result)
    }
}