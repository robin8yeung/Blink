package com.seewo.blink.fragment

import android.net.Uri
import android.os.Bundle
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.seewo.blink.fragment.container.BlinkContainerFragment
import com.seewo.blink.fragment.interceptor.BaseInterceptor
import com.seewo.blink.fragment.interceptor.InterruptedException
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * 导航到指定Fragment
 *
 * @param uri 字符串类型的Uri
 * @param onResult 返回回调。
 *
 * @return 执行结果，可能存在以下两种异常
 * - FragmentNotFoundException 无法找到uri对应的Fragment
 * - 自定义异常 路由被拦截
 */
@Deprecated("use Fragment.blinking() instead")
fun Fragment.blink(
    uri: String,
    onResult: ((Bundle?) -> Unit)? = null
): Result<Unit> = blink(uri.toUri(), onResult)

/**
 * 导航到指定Fragment
 *
 * @param uri 字符串类型的Uri
 * @param onResult 返回回调。
 *
 * @return 执行结果，可能存在以下两种异常
 * - FragmentNotFoundException 无法找到uri对应的Fragment
 * - 自定义异常 路由被拦截
 */
@Deprecated("use fragmentBlinking() instead")
fun fragmentBlink(
    uri: String,
    onResult: ((Bundle?) -> Unit)? = null
): Result<Unit> = fragmentBlink(uri.toUri(), onResult)

/**
 * 导航到指定Fragment
 *
 * @param uri Uri
 * @param onResult 返回回调。
 *
 * @return 执行结果，可能存在以下两种异常
 * - FragmentNotFoundException 无法找到uri对应的Fragment
 * - 自定义异常 路由被拦截
 */
@Deprecated("use Fragment.blinking() instead")
fun Fragment.blink(
    uri: Uri,
    onResult: ((Bundle?) -> Unit)? = null
): Result<Unit> = runCatching { runBlocking {
    Blink.navigation(uri, this@blink, onResult)
} }

/**
 * 导航到指定Fragment
 *
 * @param uri Uri
 * @param onResult 返回回调。
 *
 * @return 执行结果，可能存在以下两种异常
 * - FragmentNotFoundException 无法找到uri对应的Fragment
 * - 自定义异常 路由被拦截
 */
@Deprecated("use fragmentBlinking() instead")
fun fragmentBlink(
    uri: Uri,
    onResult: ((Bundle?) -> Unit)? = null
): Result<Unit> = runCatching { runBlocking {
    Blink.navigation(uri, null, onResult)
} }

/**
 * 导航到指定Fragment
 *
 * @param uri 字符串类型的Uri
 * @param onIntercepted 路由被拦截回调。如果回调返回的Throwable为null则表示路由成功执行
 * @param onResult 返回回调。
 *
 * @return 执行结果，可能存在以下两种异常
 * - FragmentNotFoundException 无法找到uri对应的Fragment
 * - 自定义异常 路由被拦截
 */
fun Fragment.blinking(
    uri: String,
    onIntercepted: ((Throwable?) -> Unit)? = null,
    onResult: ((Bundle?) -> Unit)? = null
) = blinking(uri.toUri(), onIntercepted, onResult)

/**
 * 导航到指定Fragment
 *
 * @param uri 字符串类型的Uri
 * @param onIntercepted 路由被拦截回调。如果回调返回的Throwable为null则表示路由成功执行
 * @param onResult 返回回调。
 *
 * @return 执行结果，可能存在以下两种异常
 * - FragmentNotFoundException 无法找到uri对应的Fragment
 * - 自定义异常 路由被拦截
 */
fun fragmentBlinking(
    uri: String,
    onIntercepted: ((Throwable?) -> Unit)? = null,
    onResult: ((Bundle?) -> Unit)? = null
) = fragmentBlinking(uri.toUri(), onIntercepted, onResult)

/**
 * 异步执行，导航到指定Fragment
 *
 * @param uri Uri
 * @param onIntercepted 路由被拦截回调。如果回调返回的Throwable为null则表示路由成功执行
 * @param onResult 返回回调。
 *
 * @return 执行结果，可能存在以下两种异常
 * - FragmentNotFoundException 无法找到uri对应的Fragment
 * - 自定义异常 路由被拦截
 */
fun Fragment.blinking(
    uri: Uri,
    onIntercepted: ((Throwable?) -> Unit)? = null,
    onResult: ((Bundle?) -> Unit)? = null
) {
    MainScope().launch {
        runCatching { Blink.navigation(uri, this@blinking, onResult) }.apply {
            onIntercepted?.invoke(exceptionOrNull())
        }
    }
}

/**
 * 异步执行，导航到指定Fragment
 *
 * @param uri Uri
 * @param onIntercepted 路由被拦截回调。如果回调返回的Throwable为null则表示路由成功执行
 * @param onResult 返回回调。
 *
 * @return 执行结果，可能存在以下两种异常
 * - FragmentNotFoundException 无法找到uri对应的Fragment
 * - 自定义异常 路由被拦截
 */
fun fragmentBlinking(
    uri: Uri,
    onIntercepted: ((Throwable?) -> Unit)? = null,
    onResult: ((Bundle?) -> Unit)? = null
) {
    MainScope().launch {
        runCatching { Blink.navigation(uri, null, onResult) }.apply {
            onIntercepted?.invoke(exceptionOrNull())
        }
    }
}

/**
 * 返回，相当于Activity的finish，但可以返回数据给路由发起者
 */
fun Fragment.pop(result: Bundle? = null) {
    (parentFragment as? BlinkContainerFragment)?.popResult(result) ?: parentFragmentManager.popBackStack()
}

/**
 * 返回到指定的Fragment（用uri来指定）
 *
 * @param uri 回退到的页面对应的uri。如果页面定义了多个uri，回退时只会匹配到实际导航到该页面的uri
 * @return 是否成功回退
 *
 * 特别注意：
 *          1. 如果回退栈中存在多个uri定义相同的Fragment，那会回退到最近的一个
 *          2. 需要回退到首个Fragment，即通过 BlinkContainerActivity.startFragment()传入的Fragment，其对应的uri为空字符串
 */
fun Fragment.popTo(uri: String): Boolean {
    return (parentFragment as? BlinkContainerFragment)?.popTo(uri) ?: false
}

/**
 * 添加拦截器
 */
fun BaseInterceptor.attach() {
    Blink.add(this)
}

/**
 * 移除拦截器
 */
fun BaseInterceptor.detach() {
    Blink.remove(this)
}

/**
 * 拦截器拦截路由建议抛出以下异常
 */
fun BaseInterceptor.interrupt(msg: String? = null) {
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