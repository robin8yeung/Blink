package com.seewo.blink

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.core.app.ActivityOptionsCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.seewo.blink.interceptor.Interceptor
import com.seewo.blink.interceptor.InterruptedException


/**
 * 添加拦截器
 */
fun Interceptor.attach() {
    Blink.add(this)
}

/**
 * 移除拦截器
 */
fun Interceptor.detach() {
    Blink.remove(this)
}

/**
 * 通过uri创建Blink使用的Intent
 */
fun Uri.createBlinkIntent() = Blink.createIntent(this)

/**
 * 通过uri string创建Blink使用的Intent
 */
fun String.createBlinkIntent() = toUri().createBlinkIntent()

/**
 * @param uri 字符串类型的Uri
 * @param options 控制跳转动画
 * @param onResult ActivityResult回调。
 * 注意：对于Context不是FragmentActivity的情况设置回调，可能会导致共享元素动画异常
 * @return 执行结果，可能存在以下两种异常
 * - ActivityNotFoundException 无法找到uri对应的Activity
 * - 自定义异常 路由被拦截
 */
fun Context.blink(
    uri: String,
    options: ActivityOptionsCompat? = null,
    onResult: ActivityResultCallback<ActivityResult>? = null
): Result<Unit> = kotlin.runCatching { Blink.navigationForResult(this, uri, options, onResult) }

/**
 * @param uri Uri类型的Uri
 * @param options 控制跳转动画
 * @param onResult ActivityResult回调。
 * 注意：对于Context不是FragmentActivity的情况设置回调，可能会导致共享元素动画异常
 * @return 执行结果，可能存在以下两种异常
 * - ActivityNotFoundException 无法找到uri对应的Activity
 * - 自定义异常 路由被拦截
 */
fun Context.blink(
    uri: Uri,
    options: ActivityOptionsCompat? = null,
    onResult: ActivityResultCallback<ActivityResult>? = null
): Result<Unit> = kotlin.runCatching { Blink.navigationForResult(this, uri, options, onResult) }

/**
 * @param intent 建议使用 Uri.createIntent()方法来创建Intent
 * @param options 控制跳转动画
 * @param onResult ActivityResult回调。
 * 注意：对于Context不是FragmentActivity的情况设置回调，可能会导致共享元素动画异常
 * @return 执行结果，可能存在以下两种异常
 * - ActivityNotFoundException 无法找到uri对应的Activity
 * - NavigationInterruptedException 路由被拦截
 * @see createBlinkIntent
 */
fun Context.blink(
    intent: Intent,
    options: ActivityOptionsCompat? = null,
    onResult: ActivityResultCallback<ActivityResult>? = null
): Result<Unit> = kotlin.runCatching { Blink.navigationForResult(this, intent, options, onResult) }

/**
 * 拦截器拦截路由建议抛出以下异常
 */
fun Interceptor.interrupt(msg: String? = null) {
    throw InterruptedException(this, msg)
}

fun Activity.stringParams(name: String): Lazy<String?> = lazy {
    intent.data?.getQueryParameter(name)
}

fun Activity.stringParamsNonNull(name: String): Lazy<String> = lazy {
    intent.data?.getQueryParameter(name) ?: ""
}

fun Activity.stringsParams(name: String): Lazy<List<String>?> = lazy {
    intent.data?.getQueryParameters(name)
}

fun Activity.stringsParamsNonNull(name: String): Lazy<List<String>> = lazy {
    intent.data?.getQueryParameters(name) ?: listOf()
}

fun Activity.boolParams(name: String, default: Boolean = false): Lazy<Boolean> = lazy {
    intent.data?.getBooleanQueryParameter(name, default) ?: default
}

fun Activity.intParams(name: String, default: Int = 0): Lazy<Int> = lazy {
    intent.data?.getQueryParameter(name)?.toIntOrNull() ?: default
}

fun Activity.longParams(name: String, default: Long = 0L): Lazy<Long> = lazy {
    intent.data?.getQueryParameter(name)?.toLongOrNull() ?: default
}

fun Activity.floatParams(name: String, default: Float = 0f): Lazy<Float> = lazy {
    intent.data?.getQueryParameter(name)?.toFloatOrNull() ?: default
}

fun Activity.doubleParams(name: String, default: Double = 0.0): Lazy<Double> = lazy {
    intent.data?.getQueryParameter(name)?.toDoubleOrNull() ?: default
}

inline fun <reified T> Activity.enumParams(name: String): Lazy<T?> = lazy {
    if (T::class.java.isEnum) {
        val value = intent.data?.getQueryParameter(name)
        val valueInt = value?.toIntOrNull()
        if (valueInt != null) {
            T::class.java.runCatching { enumConstants!![valueInt] }.getOrNull()
        } else if (value != null){
            T::class.java.runCatching { getMethod("valueOf", String::class.java).invoke(null, value) }.getOrNull() as T?
        } else null
    } else null
}

fun Fragment.stringParams(name: String): Lazy<String?> = lazy {
    arguments?.getString(name)
}

fun Fragment.stringsParams(name: String): Lazy<List<String>?> = lazy {
    arguments?.getStringArrayList(name)
}

fun Fragment.boolParams(name: String, default: Boolean = false): Lazy<Boolean> = lazy {
    arguments?.getBoolean(name, default) ?: default
}

fun Fragment.intParams(name: String, default: Int = 0): Lazy<Int> = lazy {
    arguments?.getInt(name, default) ?: default
}

fun Fragment.longParams(name: String, default: Long = 0L): Lazy<Long> = lazy {
    arguments?.getLong(name, default) ?: default
}

fun Fragment.floatParams(name: String, default: Float = 0f): Lazy<Float> = lazy {
    arguments?.getFloat(name, default) ?: default
}

fun Fragment.doubleParams(name: String, default: Double = 0.0): Lazy<Double> = lazy {
    arguments?.getDouble(name, default) ?: default
}

inline fun <reified T> Fragment.enumParams(name: String): Lazy<T?> = lazy {
    if (T::class.java.isEnum) {
        val valueInt = arguments?.runCatching { getInt(name) }?.getOrNull()
        val value = arguments?.getString(name)
        if (valueInt != null) {
            T::class.java.runCatching { enumConstants!![valueInt] }.getOrNull()
        } else if (value != null){
            T::class.java.runCatching { getMethod("valueOf", String::class.java).invoke(null, value) }.getOrNull() as T?
        } else null
    } else null
}

fun Interceptor.putInGreenChannel(intent: Intent) = Blink.greenChannel(this, intent)
