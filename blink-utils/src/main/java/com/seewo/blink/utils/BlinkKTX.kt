package com.seewo.blink.utils

import android.net.Uri


private fun Uri.Builder.appendQueryParameter(key: String, value: Any) = appendQueryParameter(key, "$value")

fun Uri.Builder.append(key: String, value: Any?): Uri.Builder {
    value ?: return this
    if (value is Collection<*>) {
        if (value.isEmpty()) {
            return appendQueryParameter(key, "")
        } else {
            value.forEach { item ->
                item ?: return@forEach
                appendQueryParameter(key, item)
            }
            return this
        }
    }
    return appendQueryParameter(key, "$value")
}

fun Uri.build(block: Uri.Builder.() -> Unit) = buildUpon().apply(block).build()
fun String.buildUri(block: Uri.Builder.() -> Unit) = Uri.parse(this).buildUpon().apply(block).build()
