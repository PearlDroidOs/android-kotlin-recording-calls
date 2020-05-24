package com.pearldroidos.recordingcalls.utils

import android.content.Context
import android.widget.Toast

class GlobalUtils {
    companion object{
        fun toast(context: Context, msg: String, isLong:Boolean = true){
            val duration = if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
            Toast.makeText(context, msg, duration).show()
        }
    }
}