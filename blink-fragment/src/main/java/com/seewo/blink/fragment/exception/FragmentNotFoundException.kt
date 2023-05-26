package com.seewo.blink.fragment.exception

import android.net.Uri
import com.seewo.blink.fragment.RouteMap
import com.seewo.blink.fragment.container.NullFragment
import com.seewo.blink.fragment.uriOrNull

class FragmentNotFoundException(fragment: NullFragment) : RuntimeException(
    "Cannot find fragment with uri: ${
        fragment.arguments?.getParcelable<Uri>(
            RouteMap.KEY_URI
        )
    }"
) {
    val uri = fragment.arguments?.uriOrNull
}