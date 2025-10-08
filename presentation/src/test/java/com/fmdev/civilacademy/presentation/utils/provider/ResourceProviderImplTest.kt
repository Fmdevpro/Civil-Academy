package com.fmdev.civilacademy.presentation.utils.provider

import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

class ResourceProviderImplTest {

    private lateinit var context: Context
    private lateinit var resourceProvider: ResourceProviderImpl

    @Before
    fun setup() {
        context = mock()
        resourceProvider = ResourceProviderImpl(context)
    }

    @Test
    fun `getString should return string from context`() {
        // Given
        val resId = 1
        val expected = "Hello World"
        whenever(context.getString(resId)).thenReturn(expected)

        // When
        val result = resourceProvider.getString(resId)
        // Then
        assertEquals(expected, result)
    }

    @Test
    fun `getString with formatArgs should return formatted string`() {
        // Given
        val resId = 2
        val expected = "Hello, John!"
        whenever(context.getString(resId, "John")).thenReturn(expected)

        // When
        val result = resourceProvider.getString(resId, "John")
        // Then
        assertEquals(expected, result)
    }
}
