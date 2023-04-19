package com.seewo.blink

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.seewo.blink.interceptor.Interceptor
import com.seewo.blink.interceptor.Interceptors
import com.seewo.blink.stub.ResultHolder
import java.lang.reflect.ParameterizedType

/**
 * 基于uri来实现Activity路由
 *
 * - 路由功能
 * - Intent修改
 * - 路由结果回调 通过Result
 * - 页面结果回调
 * - 拦截器
 */
object Blink {
    private val interceptors = Interceptors()

    /**
     * Intent的action，默认如下，业务可自定义
     */
    @JvmStatic
    var action = "blink.action.VIEW"

    /**
     * 添加拦截器
     */
    @JvmStatic
    fun add(interceptor: Interceptor) {
        interceptors.add(interceptor)
    }

    /**
     * 移除拦截器
     */
    @JvmStatic
    fun remove(interceptor: Interceptor) {
        interceptors.remove(interceptor)
    }

    /**
     * 通过uri创建UriNavigator使用的Intent
     */
    @JvmStatic
    fun createIntent(uri: Uri) = Intent(action).apply {
        data = uri
    }

    /**
     * 主要提供给java调用
     * @throws ActivityNotFoundException 无法找到uri对应的Activity
     * @throws InterruptedException 自定义异常 路由被拦截
     */
    @kotlin.jvm.Throws(
        ActivityNotFoundException::class,
        com.seewo.blink.interceptor.InterruptedException::class
    )
    @JvmStatic
    fun navigation(
        context: Context,
        uri: String,
    ) = navigationForResult(context, uri)

    /**
     * 主要提供给java调用
     * @throws ActivityNotFoundException 无法找到uri对应的Activity
     * @throws InterruptedException 自定义异常 路由被拦截
     */
    @kotlin.jvm.Throws(
        ActivityNotFoundException::class,
        com.seewo.blink.interceptor.InterruptedException::class
    )
    @JvmStatic
    fun navigationForResult(
        context: Context,
        uri: String,
        options: ActivityOptionsCompat? = null,
        onResult: ActivityResultCallback<ActivityResult>? = null
    ) = navigationForResult(context, Uri.parse(uri), options, onResult)

    /**
     * 主要提供给java调用
     * @throws ActivityNotFoundException 无法找到uri对应的Activity
     * @throws InterruptedException 自定义异常 路由被拦截
     */
    @kotlin.jvm.Throws(
        ActivityNotFoundException::class,
        com.seewo.blink.interceptor.InterruptedException::class
    )
    @JvmStatic
    fun navigation(
        context: Context,
        uri: Uri,
    ) = navigationForResult(context, uri)

    /**
     * 主要提供给java调用
     * @throws ActivityNotFoundException 无法找到uri对应的Activity
     * @throws InterruptedException 自定义异常 路由被拦截
     */
    @kotlin.jvm.Throws(
        ActivityNotFoundException::class,
        com.seewo.blink.interceptor.InterruptedException::class
    )
    @JvmStatic
    fun navigationForResult(
        context: Context,
        uri: Uri,
        options: ActivityOptionsCompat? = null,
        onResult: ActivityResultCallback<ActivityResult>? = null
    ) = navigationForResult(context, createIntent(uri), options, onResult)

    /**
     * 主要提供给java调用
     * @throws ActivityNotFoundException 无法找到uri对应的Activity
     * @throws InterruptedException 自定义异常 路由被拦截
     */
    @kotlin.jvm.Throws(
        ActivityNotFoundException::class,
        com.seewo.blink.interceptor.InterruptedException::class
    )
    @JvmStatic
    fun navigation(
        context: Context,
        intent: Intent,
    ) = navigationForResult(context, intent)

    /**
     * 主要提供给java调用
     * @throws ActivityNotFoundException 无法找到uri对应的Activity
     * @throws InterruptedException 自定义异常 路由被拦截
     */
    @kotlin.jvm.Throws(
        ActivityNotFoundException::class,
        com.seewo.blink.interceptor.InterruptedException::class
    )
    @JvmStatic
    fun navigationForResult(
        context: Context,
        intent: Intent,
        options: ActivityOptionsCompat? = null,
        onResult: ActivityResultCallback<ActivityResult>? = null
    ) {
        doNavigation(context, intent, options, onResult)
    }

    private fun doNavigation(
        context: Context,
        intent: Intent,
        options: ActivityOptionsCompat?,
        onResult: ActivityResultCallback<ActivityResult>?
    ) {
        interceptors.process(context, intent)
        if (onResult == null) {
            context.startActivity(intent.apply {
                if (context !is Activity) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }, options?.toBundle())
        } else {
            when (context) {
                is FragmentActivity -> {
                    ResultHolder.launchWithFragment(context, intent, options, onResult)
                }
                else -> {
                    ResultHolder.launch(context, intent, options, onResult)
                }
            }
        }
    }

    @JvmStatic
    fun getStringParams(uri: Uri, name: String): String? {
        return uri.getQueryParameter(name)
    }

    @JvmStatic
    fun getStringsParams(uri: Uri, name: String): List<String>? {
        return uri.getQueryParameters(name)
    }

