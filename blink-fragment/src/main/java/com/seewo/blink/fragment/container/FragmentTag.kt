package com.seewo.blink.fragment.container

import com.seewo.blink.fragment.mode.LaunchMode

internal class FragmentTag(private val tag: String?) {
    val launchMode: LaunchMode
    val uri: String?
    val className: String?
    val hashCode: Int

    init {
        val tags = tag?.split(";")
        launchMode = kotlin.runCatching {
            tags?.get(0)?.let { LaunchMode.valueOf(it) }
        }.getOrNull() ?: LaunchMode.NORMAL
        uri = kotlin.runCatching { tags?.get(1) }.getOrNull()
        className = kotlin.runCatching { tags?.get(2) }.getOrNull()
        hashCode = kotlin.runCatching { tags?.get(3) }.getOrNull()?.toIntOrNull() ?: 0
    }

    override fun toString(): String = tag ?: super.toString()
}