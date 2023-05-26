package com.seewo.blink.fragment.annotation

/**
 * 系统UI的默认样式
 *
 * @param hideStatusBar true 隐藏状态栏
 * @param hideNavigationBar true 隐藏导航栏
 * @param brightnessLight true 系统状态栏、导航栏为白底黑字; false 系统状态栏为黑底白字
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class SystemUI(
    val hideStatusBar: Boolean = false,
    val hideNavigationBar: Boolean = false,
    val brightnessLight: Boolean = true,
)
