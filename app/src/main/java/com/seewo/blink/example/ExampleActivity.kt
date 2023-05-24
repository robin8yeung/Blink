package com.seewo.blink.example

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.seewo.blink.annotation.BlinkUri
import com.seewo.blink.attach
import com.seewo.blink.blink
import com.seewo.blink.detach
import com.seewo.blink.example.databinding.ActivityExampleBinding
import com.seewo.blink.example.interceptor.ExampleInterceptor

@BlinkUri("blink://navigator/example")
class ExampleActivity : AppCompatActivity() {
    private val name: String? by lazy { intent.data?.getQueryParameter("name") }
    private val interceptor = ExampleInterceptor()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        interceptor.attach()
        val binding = ActivityExampleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.hello.text = "Hello $name"
        binding.next.setOnClickListener {
            blink(Uris.EXAMPLE_2) {
                if (it.resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "Return result: ${it.data}", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Return result: Cancel", Toast.LENGTH_LONG).show()
                }
            }.exceptionOrNull()?.let {
                Log.e("BLINK", it.message, it)
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
        }
        binding.deny.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                interceptor.attach()
            } else {
                interceptor.detach()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        interceptor.detach()
    }
}