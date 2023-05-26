package com.seewo.blink.example.activity

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.seewo.blink.annotation.BlinkUri
import com.seewo.blink.attach
import com.seewo.blink.blink
import com.seewo.blink.createBlinkIntent
import com.seewo.blink.detach
import com.seewo.blink.example.Uris
import com.seewo.blink.example.activity.interceptor.RedirectInterceptor
import com.seewo.blink.example.databinding.ActivityNextBinding
import com.seewo.blink.example.ktx.toast

@BlinkUri(Uris.NEXT)
class NextActivity : AppCompatActivity() {
    private val redirectInterceptor = RedirectInterceptor()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNextBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.next.setOnClickListener {
            blink(Uris.NOT_EXISTS).exceptionOrNull()?.let {
                Log.e("BLINK", it.message, it)
                toast(it.message)
            }
            Log.e("BLINK", "resolve ${packageManager.resolveActivity(Uris.NOT_EXISTS.createBlinkIntent(), PackageManager.MATCH_DEFAULT_ONLY)}")
        }
        binding.home.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                redirectInterceptor.attach()
            } else {
                redirectInterceptor.detach()
            }
        }
    }
}