package com.seewo.blink.example.fragment

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.seewo.blink.annotation.BlinkUri
import com.seewo.blink.example.R
import com.seewo.blink.example.Uris
import com.seewo.blink.example.bean.Navigator
import com.seewo.blink.example.databinding.FragmentHomeBinding
import com.seewo.blink.example.fragment.interceptor.ExampleInterceptor
import com.seewo.blink.example.ktx.toast
import com.seewo.blink.fragment.annotation.KeepAlive
import com.seewo.blink.fragment.annotation.Orientation
import com.seewo.blink.fragment.annotation.SystemUI
import com.seewo.blink.fragment.attach
import com.seewo.blink.fragment.blink
import com.seewo.blink.fragment.detach
import com.seewo.blink.fragment.mode.SingleTaskFragment
import com.seewo.blink.utils.append
import com.seewo.blink.utils.buildUri

@BlinkUri(Uris.FRAGMENT)
@Orientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
@SystemUI(brightnessLight = false)
@KeepAlive
class HomeFragment: SingleTaskFragment() {
    private val interceptor = ExampleInterceptor()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentHomeBinding.inflate(
        inflater.cloneInContext(
            ContextThemeWrapper(requireContext(), R.style.Theme_Blink)),
        container,
        false
    ).apply {
        next.setOnClickListener {
            blink(Uris.NEXT_FRAGMENT).onFailure {
                Log.e("BLINK", it.message, it)
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }
        }
        interceptor.attach()
        deny.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                interceptor.attach()
            } else {
                interceptor.detach()
            }
        }
        nextWithParam.setOnClickListener {
            blink(Uris.RETURN_RESULT_FRAGMENT.buildUri {
                append("navigator", Navigator.BLINK)
            }) {
                if (it != null) {
                    toast("返回结果: $it")
                } else {
                    toast("返回结果: 无")
                }
            }
        }
        goToTemp.setOnClickListener {
            blink(Uris.TEMP_FRAGMENT)
        }
        singleTop.setOnClickListener {
            blink(Uris.SINGLE_TOP_FRAGMENT)
        }
    }.root.apply { setBackgroundResource(R.color.purple_500) }

    override fun onNewArguments(arguments: Bundle?) {
        toast("onNewArguments $arguments")
        Log.i("BLINK", "onNewArguments $arguments")
    }

    override fun onDestroy() {
        super.onDestroy()
        interceptor.detach()
    }
}