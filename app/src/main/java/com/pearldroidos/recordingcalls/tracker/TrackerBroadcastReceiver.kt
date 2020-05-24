package com.pearldroidos.recordingcalls.tracker

import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import com.pearldroidos.recordingcalls.data.CallTypeEnum
import com.pearldroidos.recordingcalls.data.DBManager
import com.pearldroidos.recordingcalls.utils.GlobalUtils
import java.util.*

class TrackerBroadcastReceiver : BroadcastReceiver() {
    private var phoneNumber: String? = null //because the passed incoming is only valid in ringing
    private var lastState = TelephonyManager.CALL_STATE_IDLE
    private var callStartTime: Date? = null
    private var isIncoming = false

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == TrackerService.ACTION_OUT) {
            phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER) ?: "-"
            callStartTime = Date()

            GlobalUtils.toast(
                context,
                "Out calling on phone number: $phoneNumber",
                false
            )
            Log.d("Recording Call", "Out going call: $phoneNumber  -  ${callStartTime.toString()}");
        } else {
            val stateStr = intent.extras?.getString(TelephonyManager.EXTRA_STATE) ?: ""
            val number =
                intent.extras?.getString(TelephonyManager.EXTRA_INCOMING_NUMBER) ?: phoneNumber
            var stateNum = 0
            when (stateStr) {
                TelephonyManager.EXTRA_STATE_IDLE -> {
                    stateNum = TelephonyManager.CALL_STATE_IDLE
                }
                TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                    stateNum = TelephonyManager.CALL_STATE_OFFHOOK
                }
                TelephonyManager.EXTRA_STATE_RINGING -> {
                    stateNum = TelephonyManager.CALL_STATE_RINGING
                }
            }

            onCallStateChanged(context = context, state = stateNum, number = number!!)

        }
    }

    //Deals with actual events

    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    private fun onCallStateChanged(context: Context, state: Int, number: String) {
        val dbManager = DBManager(context)
        val values = ContentValues()
        Log.d("RecordingCall", "State $state  -  last state $lastState")
        if (lastState == state) {
            //No change, debounce extras
            return
        }


        when (state) {
            TelephonyManager.CALL_STATE_RINGING -> {
                isIncoming = true
                callStartTime = Date()
                phoneNumber = number



                //onIncomingCallReceived(context, number, callStartTime)
                GlobalUtils.toast(
                    context,
                    "In calling on phone number: $number  -  ${callStartTime.toString()}",
                    false
                )

                Log.d("Recording Call", "In coming call $number  -  ${callStartTime.toString()}")
            }
            TelephonyManager.CALL_STATE_OFFHOOK ->                     //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false
                    callStartTime = Date()
                    GlobalUtils.toast(context, "State out going: During calling", false)
                    Log.d("Recording Call", "During Calling from OUT going call")
                    // startRecording()
                    // onOutgoingCallStarted(context, savedNumber, callStartTime)
                } else {
                    isIncoming = true
                    callStartTime = Date()
                    GlobalUtils.toast(context, "State in coming: During calling", false)
                    Log.d("Recording Call", "During Calling from IN coming call")
                    // startRecording()
                    // onIncomingCallAnswered(context, savedNumber, callStartTime)
                }
            TelephonyManager.CALL_STATE_IDLE ->                     //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    GlobalUtils.toast(
                        context,
                        "State Miss calling: Reject - Disconnect on phone number: $number  -  ${Date()}",
                        false
                    )

                    Log.d("Recording Call", "Miss calling: $number  -  ${Date()}");
                    //Ring but no pickup-  a miss
                    // onMissedCall(context, savedNumber, callStartTime)
                } else if (isIncoming) {

                    values.put("PhoneNumber", phoneNumber)
                    values.put("CallingType", CallTypeEnum.IN_COMING_CALL.name)
                    values.put("StartDate", callStartTime.toString())
                    values.put("EndDate", Date().toString())


                    dbManager.insert(values)

                    GlobalUtils.toast(
                        context,
                        "End In coming call on phone number: $number  -  ${Date()}",
                        false
                    )
                    Log.d("Recording Call", "End In coming call on phone number: $number  -  ${Date()}")

                    // stopRecording()
                    // onIncomingCallEnded(context, savedNumber, callStartTime, Date())
                } else {


                    values.put("PhoneNumber", phoneNumber)
                    values.put("CallingType", CallTypeEnum.OUT_GOING_CALL.name)
                    values.put("StartDate", callStartTime.toString())
                    values.put("EndDate", Date().toString())

                    dbManager.insert(values)
                    GlobalUtils.toast(
                        context,
                        "End OUT going call on phone number: $number  -  ${Date()} ",
                        false
                    )
                    Log.d("Recording Call", "End OUT going call: $number  -  ${Date()}")

                    //stopRecording()
                    //onOutgoingCallEnded(context, savedNumber, callStartTime, Date())
                }
        }
        lastState = state

    }

}