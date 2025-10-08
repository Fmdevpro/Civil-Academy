package com.fmdev.civilacademy.data.remote

import android.content.Context
import android.content.Intent
import com.fmdev.civilacademy.androidshared.provider.GoogleSignInClientProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleSignInClientProviderImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val webClientId: String
) : GoogleSignInClientProvider {

    private val client: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    override fun getSignInIntent(): Intent = client.signInIntent
}