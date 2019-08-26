package com.androiddeveloper.tarunkumar.foursquareplacefinder.utils

interface ResourceProvider {

    fun getString(resourceIdentifier: Int, vararg arguments: Any = arrayOf()): String
}