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
 * @param onResult 返回回调。
 *
 * @return 执行结果，可能存在以下两种异常
 * - FragmentNotFoundException 无法找到uri对应的Fragment
 * - 自定义异常 路由被拦截
 */
fun Fragment.blink(
    uri: String,
    onResult: ((Bundle?) -> Unit)? = null
): Result<Unit> = runCatching { Blink.navigation(uri, this@blink, onResult) }

/**
 * @param uri 字符串类型的Uri
 * @param onResult 返回回调。
 *
 * @return 执行结果，可能存在以下两种异常
 * - FragmentNotFoundException 无法找到uri对应的Fragment
 * - 自定义异常 路由被拦截
 */
fun fragmentBlink(
    uri: String,
    onResult: ((Bundle?) -> Unit)? = null
): Result<Unit> = runCatching { Blink.navigation(uri, null, onResult) }

/**
 * @param uri Uri
 * @param onResult 返回回调。
 *
 * @return 执行结果，可能存在以下两种异常
 * - FragmentNotFoundException 无法找到uri对应的Fragment
 * - 自定义异常 路由被拦截
 */
fun Fragment.blink(
    uri: Uri,
    onResult: ((Bundle?) -> Unit)? = null
): Result<Unit> = runCatching { Blink.navigation(uri, this@blink, onResult) }

/**
 * @param uri Uri
 * @param onResult 返回回调。
 *
 * @return 执行结果，可能存在以下两种异常
 * - FragmentNotFoundException 无法找到uri对应的Fragment
 * - 自定义异常 路由被拦截
 */
fun fragmentBlink(
    uri: Uri,
    onResult: ((Bundle?) -> Unit)? = null
): Result<Unit> = runCatching { Blink.navigation(uri, null, onResult) }

/**
 * 返回，相当于Activity的finish，但可以返回数据给路由发起者
 */
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

fun Fragment.stringParams(name: String): Lazy<String?> = lazy {
    arguments?.uriOrNull?.getQueryParameter(name)
}

fun Fragment.stringParamsNonNull(name: String): Lazy<String> = lazy {
    arguments?.uriOrNull?.getQueryParameter(name) ?: ""
}

fun Fragment.stringsParams(name: String): Lazy<List<String>?> = lazy {
    arguments?.uriOrNull?.getQueryParameters(name)
}

fun Fragment.stringsParamsNonNull(name: String): Lazy<List<String>> = lazy {
    arguments?.uriOrNull?.getQueryParameters(name) ?: listOf()
}

fun Fragment.boolParams(name: String, default: Boolean = false): Lazy<Boolean> = lazy {
    arguments?.uriOrNull?.getBooleanQueryParameter(name, default) ?: default
}

fun Fragment.intParams(name: String, default: Int = 0): Lazy<Int> = lazy {
    arguments?.uriOrNull?.getQueryParameter(name)?.toIntOrNull() ?: default
}

fun Fragment.longParams(name: String, default: Long = 0L): Lazy<Long> = lazy {
    arguments?.uriOrNull?.getQueryParameter(name)?.toLongOrNull() ?: default
}

fun Fragment.floatParams(name: String, default: Float = 0f): Lazy<Float> = lazy {
    arguments?.uriOrNull?.getQueryParameter(name)?.toFloatOrNull() ?: default
}

fun Fragment.doubleParams(name: String, default: Double = 0.0): Lazy<Double> = lazy {
    arguments?.uriOrNull?.getQueryParameter(name)?.toDoubleOrNull() ?: default
}

inline fun <reified T> Fragment.enumParams(name: String): Lazy<T?> = lazy {
    if (T::class.java.isEnum) {
        val value = arguments?.uriOrNull?.getQueryParameter(name)
        val valueInt = value?.toIntOrNull()
        if (valueInt != null) {
            T::class.java.runCatching { enumConstants!![valueInt] }.getOrNull()
        } else if (value != null){
            T::class.java.runCatching { getMethod("valueOf", String::class.java).invoke(null, value) }.getOrNull() as T?
        } else null
    } else null
}

val Bundle.uriNonNull: Uri
    get() = uriOrNull!!

val Bundle.uriOrNull: Uri?
    get() = getParcelable(RouteMap.KEY_URI)

fun Bundle.setUri(uri: String) {
    setUri(uri.toUri())
}

fun Bundle.setUri(uri: Uri) {
    putParcelable(RouteMap.KEY_URI, uri)
}