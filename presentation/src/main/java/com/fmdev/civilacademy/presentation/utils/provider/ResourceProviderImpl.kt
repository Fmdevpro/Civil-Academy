package com.fmdev.civilacademy.presentation.utils.provider

import android.content.Context
import com.fmdev.civilacademy.shared.provider.ResourceProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ResourceProviderImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : ResourceProvider {

    override fun getString(resId: Int): String {
        return context.getString(resId)
    }

    override fun getString(resId: Int, vararg formatArgs: Any): String {
        return context.getString(resId, *formatArgs)
    }
}