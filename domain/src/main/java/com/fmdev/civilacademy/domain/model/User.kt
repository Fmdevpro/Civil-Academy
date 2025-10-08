package com.fmdev.civilacademy.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val uid: String,
    val displayName: String,
    val email: String,
    val isEmailVerified: Boolean,
    val isGoogleUser: Boolean
)