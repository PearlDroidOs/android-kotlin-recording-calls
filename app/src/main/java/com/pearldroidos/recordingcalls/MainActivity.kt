package com.pearldroidos.recordingcalls

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.pearldroidos.recordingcalls.data.DBManager
import com.pearldroidos.recordingcalls.data.RecordingModel
import com.pearldroidos.recordingcalls.tracker.DeviceReceiver
import com.pearldroidos.recordingcalls.tracker.TrackerService
import com.pearldroidos.recordingcalls.utils.GlobalUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener {
    private val REQUEST_CODE = 0
    private var mDPM: DevicePolicyManager? = null
    private var mAdminName: ComponentName? = null
    private var listOfRecord: ArrayList<RecordingModel> = ArrayList()
    private var intentService: Intent? = null
    private lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        sp = getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE)
        getService()

        swiperefresh.setOnRefreshListener {
            Handler().postDelayed(Runnable {
                swiperefresh.isRefreshing = false
                load("%")
            }, 900)

        }

        swTrack.setOnCheckedChangeListener(this)
        swTrack.setOnClickListener {
                getService()
        }
        swTrack.isChecked = isTrackerStatus()

    }

    private fun checkMessageView(){
        if(listOfRecord.size >0){
            tvMsg.visibility = View.GONE
        }else{
            tvMsg.visibility = View.VISIBLE
        }
    }

    private fun getService() {
        if (isTrackerStatus()) {
            Log.d("Precord","service")
            try {
                // Initiate DevicePolicyManager.
                mDPM = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
                mAdminName = ComponentName(this, DeviceReceiver::class.java)
                if (!mDPM!!.isAdminActive(mAdminName!!)) {
                    intentService = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                    intentService!!.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName)

                    GlobalUtils.toast(this, "Start Service", false)
                    startActivityForResult(intentService, REQUEST_CODE)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            GlobalUtils.toast(this, "Stop Service", false)
            if(intentService != null) {
                stopService(intentService)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("Record", "onResume")
        swTrack.isChecked = isTrackerStatus()
        load("%")
        checkMessageView()
    }

    override fun onPause() {
        super.onPause()
        Log.d("Record", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Record", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(intentService)
    }

    private fun load(numbers: String) {
        val dbManager = DBManager(this)
        val projection = arrayOf("ID", "PhoneNumber", "CallingType", "StartDate", "EndDate")
        val selectionArgs = arrayOf(numbers)
        val cursor = dbManager.query(projection, "PhoneNumber LIKE ?", selectionArgs, "PhoneNumber")
        listOfRecord.clear()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("ID"))
                val phoneNumber = cursor.getString(cursor.getColumnIndex("PhoneNumber"))
                val callingType = cursor.getString(cursor.getColumnIndex("CallingType"))
                val startCall = cursor.getString(cursor.getColumnIndex("StartDate"))
                val endCall = cursor.getString(cursor.getColumnIndex("EndDate"))

                listOfRecord.add(
                    RecordingModel(
                        id,
                        phoneNumber,
                        callingType,
                        Date(startCall),
                        Date(endCall)
                    )
                )
            } while (cursor.moveToNext())
        }

        initRecordList()
    }

    private fun initRecordList() {
        val layoutMG = LinearLayoutManager(this)
        val adapter = MainAdapter(listOfRecord, this)
        rvDessert.layoutManager = layoutMG
        rvDessert.adapter = adapter
    }

    private fun saveTrackerStatus(isTrack: Boolean) {
        val editor = sp.edit()
        editor.putBoolean("track", isTrack);
        editor.apply();
    }

    private fun isTrackerStatus(): Boolean {
        return sp.getBoolean("track", false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (REQUEST_CODE == requestCode) {
            intentService = Intent(this, TrackerService::class.java)
            startService(intentService)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if(buttonView!!.id == R.id.swTrack){
            saveTrackerStatus(isChecked)
        }
    }
}
