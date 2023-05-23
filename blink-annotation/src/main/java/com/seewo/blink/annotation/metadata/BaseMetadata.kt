package com.seewo.blink.annotation.metadata

abstract class BaseMetadata {
    fun inject() {
        val ktx = Class.forName("com.seewo.blink.Initializer_${this::class.java.canonicalName.replace('.', '_')}Kt")
        ktx.getMethod("_inject", this::class.java).invoke(ktx, this)
    }
}