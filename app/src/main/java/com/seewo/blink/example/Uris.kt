package com.seewo.blink.example

import android.net.Uri

object Uris {
    private const val BASE_URI = "blink://navigator"
    val EXAMPLE = Uri.parse("$BASE_URI/example")!!
    val EXAMPLE_2 = Uri.parse("$BASE_URI/example/2")!!
    val NOT_EXIST = Uri.parse("$BASE_URI/example/3")!!
}