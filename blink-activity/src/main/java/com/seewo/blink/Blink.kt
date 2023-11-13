package com.seewo.blink

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.seewo.blink.callback.UriBuilder
import com.seewo.blink.interceptor.BaseInterceptor
import com.seewo.blink.interceptor.Interceptor
import com.seewo.blink.interceptor.Interceptors
import com.seewo.blink.stub.ResultHolder
import com.seewo.blink.utils.append
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.Serializable
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
@SuppressLint("StaticFieldLeak")
object Blink {
    internal const val GREEN_CHANNEL = "Blink#GREEN#CHANNEL"
    private val interceptors = Interceptors()

    internal lateinit var context: Context

    /**
     * 如果有依赖startup框架，会自动初始化。否则可以调用此方法初始化
     */
    fun init(context: Context) {
        this.context = context.applicationContext
    }

    /**
     * Intent的action，默认如下，业务可自定义
     */
    @JvmStatic
    var action = "blink.action.VIEW"

    /**
     * 添加拦截器
     */
    @JvmStatic
    fun add(interceptor: BaseInterceptor) {
        interceptors.add(interceptor)
    }

    /**
     * 移除拦截器
     */
    @JvmStatic
    fun remove(interceptor: BaseInterceptor) {
        interceptors.remove(interceptor)
    }

    /**
     * 通过uri创建UriNavigator使用的Intent
     */
    @JvmStatic
    fun createIntent(uri: Uri) = RouteMap.get(uri)

    /**
     * 通过uri创建UriNavigator使用的Intent
     */
    @JvmStatic
    fun createIntent(uri: String) = RouteMap.get(uri)

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
        Log.println(Log.ASSERT, "robin", "A")
        runBlocking {
            Log.println(Log.ASSERT, "robin", "B")
            doNavigation(context, intent, options, onResult)
        }
    }

    suspend fun asyncNavigationForResult(
        context: Context,
        intent: Intent,
        options: ActivityOptionsCompat? = null,
        onResult: ActivityResultCallback<ActivityResult>? = null
    ) {
        doNavigation(context, intent, options, onResult)
    }

    private suspend fun doNavigation(
        context: Context,
        intent: Intent,
        options: ActivityOptionsCompat?,
        onResult: ActivityResultCallback<ActivityResult>?
    ) {
        withContext(Dispatchers.IO) {
            interceptors.process(context, intent)
            intent.data?.let {
                intent.component = RouteMap.get(it).component
            }
        }
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

    @JvmStatic
    fun greenChannel(interceptor: Interceptor, intent: Intent) = intent.apply { putExtra(GREEN_CHANNEL, interceptor::class.java) }

    @JvmStatic
    fun buildUri(uri: String, builder: UriBuilder): Uri = buildUri(Uri.parse(uri), builder)

    @JvmStatic
    fun buildUri(uri: Uri, builder: UriBuilder): Uri = uri.run {
        buildUpon().also { builder.build(it) }.build()
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    @JvmStatic
    fun inject(host: Activity) {
        val uri = host.intent.data ?: return
        val javaClass = host.javaClass
        injectActivity(javaClass, uri, host)
    }

    private fun injectActivity(
        clazz: Class<in Activity>,
        uri: Uri,
        host: Activity
    ) {
        clazz.declaredFields
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
                    ArrayList::class.java, List::class.java -> when ((it.genericType as ParameterizedType).actualTypeArguments.first()) {
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
        val superClass = clazz.superclass ?: return
        injectActivity(superClass, uri, host)
    }

    @JvmStatic
    fun inject(host: Fragment) {
        val bundle = host.arguments ?: return
        val javaClass = host.javaClass
        injectFragment(javaClass, bundle, host)
    }

    private fun injectFragment(
        clazz: Class<in Fragment>,
        bundle: Bundle,
        host: Fragment
    ) {
        clazz.declaredFields
            .filter { it.isAnnotationPresent(BlinkParams::class.java) }
            .forEach {
                val key = it.getAnnotation(BlinkParams::class.java)?.name ?: return@forEach
                if (key.isBlank()) return@forEach
                it.isAccessible = true
                // 注意：此处必须把变量的类型定义出来，否则用apply时遇到了kotlin把输出强转为serializable的问题
                val value: Any? = when {
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
                    Parcelable::class.java.isAssignableFrom(it.type) ->
                        bundle.getParcelable(key)
                    Serializable::class.java.isAssignableFrom(it.type) ->
                        bundle.getSerializable(key)
                    List::class.java.isAssignableFrom(it.type) -> {
                        val typeOfList =
                            (it.genericType as ParameterizedType).actualTypeArguments.first() as Class<*>
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
                }
                value?.runCatching {
                    it.set(host, this)
                }
            }
        val superClass = clazz.superclass ?: return
        injectFragment(superClass, bundle, host)
    }

    private fun Uri.stringParams(key: String) = getQueryParameter(key)
    private fun Uri.intParams(key: String) = getQueryParameter(key)?.toIntOrNull()
    private fun Uri.longParams(key: String) = getQueryParameter(key)?.toLongOrNull()
    private fun Uri.floatParams(key: String) = getQueryParameter(key)?.toFloatOrNull()
    private fun Uri.doubleParams(key: String) = getQueryParameter(key)?.toDoubleOrNull()
    private fun Uri.booleanParams(key: String) = getQueryParameter(key)?.toBooleanStrictOrNull()
        ?: getQueryParameter(key)?.toIntOrNull()?.let { it == 1 }

    @JvmStatic
    fun appendStrings(builder: Uri.Builder, key: String, strings: List<String>?) {
        strings?.forEach { builder.append(key, it) }
    }
}