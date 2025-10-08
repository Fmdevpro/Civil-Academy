package com.fmdev.civilacademy.androidshared.provider

import android.content.Intent

interface GoogleSignInResultHandler {
    fun getIdToken(intent: Intent?): String?
}