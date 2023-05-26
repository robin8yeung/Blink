package com.seewo.blink.example.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.seewo.blink.annotation.BlinkUri
import com.seewo.blink.attach
import com.seewo.blink.blink
import com.seewo.blink.detach
import com.seewo.blink.example.Uris
import com.seewo.blink.example.activity.interceptor.ExampleInterceptor
import com.seewo.blink.example.bean.Navigator
import com.seewo.blink.example.databinding.ActivityHomeBinding
import com.seewo.blink.example.ktx.toast
import com.seewo.blink.utils.append
import com.seewo.blink.utils.buildUri

@BlinkUri(value = [Uris.HOME_ACTIVITY, Uris.ACTIVITY])
class HomeActivity : AppCompatActivity() {
    private val interceptor = ExampleInterceptor().apply {
        attach()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.next.setOnClickListener {
            blink(Uris.NEXT).onFailure {
                Log.e("BLINK", it.message, it)
                toast(it.message)
            }
        }
        binding.deny.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                interceptor.attach()
            } else {
                interceptor.detach()
            }
        }

        binding.nextWithParam.setOnClickListener {
            blink(Uris.RETURN_RESULT.buildUri {
                append("navigator", Navigator.BLINK)
            }) {
                if (it.resultCode == Activity.RESULT_OK) {
                    toast("返回结果: ${it.data?.getStringExtra("result")}")
                } else {
                    toast("返回结果: 无")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        interceptor.detach()
    }
}