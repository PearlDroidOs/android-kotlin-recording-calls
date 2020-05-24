package com.pearldroidos.recordingcalls.tracker

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log

class TrackerService: Service() {
    private lateinit var trackerBR:TrackerBroadcastReceiver

    companion object{
        val ACTION_IN = "android.intent.action.PHONE_STATE"
        val ACTION_OUT = "android.intent.action.NEW_OUTGOING_CALL"
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d("Recording Call", "Service Bind");
        return null
    }

    override fun onDestroy() {
        Log.d("Recording Call", "Service Destroy");
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val filter = IntentFilter()
        filter.addAction(ACTION_OUT)
        filter.addAction(ACTION_IN)
        trackerBR = TrackerBroadcastReceiver()
        this.registerReceiver(trackerBR, filter)
        return super.onStartCommand(intent, flags, startId)
    }

}