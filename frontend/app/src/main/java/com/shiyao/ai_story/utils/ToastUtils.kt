package com.shiyao.ai_story.utils

import android.content.Context
import android.widget.Toast

object ToastUtils {

    fun showShort(context: Context, message: String?) {
        message ?: return
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showLong(context: Context, message: String?) {
        message ?: return
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}