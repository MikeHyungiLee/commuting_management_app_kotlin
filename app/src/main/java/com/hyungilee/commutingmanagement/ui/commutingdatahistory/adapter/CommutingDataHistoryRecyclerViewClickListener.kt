package com.hyungilee.commutingmanagement.ui.commutingdatahistory.adapter

import android.view.View
import com.hyungilee.commutingmanagement.data.entity.CommutingData

interface CommutingDataHistoryRecyclerViewClickListener {

    fun onCommutingDataHistoryItemClick(view: View, commutingData: CommutingData)

}