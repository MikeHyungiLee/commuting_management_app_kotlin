package com.hyungilee.commutingmanagement.ui.commutingdatahistory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.hyungilee.commutingmanagement.R
import com.hyungilee.commutingmanagement.data.entity.CommutingData
import com.hyungilee.commutingmanagement.databinding.RecyclerviewCommutingDataBinding
import kotlinx.android.synthetic.main.commuting_time_history_list_item.view.*

class CommutingDataHistoryBindAdapter(
      private val commutingData: List<CommutingData>,
      private val listener: CommutingDataHistoryRecyclerViewClickListener
    ):RecyclerView.Adapter<CommutingDataHistoryBindAdapter.CommutingDataViewHolder>() {

    override fun getItemCount(): Int = commutingData.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CommutingDataViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.recyclerview_commuting_data,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: CommutingDataViewHolder, position: Int) {
        holder.recyclerviewCommutingDataBinding.comdata = commutingData[position]
        holder.recyclerviewCommutingDataBinding.detailButton.setOnClickListener {
            listener.onCommutingDataHistoryItemClick(holder.recyclerviewCommutingDataBinding.detailButton, commutingData[position])
        }
    }

    inner class CommutingDataViewHolder(
        val recyclerviewCommutingDataBinding: RecyclerviewCommutingDataBinding
    ): RecyclerView.ViewHolder(recyclerviewCommutingDataBinding.root)



}