package com.seewo.blink.example.fragment

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.seewo.blink.annotation.BlinkUri
import com.seewo.blink.example.Uris
import com.seewo.blink.example.databinding.FragmentNextBinding
import com.seewo.blink.example.fragment.interceptor.RedirectInterceptor
import com.seewo.blink.example.ktx.toast
import com.seewo.blink.fragment.annotation.CustomAnimations
import com.seewo.blink.fragment.annotation.Orientation
import com.seewo.blink.fragment.annotation.SystemUI
import com.seewo.blink.fragment.attach
import com.seewo.blink.fragment.blink
import com.seewo.blink.fragment.detach

@BlinkUri(Uris.NEXT_FRAGMENT)
@Orientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
@SystemUI(brightnessLight = true, hideNavigationBar = true, hideStatusBar = true)
@CustomAnimations
class NextFragment: Fragment() {
    private val redirectInterceptor = RedirectInterceptor()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentNextBinding.inflate(inflater, container, false).apply {
        next.setOnClickListener {
            blink(Uris.NOT_EXISTS).exceptionOrNull()?.let {
                Log.e("BLINK", it.message, it)
                toast(it.message)
            }
        }
        home.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                redirectInterceptor.attach()
            } else {
                redirectInterceptor.detach()
            }
        }
    }.root
}