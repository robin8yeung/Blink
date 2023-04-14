package com.seewo.blink.example

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.seewo.blink.attach
import com.seewo.blink.blink
import com.seewo.blink.detach
import com.seewo.blink.example.databinding.ActivityExampleBinding
import com.seewo.blink.example.databinding.ActivityMainBinding
import com.seewo.blink.example.interceptor.LoggerInterceptor

class MainActivity : AppCompatActivity() {
    private val loggerInterceptor = LoggerInterceptor()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loggerInterceptor.attach()
        binding.button.setOnClickListener {
            blink(Uris.EXAMPLE.buildUpon().appendQueryParameter("name", "Blink").build())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        loggerInterceptor.detach()
    }
}