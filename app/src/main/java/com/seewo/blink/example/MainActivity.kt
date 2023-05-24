package com.seewo.blink.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.seewo.blink.attach
import com.seewo.blink.blink
import com.seewo.blink.detach
import com.seewo.blink.example.databinding.ActivityMainBinding
import com.seewo.blink.example.fragment.RouteMetadata
import com.seewo.blink.example.interceptor.LoggerInterceptor

class MainActivity : AppCompatActivity() {
    private val loggerInterceptor = LoggerInterceptor()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RouteMetadata().inject()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loggerInterceptor.attach()
        binding.button.setOnClickListener {
//            FragmentContainerActivity.start(it.context)
            blink("blink://navigator/example?a=1")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        loggerInterceptor.detach()
    }
}