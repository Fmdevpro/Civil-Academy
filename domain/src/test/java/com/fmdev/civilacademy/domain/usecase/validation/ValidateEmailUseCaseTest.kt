package com.fmdev.civilacademy.domain.usecase.validation

import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class ValidateEmailUseCaseTest {

    private lateinit var useCase: ValidateEmailUseCase

    @Before
    fun setup() {
        useCase = ValidateEmailUseCase()
    }

    @Test
    fun `should return Invalid when email is blank`() {
        // When
        val result = useCase("")
        // Then
        assertEquals(ValidationResult.Invalid(ValidationError.EMPTY_FIELD), result)
    }

    @Test
    fun `should return Invalid when email is too long`() {
        // Given
        val longEmail = "a".repeat(310) + "@example.com"
        // When
        val result = useCase(longEmail)
        // Then
        assertEquals(ValidationResult.Invalid(ValidationError.EMAIL_TOO_LONG), result)
    }

    @Test
    fun `should return Invalid when email format is invalid`() {
        // When
        val result = useCase("invalid-email")
        // Then
        assertEquals(ValidationResult.Invalid(ValidationError.INVALID_EMAIL_FORMAT), result)
    }

    @Test
    fun `should return Valid when email is correct`() {
        // When
        val result = useCase("test@example.com")
        // Then
        assertEquals(ValidationResult.Valid, result)
    }
}
