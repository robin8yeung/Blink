package com.seewo.blink.fragment.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

fun Activity.hideStatusBar() {
    WindowCompat.getInsetsController(window, window.decorView).run {
        hide(WindowInsetsCompat.Type.statusBars())
        WindowCompat.setDecorFitsSystemWindows(window, false)
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

fun Activity.showStatusBar() {
    WindowCompat.getInsetsController(window, window.decorView).run {
        show(WindowInsetsCompat.Type.statusBars())
    }
}

fun Activity.hideNavigationBar() {
    WindowCompat.getInsetsController(window, window.decorView).run {
        hide(WindowInsetsCompat.Type.navigationBars())
        WindowCompat.setDecorFitsSystemWindows(window, false)
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

fun Activity.showNavigationBar() {
    WindowCompat.getInsetsController(window, window.decorView).run {
        show(WindowInsetsCompat.Type.navigationBars())
    }
}

fun Activity.setStatusBarTransparent(brightnessLight: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        window.attributes.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
    }
    setStatusBarTransparentOld(brightnessLight)
}

private fun Activity.setStatusBarTransparentOld(brightnessLight: Boolean) {
    when {
        MIUISetStatusBarLightMode(brightnessLight) -> Unit
        FlymeSetStatusBarLightMode(brightnessLight) -> Unit
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
            setStatusBarTransparentWithSys(brightnessLight)
        }
    }
}

/**
 * 设置状态栏图标为深色和魅族特定的文字风格，Flyme4.0以上
 * 可以用来判断是否为Flyme用户
 *
 * @param dark   是否把状态栏字体及图标颜色设置为深色
 * @return boolean 成功执行返回true
 */
private fun Activity.FlymeSetStatusBarLightMode(dark: Boolean): Boolean {
    var result = false
    if (window != null) {
        try {
            val lp = window.attributes
            val darkFlag = WindowManager.LayoutParams::class.java
                .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
            val meizuFlags = WindowManager.LayoutParams::class.java
                .getDeclaredField("meizuFlags")
            darkFlag.isAccessible = true
            meizuFlags.isAccessible = true
            val bit = darkFlag.getInt(null)
            var value = meizuFlags.getInt(lp)
            value = if (dark) {
                value or bit
            } else {
                value and bit.inv()
            }
            meizuFlags.setInt(lp, value)
            window.attributes = lp
            result = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 新版本采用了系统API，旧方法无效但不会报错，所以两个方式都要加上
                setStatusBarTransparentWithSys(dark)
            }
        } catch (ignore: Exception) { }
    }
    return result
}

/**
 * 需要MIUIV6以上
 *
 * @param dark 是否把状态栏文字及图标颜色设置为深色
 * @return boolean 成功执行返回true
 */
private fun Activity.MIUISetStatusBarLightMode(dark: Boolean): Boolean {
    var result = false
    if (window != null) {
        val clazz: Class<*> = window.javaClass
        try {
            var darkModeFlag = 0
            @SuppressLint("PrivateApi") val layoutParams =
                Class.forName("android.view.MiuiWindowManager\$LayoutParams")
            val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
            darkModeFlag = field.getInt(layoutParams)
            val extraFlagField = clazz.getMethod(
                "setExtraFlags",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType
            )
            if (dark) {
                extraFlagField.invoke(window, darkModeFlag, darkModeFlag) //状态栏透明且黑色字体
            } else {
                extraFlagField.invoke(window, 0, darkModeFlag) //清除黑色字体
            }
            result = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //开发版 7.7.13 及以后版本采用了系统API，旧方法无效但不会报错，所以两个方式都要加上
                setStatusBarTransparentWithSys(dark)
            }
        } catch (ignore: Exception) { }
    }
    return result
}

private fun Activity.setStatusBarTransparentWithSys(lightStatusBar: Boolean) {
    WindowCompat.getInsetsController(window, window.decorView).run {
        isAppearanceLightStatusBars = lightStatusBar
        isAppearanceLightNavigationBars = lightStatusBar
    }
    window.run {
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        statusBarColor = Color.TRANSPARENT
        navigationBarColor = Color.TRANSPARENT
    }
    WindowCompat.setDecorFitsSystemWindows(window, false)
}