    @JvmStatic
    fun getBoolParams(uri: Uri, name: String, default: Boolean): Boolean {
        return uri.getQueryParameter(name)?.toBooleanStrictOrNull() ?: default
    }

    @JvmStatic
    fun getIntParams(uri: Uri, name: String, default: Int): Int {
        return uri.getQueryParameter(name)?.toIntOrNull() ?: default
    }

    @JvmStatic
    fun getLongParams(uri: Uri, name: String, default: Long): Long {
        return uri.getQueryParameter(name)?.toLongOrNull() ?: default
    }

    @JvmStatic
    fun getFloatParams(uri: Uri, name: String, default: Float): Float {
        return uri.getQueryParameter(name)?.toFloatOrNull() ?: default
    }

    @JvmStatic
    fun getDoubleParams(uri: Uri, name: String, default: Double): Double {
        return uri.getQueryParameter(name)?.toDoubleOrNull() ?: default
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    @JvmStatic
    fun inject(host: Activity) {
        val uri = host.intent.data ?: return
        host.javaClass.declaredFields
            .filter { it.isAnnotationPresent(BlinkParams::class.java) }
            .forEach {
                val key = it.getAnnotation(BlinkParams::class.java)?.name ?: return@forEach
                if (key.isBlank()) return@forEach
                it.isAccessible = true
                when (it.type) {
                    String::class.java -> uri.stringParams(key)
                    Int::class.java -> uri.intParams(key)
                    Long::class.java -> uri.longParams(key)
                    Float::class.java -> uri.floatParams(key)
                    Double::class.java -> uri.doubleParams(key)
                    Boolean::class.java -> uri.booleanParams(key)
                    List::class.java -> when ((it.genericType as ParameterizedType).actualTypeArguments.first()) {
                        String::class.java -> uri.getQueryParameters(key)
                        else -> null
                    }
                    else -> if (it.type.isEnum) {
                        val index = uri.intParams(key)
                        if (index != null) {
                            it.type.runCatching { enumConstants[index] }.getOrNull()
                        } else {
                            val string = uri.stringParams(key)
                            if (string != null) {
                                it.type.runCatching {
                                    getMethod("valueOf", String::class.java)
                                        .invoke(null, string)
                                }.getOrNull()
                            } else null
                        }
                    } else null
                }?.runCatching {
                    it.set(host, this)
                }
            }
    }

    @JvmStatic
    fun inject(host: Fragment) {
        val bundle = host.arguments ?: return
        host.javaClass.declaredFields
            .filter { it.isAnnotationPresent(BlinkParams::class.java) }
            .forEach {
                val key = it.getAnnotation(BlinkParams::class.java)?.name ?: return@forEach
                if (key.isBlank()) return@forEach
                it.isAccessible = true
                when {
                    it.type == String::class.java -> bundle.getString(key)
                    it.type == Int::class.java -> bundle.getInt(key)
                    it.type == Long::class.java -> bundle.getLong(key)
                    it.type == Float::class.java -> bundle.getFloat(key)
                    it.type == Double::class.java -> bundle.getDouble(key)
                    it.type == Boolean::class.java -> bundle.getBoolean(key)
                    it.type == IntArray::class.java -> bundle.getIntArray(key)
                    it.type == LongArray::class.java -> bundle.getLongArray(key)
                    it.type == FloatArray::class.java -> bundle.getFloatArray(key)
                    it.type == DoubleArray::class.java -> bundle.getDoubleArray(key)
                    it.type == BooleanArray::class.java -> bundle.getBooleanArray(key)
                    Parcelable::class.java.isAssignableFrom(it.type) -> bundle.getParcelable(key)
                    java.io.Serializable::class.java.isAssignableFrom(it.type) -> bundle.getSerializable(
                        key
                    )
                    List::class.java.isAssignableFrom(it.type) -> {
                        val typeOfList =
                            ((it.genericType as ParameterizedType).actualTypeArguments.first() as ParameterizedType).rawType as Class<*>
                        when {
                            typeOfList == String::class.java -> bundle.getStringArrayList(key)
                            typeOfList == Int::class.java -> bundle.getIntegerArrayList(key)
                            Parcelable::class.java.isAssignableFrom(typeOfList) -> bundle.getParcelableArrayList(
                                key
                            )
                            else -> null
                        }
                    }
                    else -> null
                }?.runCatching {
                    it.set(host, this)
                }
            }
    }

    private fun Uri.stringParams(key: String) = getQueryParameter(key)
    private fun Uri.intParams(key: String) = getQueryParameter(key)?.toIntOrNull()
    private fun Uri.longParams(key: String) = getQueryParameter(key)?.toLongOrNull()
    private fun Uri.floatParams(key: String) = getQueryParameter(key)?.toFloatOrNull()
    private fun Uri.doubleParams(key: String) = getQueryParameter(key)?.toDoubleOrNull()
    private fun Uri.booleanParams(key: String) = getQueryParameter(key)?.toBooleanStrictOrNull()
        ?: (getQueryParameter(key)?.toIntOrNull() == 1)
}