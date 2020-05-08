package com.hyungilee.commutingmanagement.ui.commutingdatahistory.viewholders

import android.view.View
import android.widget.TextView
import com.hyungilee.commutingmanagement.R
import ph.ingenuity.tableview.adapter.recyclerview.holder.AbstractViewHolder

class RandomDataRowHeaderViewHolder(itemView: View) : AbstractViewHolder(itemView) {
    val cellTextView: TextView
        get() = itemView.findViewById(R.id.row_header_text)
}