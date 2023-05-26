package com.seewo.blink.fragment

import android.net.Uri
import android.os.Bundle
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.seewo.blink.fragment.container.BlinkContainerFragment
import com.seewo.blink.fragment.interceptor.Interceptor
import com.seewo.blink.fragment.interceptor.InterruptedException

/**
 * @param uri 字符串类型的Uri
 * @param onResult ActivityResult回调。
 * 注意：对于Context不是FragmentActivity的情况设置回调，可能会导致共享元素动画异常
 * @return 执行结果，可能存在以下两种异常
 * - ActivityNotFoundException 无法找到uri对应的Activity
 * - 自定义异常 路由被拦截
 */
fun Fragment.blink(
    uri: String,
    onResult: ((Bundle?) -> Unit)? = null
): Result<Unit> = runCatching { Blink.navigation(uri, this@blink, onResult) }

fun fragmentBlink(
    uri: String,
    onResult: ((Bundle?) -> Unit)? = null
): Result<Unit> = runCatching { Blink.navigation(uri, null, onResult) }

/**
 * @param fragment 需要跳转的Fragment
 * @param onResult ActivityResult回调。
 * 注意：对于Context不是FragmentActivity的情况设置回调，可能会导致共享元素动画异常
 * @return 执行结果，可能存在以下两种异常
 * - ActivityNotFoundException 无法找到uri对应的Activity
 * - 自定义异常 路由被拦截
 */
fun Fragment.blink(
    fragment: Fragment,
    onResult: ((Bundle?) -> Unit)? = null
): Result<Unit> = runCatching { Blink.navigation(fragment, this@blink, onResult) }

fun fragmentBlink(
    fragment: Fragment,
    onResult: ((Bundle?) -> Unit)? = null
): Result<Unit> = runCatching { Blink.navigation(fragment, null, onResult) }

fun Fragment.pop(result: Bundle? = null) {
    (parentFragment as? BlinkContainerFragment)?.popResult(result) ?: parentFragmentManager.popBackStack()
}

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
 * 拦截器拦截路由建议抛出以下异常
 */
fun Interceptor.interrupt(msg: String? = null) {
    throw InterruptedException(this, msg)
}

fun Uri.stringParams(name: String): Lazy<String?> = lazy {
    getQueryParameter(name)
}

fun Uri.stringParamsNonNull(name: String): Lazy<String> = lazy {
    getQueryParameter(name) ?: ""
}

fun Uri.stringsParams(name: String): Lazy<List<String>?> = lazy {
    getQueryParameters(name)
}

fun Uri.stringsParamsNonNull(name: String): Lazy<List<String>> = lazy {
    getQueryParameters(name) ?: listOf()
}

fun Uri.boolParams(name: String, default: Boolean = false): Lazy<Boolean> = lazy {
    getBooleanQueryParameter(name, default)
}

fun Uri.intParams(name: String, default: Int = 0): Lazy<Int> = lazy {
    getQueryParameter(name)?.toIntOrNull() ?: default
}

fun Uri.longParams(name: String, default: Long = 0L): Lazy<Long> = lazy {
    getQueryParameter(name)?.toLongOrNull() ?: default
}

fun Uri.floatParams(name: String, default: Float = 0f): Lazy<Float> = lazy {
    getQueryParameter(name)?.toFloatOrNull() ?: default
}

fun Uri.doubleParams(name: String, default: Double = 0.0): Lazy<Double> = lazy {
    getQueryParameter(name)?.toDoubleOrNull() ?: default
}

inline fun <reified T> Uri.enumParams(name: String): Lazy<T?> = lazy {
    if (T::class.java.isEnum) {
        val value = getQueryParameter(name)
        val valueInt = value?.toIntOrNull()
        if (valueInt != null) {
            T::class.java.runCatching { enumConstants!![valueInt] }.getOrNull()
        } else if (value != null){
            T::class.java.runCatching { getMethod("valueOf", String::class.java).invoke(null, value) }.getOrNull() as T?
        } else null
    } else null
}

val Fragment.uriNonNull: Lazy<Uri>
    get()  = lazy {
        arguments!!.getString(RouteMap.KEY_URI)!!.toUri()
    }

val Fragment.uriOrNull: Lazy<Uri?>
    get()  = lazy {
        arguments?.getString(RouteMap.KEY_URI)?.runCatching { toUri() }?.getOrNull()
    }
