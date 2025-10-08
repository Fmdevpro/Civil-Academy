package com.fmdev.civilacademy.presentation.screen.main

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @Test
    fun `initial state isSystemUIVisible is false`() = runTest {
        // Given
        val viewModel = MainViewModel()

        // When
        val result = viewModel.uiState.first()

        // Then
        assertEquals(false, result.isSystemUIVisible)
    }

    @Test
    fun `onEvent HideSystemUI sets state to false and emits side effect`() = runTest {
        // Given
        val viewModel = MainViewModel()

        // When
        viewModel.onEvent(MainUiEvent.HideSystemUI)

        // Then
        val result = viewModel.uiState.first()
        assertEquals(false, result.isSystemUIVisible)

        // Then
        viewModel.sideEffect.test {
            assertEquals(MainSideEffect.HideSystemUI, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onEvent ShowSystemUI sets state to true and emits side effect`() = runTest {
        // Given
        val viewModel = MainViewModel()

        // When
        viewModel.onEvent(MainUiEvent.ShowSystemUI)

        // Then
        val result = viewModel.uiState.first()
        assertEquals(true, result.isSystemUIVisible)

        // Then
        viewModel.sideEffect.test {
            assertEquals(MainSideEffect.ShowSystemUI, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}