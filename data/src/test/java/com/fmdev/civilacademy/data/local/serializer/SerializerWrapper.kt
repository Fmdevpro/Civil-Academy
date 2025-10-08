package com.fmdev.civilacademy.data.local.serializer

import androidx.datastore.core.CorruptionException
import java.io.InputStream

open class SerializerWrapper<T> {
    open fun decode(json: String): T? = null
    fun readFrom(input: InputStream): T? {
        return try {
            val bytes = input.readBytes()
            if (bytes.isEmpty()) null else decode(bytes.decodeToString())
        } catch (e: IllegalStateException) {
            throw CorruptionException("Cannot read user data", e)
        }
    }
}