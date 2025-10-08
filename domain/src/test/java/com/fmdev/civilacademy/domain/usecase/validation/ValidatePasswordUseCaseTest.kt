package com.fmdev.civilacademy.domain.usecase.validation

import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class ValidatePasswordUseCaseTest {

    private lateinit var useCase: ValidatePasswordUseCase

    @Before
    fun setup() {
        useCase = ValidatePasswordUseCase()
    }

    @Test
    fun `should return Invalid when password is blank`() {
        // When
        val result = useCase("")
        // Then
        assertEquals(ValidationResult.Invalid(ValidationError.EMPTY_FIELD), result)
    }

    @Test
    fun `should return Invalid when password is too short`() {
        // When
        val result = useCase("123")
        // Then
        assertEquals(ValidationResult.Invalid(ValidationError.PASSWORD_TOO_SHORT), result)
    }

    @Test
    fun `should return Invalid when password is too long`() {
        // Given
        val longPassword = "a".repeat(129)
        // When
        val result = useCase(longPassword)
        // Then
        assertEquals(ValidationResult.Invalid(ValidationError.PASSWORD_TOO_LONG), result)
    }

    @Test
    fun `should return Valid when password is within valid range`() {
        // When
        val result = useCase("123456")
        // Then
        assertEquals(ValidationResult.Valid, result)
    }
}
