package com.seewo.blink.fragment

import com.seewo.blink.fragment.container.NullFragment

class FragmentNotFoundException(fragment: NullFragment) : RuntimeException(
    "Cannot find fragment with uri: ${
        fragment.arguments?.getString(
            RouteMap.KEY_URI
        )
    }"
) {
    val uri = fragment.arguments?.getString(RouteMap.KEY_URI)
}