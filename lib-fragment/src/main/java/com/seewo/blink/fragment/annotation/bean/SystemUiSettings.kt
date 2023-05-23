package com.seewo.blink.fragment.annotation.bean

/**
 * 系统UI的默认样式
 *
 * @param fullscreen true 全屏; false 非全屏
 * @param statusBarLight true 系统状态栏为黑色字体; false 系统状态栏为白色字体
 * @param navigationBarLight true 导航栏为白色; false 导航栏为深色
 */
internal class SystemUiSettings(
    val fullscreen: Boolean = false,
    val statusBarLight: Boolean = true,
    val navigationBarLight: Boolean = true,
)
