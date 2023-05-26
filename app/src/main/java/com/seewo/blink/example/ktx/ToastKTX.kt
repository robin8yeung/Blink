package com.seewo.blink.example.ktx

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment

fun Context.toast(msg: String?) {
    msg ?: return
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(msg: String?) {
    msg ?: return
    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}