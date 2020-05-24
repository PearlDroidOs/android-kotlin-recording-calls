package com.pearldroidos.recordingcalls.data

import java.util.*

data class RecordingModel(
    val id:Int,
    val phoneNumber:String,
    val callingType:String,
    val startCall: Date,
    val endCall:Date
    )