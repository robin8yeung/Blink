package com.seewo.blink.fragment.interceptor

import android.os.Bundle
import androidx.fragment.app.Fragment

private const val GREEN_CHANNEL = "BLINK#GREEN#CHANNEL"

internal class Interceptors {
    private val interceptors = mutableListOf<Interceptor>()

    fun add(interceptor: Interceptor) {
        if (interceptors.contains(interceptor)) return
        interceptors += interceptor
        interceptors.sortByDescending { it.priority() }
    }

    fun remove(interceptor: Interceptor) {
        interceptors.remove(interceptor)
    }

    fun process(from: Fragment?, to: Fragment): Fragment? {
        var target = to
        interceptors.forEach {
            if (!target.isInGreenChannel(it) && it.filter(target)) {
                target = it.process(from, target) ?: return null
            }
        }
        return target
    }

    private fun Fragment.isInGreenChannel(interceptor: Interceptor): Boolean =
        arguments?.getSerializable(GREEN_CHANNEL) == interceptor::class.java
}

fun Interceptor.putInGreenChannel(fragment: Fragment) {
    fragment.arguments = (fragment.arguments ?: Bundle()).apply {
        putSerializable(GREEN_CHANNEL, this@putInGreenChannel::class.java)
    }
}