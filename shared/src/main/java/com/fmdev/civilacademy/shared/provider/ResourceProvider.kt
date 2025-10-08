package com.fmdev.civilacademy.shared.provider

interface ResourceProvider {
    fun getString(resId: Int): String
    fun getString(resId: Int, vararg formatArgs: Any): String
}