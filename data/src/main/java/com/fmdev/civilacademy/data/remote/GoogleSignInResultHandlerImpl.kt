package com.fmdev.civilacademy.data.remote

import android.content.Intent
import com.fmdev.civilacademy.androidshared.provider.GoogleSignInResultHandler
import com.google.android.gms.auth.api.signin.GoogleSignIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleSignInResultHandlerImpl @Inject constructor() : GoogleSignInResultHandler {
    override fun getIdToken(intent: Intent?): String? {
        val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
        return task.result?.idToken
    }
}