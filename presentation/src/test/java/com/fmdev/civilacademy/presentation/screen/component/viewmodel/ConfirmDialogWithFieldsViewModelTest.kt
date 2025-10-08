package com.fmdev.civilacademy.presentation.screen.component.viewmodel

import app.cash.turbine.test
import com.fmdev.civilacademy.domain.usecase.validation.ValidateEmailUseCase
import com.fmdev.civilacademy.domain.usecase.validation.ValidatePasswordUseCase
import com.fmdev.civilacademy.domain.usecase.validation.ValidationError
import com.fmdev.civilacademy.domain.usecase.validation.ValidationResult
import com.fmdev.civilacademy.presentation.screen.login.validation.FieldValidationType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class ConfirmDialogWithFieldsViewModelTest {

    private lateinit var viewModel: ConfirmDialogWithFieldsViewModel
    private lateinit var validateEmailUseCase: ValidateEmailUseCase
    private lateinit var validatePasswordUseCase: ValidatePasswordUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        validateEmailUseCase = mock()
        validatePasswordUseCase = mock()
        viewModel = ConfirmDialogWithFieldsViewModel(
            validateEmailUseCase = validateEmailUseCase,
            validatePasswordUseCase = validatePasswordUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have empty values`() = runTest {
        // When
        val state = viewModel.uiState.value

        // Then
        assertEquals("", state.firstFieldValue)
        assertEquals("", state.secondFieldValue)
        assertFalse(state.isFirstFieldFocused)
        assertFalse(state.isSecondFieldFocused)
        assertEquals(ValidationResult.Empty, state.firstFieldValidationError)
        assertEquals(ValidationResult.Empty, state.secondFieldValidationError)
        assertFalse(state.areFieldsValid)
        assertFalse(state.isPasswordVisible)
    }

    @Test
    fun `first field changed with email type should validate and update state`() = runTest {
        // Given
        val email = "test@example.com"
        whenever(validateEmailUseCase(email)).thenReturn(ValidationResult.Valid)

        // When
        viewModel.onEvent(
            ConfirmDialogWithFieldsUiEvent.FirstFieldChanged(
                newValue = email,
                firstFieldType = FieldValidationType.EMAIL
            )
        )
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(email, state.firstFieldValue)
        assertEquals(ValidationResult.Valid, state.firstFieldValidationError)
    }

    @Test
    fun `first field changed with invalid email should show error`() = runTest {
        // Given
        val invalidEmail = "invalid-email"
        whenever(validateEmailUseCase(invalidEmail))
            .thenReturn(ValidationResult.Invalid(ValidationError.INVALID_EMAIL_FORMAT))

        // When
        viewModel.onEvent(
            ConfirmDialogWithFieldsUiEvent.FirstFieldChanged(
                newValue = invalidEmail,
                firstFieldType = FieldValidationType.EMAIL
            )
        )
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(invalidEmail, state.firstFieldValue)
        assertTrue(state.firstFieldValidationError is ValidationResult.Invalid)
        assertEquals(
            ValidationError.INVALID_EMAIL_FORMAT,
            (state.firstFieldValidationError as ValidationResult.Invalid).error
        )
    }

    @Test
    fun `second field changed with password type should validate and update state`() = runTest {
        // Given
        val password = "Password123!"
        whenever(validatePasswordUseCase(password)).thenReturn(ValidationResult.Valid)

        // When
        viewModel.onEvent(
            ConfirmDialogWithFieldsUiEvent.SecondFieldChanged(
                newValue = password,
                secondFieldType = FieldValidationType.PASSWORD
            )
        )
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(password, state.secondFieldValue)
        assertEquals(ValidationResult.Valid, state.secondFieldValidationError)
    }

    @Test
    fun `text field with empty value should show error`() = runTest {
        // Given
        val emptyText = ""

        // When
        viewModel.onEvent(
            ConfirmDialogWithFieldsUiEvent.FirstFieldChanged(
                newValue = emptyText,
                firstFieldType = FieldValidationType.TEXT
            )
        )
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state.firstFieldValidationError is ValidationResult.Invalid)
        assertEquals(
            ValidationError.EMPTY_FIELD,
            (state.firstFieldValidationError as ValidationResult.Invalid).error
        )
    }

    @Test
    fun `text field with non-empty value should be valid`() = runTest {
        // Given
        val someText = "Some text"

        // When
        viewModel.onEvent(
            ConfirmDialogWithFieldsUiEvent.FirstFieldChanged(
                newValue = someText,
                firstFieldType = FieldValidationType.TEXT
            )
        )
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(ValidationResult.Valid, state.firstFieldValidationError)
    }

    @Test
    fun `first field focus changed should update state`() = runTest {
        // Given

        // When - Focus ON
        viewModel.onEvent(ConfirmDialogWithFieldsUiEvent.FirstFieldFocusChanged(true))
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.isFirstFieldFocused)

        // When - Focus OFF
        viewModel.onEvent(ConfirmDialogWithFieldsUiEvent.FirstFieldFocusChanged(false))
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.isFirstFieldFocused)
    }

    @Test
    fun `second field focus changed should update state`() = runTest {
        // Given

        // When
        viewModel.onEvent(ConfirmDialogWithFieldsUiEvent.SecondFieldFocusChanged(true))
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.isSecondFieldFocused)
    }

    @Test
    fun `password visibility toggle should switch state`() = runTest {
        // Given
        assertFalse(viewModel.uiState.value.isPasswordVisible)

        // When - Toggle ON
        viewModel.onEvent(ConfirmDialogWithFieldsUiEvent.PasswordVisibilityToggle)
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.isPasswordVisible)

        // When - Toggle OFF
        viewModel.onEvent(ConfirmDialogWithFieldsUiEvent.PasswordVisibilityToggle)
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.isPasswordVisible)
    }

    @Test
    fun `confirm button with matching valid fields should send confirm side effect`() = runTest {
        // Given
        val email = "test@example.com"
        whenever(validateEmailUseCase(email)).thenReturn(ValidationResult.Valid)

        viewModel.onEvent(
            ConfirmDialogWithFieldsUiEvent.FirstFieldChanged(email, FieldValidationType.EMAIL)
        )
        viewModel.onEvent(
            ConfirmDialogWithFieldsUiEvent.SecondFieldChanged(email, FieldValidationType.EMAIL)
        )
        advanceUntilIdle()

        // When
        viewModel.sideEffect.test {
            viewModel.onEvent(
                ConfirmDialogWithFieldsUiEvent.ConfirmButtonClicked(
                    FieldValidationType.EMAIL,
                    FieldValidationType.EMAIL
                )
            )
            advanceUntilIdle()

            // Then
            val sideEffect = awaitItem()
            assertTrue(sideEffect is ConfirmDialogWithFieldsSideEffect.Confirm)
            assertEquals(email, (sideEffect as ConfirmDialogWithFieldsSideEffect.Confirm).firstFieldValue)
            assertTrue(viewModel.uiState.value.areFieldsValid)
        }
    }

    @Test
    fun `confirm button with non-matching fields should not send confirm side effect`() = runTest {
        // Given
        val email1 = "test1@example.com"
        val email2 = "test2@example.com"
        whenever(validateEmailUseCase(any())).thenReturn(ValidationResult.Valid)

        viewModel.onEvent(
            ConfirmDialogWithFieldsUiEvent.FirstFieldChanged(email1, FieldValidationType.EMAIL)
        )
        viewModel.onEvent(
            ConfirmDialogWithFieldsUiEvent.SecondFieldChanged(email2, FieldValidationType.EMAIL)
        )
        advanceUntilIdle()

        // When
        viewModel.sideEffect.test {
            viewModel.onEvent(
                ConfirmDialogWithFieldsUiEvent.ConfirmButtonClicked(
                    FieldValidationType.EMAIL,
                    FieldValidationType.EMAIL
                )
            )
            advanceUntilIdle()

            // Then
            expectNoEvents()
            assertFalse(viewModel.uiState.value.areFieldsValid)
        }
    }

    @Test
    fun `confirm button with invalid first field should not send confirm side effect`() = runTest {
        // Given
        val invalidEmail = "invalid"
        val validEmail = "valid@example.com"
        whenever(validateEmailUseCase(invalidEmail))
            .thenReturn(ValidationResult.Invalid(ValidationError.INVALID_EMAIL_FORMAT))
        whenever(validateEmailUseCase(validEmail)).thenReturn(ValidationResult.Valid)

        viewModel.onEvent(
            ConfirmDialogWithFieldsUiEvent.FirstFieldChanged(invalidEmail, FieldValidationType.EMAIL)
        )
        viewModel.onEvent(
            ConfirmDialogWithFieldsUiEvent.SecondFieldChanged(validEmail, FieldValidationType.EMAIL)
        )
        advanceUntilIdle()

        // When
        viewModel.sideEffect.test {
            viewModel.onEvent(
                ConfirmDialogWithFieldsUiEvent.ConfirmButtonClicked(
                    FieldValidationType.EMAIL,
                    FieldValidationType.EMAIL
                )
            )
            advanceUntilIdle()

            // Then
            expectNoEvents()
            assertFalse(viewModel.uiState.value.areFieldsValid)
        }
    }

    @Test
    fun `close dialog should clean state and send close side effect`() = runTest {
        // Given
        val email = "test@example.com"
        whenever(validateEmailUseCase(email)).thenReturn(ValidationResult.Valid)

        viewModel.onEvent(
            ConfirmDialogWithFieldsUiEvent.FirstFieldChanged(email, FieldValidationType.EMAIL)
        )
        viewModel.onEvent(ConfirmDialogWithFieldsUiEvent.FirstFieldFocusChanged(true))
        viewModel.onEvent(ConfirmDialogWithFieldsUiEvent.PasswordVisibilityToggle)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isFirstFieldFocused)

        // When
        viewModel.sideEffect.test {
            viewModel.onEvent(ConfirmDialogWithFieldsUiEvent.CloseDialog)
            advanceUntilIdle()

            // Then
            val sideEffect = awaitItem()
            assertTrue(sideEffect is ConfirmDialogWithFieldsSideEffect.Close)

            val state = viewModel.uiState.value
            assertEquals("", state.firstFieldValue)
            assertEquals("", state.secondFieldValue)
            assertFalse(state.isFirstFieldFocused)
            assertFalse(state.isSecondFieldFocused)
            assertFalse(state.isPasswordVisible)
            assertEquals(ValidationResult.Empty, state.firstFieldValidationError)
            assertEquals(ValidationResult.Empty, state.secondFieldValidationError)
        }
    }

    @Test
    fun `field validation with NONE type should return Empty`() = runTest {
        // Given
        val value = "some value"

        // When
        viewModel.onEvent(
            ConfirmDialogWithFieldsUiEvent.FirstFieldChanged(
                newValue = value,
                firstFieldType = FieldValidationType.NONE
            )
        )
        advanceUntilIdle()

        // Then
        assertEquals(ValidationResult.Empty, viewModel.uiState.value.firstFieldValidationError)
    }
}