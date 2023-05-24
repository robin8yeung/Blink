package com.seewo.blink.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment

/**
 * 路由表，可手动注册，也可通过ksp动态注册
 */
object RouteMap {
    const val KEY_URI = "uri"
    private val routeMetadataMap = mutableMapOf<String, Class<out Fragment>>()

    fun register(baseUri: String, fragment: Class<out Fragment>) {
        val confirmedBaseUri = baseUri.baseUri
        Log.e("robin", "register: $confirmedBaseUri >> $fragment")
        routeMetadataMap[confirmedBaseUri] = fragment
    }

    fun get(uri: String): Fragment? = routeMetadataMap[uri.baseUri]?.newInstance()?.apply {
        arguments = (arguments ?: Bundle()).apply {
            putString(KEY_URI, uri)
        }
    }
}

private val String.baseUri: String
    get() = split("?").first()
