package com.fmdev.civilacademy.data.local.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.fmdev.civilacademy.domain.model.User
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object UserSerializer : Serializer<User?> {
    
    override val defaultValue: User? = null

    override suspend fun readFrom(input: InputStream): User? {
        return try {
            val bytes = input.readBytes()
            if (bytes.isEmpty()) {
                defaultValue
            } else {
                Json.decodeFromString<User>(bytes.decodeToString())
            }
        } catch (e: SerializationException) {
            throw CorruptionException("Cannot read user data", e)
        }
    }

    override suspend fun writeTo(t: User?, output: OutputStream) {
        if (t == null) {
            output.write(byteArrayOf())
        } else {
            output.write(
                Json.encodeToString(t).encodeToByteArray()
            )
        }
    }
}
