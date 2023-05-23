package com.seewo.blink.fragment

import android.content.Context
import androidx.startup.Initializer

class BlinkInitializer : Initializer<Unit>{
    override fun create(context: Context) {
//        Blink.context = context
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}