package com.androiddeveloper.tarunkumar.foursquareplacefinder.utils

import android.content.Context
import androidx.annotation.StringRes

class ResourceProviderImpl(val context: Context) : ResourceProvider {
    override fun getString(@StringRes resourceIdentifier: Int, vararg arguments: Any): String {
        return if (arguments.isNotEmpty())
            context.resources.getString(resourceIdentifier, *arguments)
        else
            context.resources.getString(resourceIdentifier)
    }
}