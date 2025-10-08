package com.fmdev.civilacademy.androidshared.provider

import android.content.Intent

interface GoogleSignInClientProvider {
    fun getSignInIntent(): Intent
}