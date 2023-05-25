package com.seewo.blink.utils

import android.net.Uri


private fun Uri.Builder.appendQueryParameter(key: String, value: Any) = appendQueryParameter(key, "$value")

/**
 * 为Uri设置参数时，建议使用以下append方法，而不是Uri.Builder自带的appendQueryParameter方法，主要做以下几点处理
 * 1. 可以正确的传入列表
 * 2. 避免null被转为"null"或""传入到参数中，导致参数失真
 * 3. 对于非String类型的一些常用数据类型，做了相关序列化处理
 * 4. 目前支持的类型：基本类型，String，枚举，以上几种类型的列表
 * 5. 复杂数据结构建议序列化为json后传入，或者转化成Intent后传入。
 * 6. 庞大的数据不建议通过路由参数传递给页面，如有必要，建议通过静态对象等来传递
 */
fun Uri.Builder.append(key: String, value: Any?): Uri.Builder {
    value ?: return this
    if (value is Collection<*>) {
        if (value.isEmpty()) {
            return appendQueryParameter(key, "")
        } else {
            value.forEach { item ->
                item ?: return@forEach
                appendQueryParameter(key, item)
            }
            return this
        }
    }
    return appendQueryParameter(key, "$value")
}

/**
 * 在lambda中对Uri进行修改或参数追加
 */
fun Uri.build(block: Uri.Builder.() -> Unit) = buildUpon().apply(block).build()

/**
 * 在lambda中对Uri进行修改或参数追加
 */
fun String.buildUri(block: Uri.Builder.() -> Unit) = Uri.parse(this).buildUpon().apply(block).build()

/**
 * 获取Uri的主Uri，对于带参的Uri，即为?前的部分
 */
val Uri.baseUri: String
    get() = "$scheme://$authority$path"

