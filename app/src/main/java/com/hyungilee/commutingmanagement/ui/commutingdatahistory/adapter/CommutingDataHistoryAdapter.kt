package com.hyungilee.commutingmanagement.ui.commutingdatahistory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hyungilee.commutingmanagement.R
import com.hyungilee.commutingmanagement.data.entity.CommutingData
import kotlinx.android.synthetic.main.commuting_time_history_list_item.view.*

class CommutingDataHistoryAdapter: RecyclerView.Adapter<CommutingDataHistoryAdapter.ViewHolder>() {

    private var commutingDataList: List<CommutingData> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.commuting_time_history_list_item, parent, false)

       return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return this.commutingDataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(commutingDataList[position])
    }

    fun setCommutingDataList(commutingDataList: List<CommutingData>){
        this.commutingDataList = commutingDataList
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(commutingData: CommutingData){
            itemView.user_id.text = commutingData.id.toString()
            itemView.user_name.text = commutingData.user_name
            itemView.date.text = commutingData.date
            itemView.start_time.text = commutingData.start_time
            itemView.end_time.text = commutingData.leave_time
        }
    }
}