package com.seewo.blink.fragment.annotation

import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class CustomAnimations(
    @AnimatorRes @AnimRes val enter: Int = 0,
    @AnimatorRes @AnimRes val exit: Int = 0,
    @AnimatorRes @AnimRes val popEnter: Int = 0,
    @AnimatorRes @AnimRes val popExit: Int = 0,
)
