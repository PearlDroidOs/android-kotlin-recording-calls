package com.pearldroidos.recordingcalls

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pearldroidos.recordingcalls.data.RecordingModel
import kotlinx.android.synthetic.main.recording_item.view.*
import java.text.SimpleDateFormat

class MainAdapter(private val listOfRecord: List<RecordingModel>, context: Context) :
    RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    //Immutable variable of layoutInflater
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var phoneNumber = itemView.tvPhoneNum
        var callingType = itemView.tvCallingType
        var date = itemView.tvDate
        var duration = itemView.tvDuration
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = layoutInflater.inflate(R.layout.recording_item, parent, false)
        return MainViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listOfRecord.size
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val format = SimpleDateFormat("dd/MM/yyy")
        val formatted = format.format(listOfRecord[position].startCall)

        //Calculate Duration
        val startDate = listOfRecord[position].startCall
        val endDate = listOfRecord[position].endCall
        val diff: Long = endDate.time - startDate.time

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        val secStr = if (seconds < 10) "0$seconds" else "$seconds"
        val minStr = if (minutes < 10) "0$minutes" else "$minutes"
        val hourStr = if (hours < 10) "0$hours" else "$hours"

        holder.phoneNumber.text = listOfRecord[position].phoneNumber
        holder.callingType.text = listOfRecord[position].callingType
        holder.date.text = "Date:  $formatted"
        holder.duration.text = "Duration:  $hourStr:$minStr:$secStr"
    }
}