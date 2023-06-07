package com.seewo.blink.fragment

import androidx.fragment.app.Fragment
import com.seewo.blink.fragment.container.BlinkContainerFragment
import com.seewo.blink.fragment.mode.LaunchMode
import com.seewo.blink.fragment.mode.SingleTaskFragment
import com.seewo.blink.fragment.mode.SingleTopFragment

private val Fragment.mode: LaunchMode
    get() = when (this) {
        is SingleTaskFragment -> LaunchMode.SINGLE_TASK
        is SingleTopFragment -> LaunchMode.SINGLE_TOP
        else -> LaunchMode.NORMAL
    }

private val Fragment.who: String?
    get() = runCatching { Fragment::class.java.getDeclaredField("mWho").apply {
        isAccessible = true
    }.get(this) as? String }.getOrNull()

val Fragment.generateFragmentTag: String
    get() = (this as? BlinkContainerFragment)?.fragmentTag
        ?: "$mode;${this.arguments?.uriOrNull ?: ""};${this::class.java.canonicalName};${hashCode()};${who}"