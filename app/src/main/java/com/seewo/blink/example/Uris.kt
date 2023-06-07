package com.seewo.blink.example

object Uris {
    private const val BASE_URI = "blink://example"

    // Activity
    const val ACTIVITY = "$BASE_URI/activity"
    const val HOME_ACTIVITY = "$BASE_URI/home/activity"
    const val NEXT = "$BASE_URI/next"
    const val RETURN_RESULT = "$BASE_URI/return/result"
    const val NOT_EXISTS = "$BASE_URI/not_exists"

    // Fragment
    const val FRAGMENT = "$BASE_URI/fragment"
    const val NEXT_FRAGMENT = "$BASE_URI/next/fragment"
    const val RETURN_RESULT_FRAGMENT = "$BASE_URI/return/result/fragment"
    const val TEMP_FRAGMENT = "$BASE_URI/temp"
    const val FINAL_FRAGMENT = "$BASE_URI/final"
    const val SINGLE_TOP_FRAGMENT = "$BASE_URI/singleTop"
}