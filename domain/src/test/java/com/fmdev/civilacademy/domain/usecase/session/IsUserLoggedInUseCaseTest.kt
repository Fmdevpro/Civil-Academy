package com.fmdev.civilacademy.domain.usecase.session

import com.fmdev.civilacademy.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertTrue

class IsUserLoggedInUseCaseTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var isUserLoggedInUseCase: IsUserLoggedInUseCase

    @Before
    fun setup() {
        authRepository = mock()
        isUserLoggedInUseCase = IsUserLoggedInUseCase(authRepository)
    }

    @Test
    fun `invoke should return isUserLoggedIn flow from sessionRepository`() = runTest {
        // Given
        val expectedFlow = flowOf(true)

        whenever(authRepository.isUserLoggedIn()).thenReturn(expectedFlow)

        // When
        val result = isUserLoggedInUseCase().first()

        // Then
        assertTrue(result)
        verify(authRepository).isUserLoggedIn()
    }
}
