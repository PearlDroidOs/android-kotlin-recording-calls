package com.pearldroidos.recordingcalls.tracker

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent

class DeviceReceiver: DeviceAdminReceiver(){
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
    }
}