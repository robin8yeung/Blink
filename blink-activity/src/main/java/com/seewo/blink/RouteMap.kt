package com.seewo.blink

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import com.seewo.blink.utils.baseUri
import java.util.concurrent.ConcurrentHashMap

/**
 * 路由表，可手动注册，也可通过ksp动态注册
 */
object RouteMap {
    private val routeMetadataMap = ConcurrentHashMap<String, Class<out Activity>>()

    fun register(baseUri: String, activity: Class<out Activity>) {
        val confirmedBaseUri = baseUri.baseUri
        routeMetadataMap[confirmedBaseUri] = activity
    }

    fun get(uri: String): Intent = get(uri.toUri())

    fun get(uri: Uri): Intent = routeMetadataMap[uri.baseUri]?.let {
        Intent(Blink.context, it).apply {
            data = uri
        }
    } ?: Intent("action.Blink.ActivityNotFound", uri)

    fun dump(): Map<String, Class<out Activity>> = routeMetadataMap
}

private val String.baseUri: String
    get() = split("?").first()
