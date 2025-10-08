package com.fmdev.civilacademy.domain.usecase.session

import com.fmdev.civilacademy.domain.model.User
import com.fmdev.civilacademy.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class GetUserSessionUseCaseTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var getUserSessionUseCase: GetUserSessionUseCase

    @Before
    fun setup() {
        authRepository = mock()
        getUserSessionUseCase = GetUserSessionUseCase(authRepository)
    }

    @Test
    fun `invoke should return user flow from sessionRepository`() = runTest {
        // Given
        val user = User("123", "User", "test@test.com",
            isEmailVerified = true,
            isGoogleUser = false
        )
        val expectedFlow = flowOf(user)

        whenever(authRepository.getCurrentUser()).thenReturn(expectedFlow)

        // When
        val result = getUserSessionUseCase().first()

        // Then
        assertEquals(user, result)
        verify(authRepository).getCurrentUser()
    }
}
