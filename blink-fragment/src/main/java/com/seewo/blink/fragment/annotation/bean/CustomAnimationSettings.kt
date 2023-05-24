package com.seewo.blink.fragment.annotation.bean

import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes

internal class CustomAnimationSettings(
    @AnimatorRes @AnimRes val enter: Int = 0,
    @AnimatorRes @AnimRes val exit: Int = 0,
    @AnimatorRes @AnimRes val popEnter: Int = 0,
    @AnimatorRes @AnimRes val popExit: Int = 0,
)
