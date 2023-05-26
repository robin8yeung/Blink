package com.seewo.blink

import android.content.Context
import androidx.startup.Initializer

class BlinkInitializer : Initializer<Unit>{
    override fun create(context: Context) {
        Blink.init(context)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}