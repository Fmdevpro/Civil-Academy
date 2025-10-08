package com.fmdev.civilacademy.data.local.serializer

import androidx.datastore.core.CorruptionException
import com.fmdev.civilacademy.domain.model.User
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class UserSerializerTest {

    @Test
    fun `writeTo should serialize User to JSON`() = runTest {
        // Given
        val user = User(
            uid = "123",
            displayName = "Fernando",
            email = "fernando@example.com",
            isEmailVerified = true,
            isGoogleUser = false
        )
        val output = ByteArrayOutputStream()

        // When
        UserSerializer.writeTo(user, output)

        // Then
        val json = output.toString()
        assertEquals(Json.encodeToString(user), json)
    }

    @Test
    fun `writeTo should write empty byteArray when user is null`() = runTest {
        // Given
        val output = ByteArrayOutputStream()

        // When
        UserSerializer.writeTo(null, output)

        // Then
        assertEquals(0, output.toByteArray().size)
    }

    @Test
    fun `readFrom should deserialize valid JSON`() = runTest {
        // Given
        val user = User(
            uid = "123",
            displayName = "Fernando",
            email = "fernando@example.com",
            isEmailVerified = true,
            isGoogleUser = false
        )
        val json = Json.encodeToString(user)
        val input = ByteArrayInputStream(json.encodeToByteArray())

        // When
        val result = UserSerializer.readFrom(input)

        // Then
        assertEquals(user, result)
    }

    @Test
    fun `readFrom should return defaultValue for empty input`() = runTest {
        // Given
        val input = ByteArrayInputStream(byteArrayOf())

        // When
        val result = UserSerializer.readFrom(input)

        // Then
        assertNull(result)
    }

    @Test
    fun `readFrom should throw CorruptionException for invalid JSON`() = runTest {
        // Given
        val invalidJson = "{ invalid json }"
        val input = ByteArrayInputStream(invalidJson.encodeToByteArray())

        // When & Then
        val exception = assertThrows(CorruptionException::class.java) {
            runBlocking {
                UserSerializer.readFrom(input)
            }
        }

        assertTrue(exception.message!!.contains("Cannot read user data"))
    }

    @Test
    fun `readFrom should throw CorruptionException when IllegalStateException occurs`() = runTest {
        // Given
        val input = ByteArrayInputStream("{}".encodeToByteArray())
        val serializer = object : SerializerWrapper<User?>() {
            override fun decode(json: String): User? {
                throw IllegalStateException("Simulated IllegalStateException")
            }
        }

        // When & Then
        val exception = assertThrows(CorruptionException::class.java) {
            serializer.readFrom(input)
        }

        assertTrue(exception.message!!.contains("Cannot read user data"))
    }

}