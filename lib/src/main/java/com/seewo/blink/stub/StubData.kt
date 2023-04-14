package com.seewo.blink.stub

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.core.app.ActivityOptionsCompat

class StubData(
    val intent: Intent,
    val options: ActivityOptionsCompat?,
    val callback: ActivityResultCallback<ActivityResult>
)