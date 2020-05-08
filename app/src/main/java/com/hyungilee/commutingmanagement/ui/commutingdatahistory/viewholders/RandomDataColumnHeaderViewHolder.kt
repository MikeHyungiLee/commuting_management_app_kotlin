package com.hyungilee.commutingmanagement.ui.commutingdatahistory.viewholders

import android.view.View
import android.widget.TextView
import com.hyungilee.commutingmanagement.R
import ph.ingenuity.tableview.adapter.recyclerview.holder.AbstractViewHolder

class RandomDataColumnHeaderViewHolder(itemView: View) : AbstractViewHolder(itemView) {
    val cellTextView: TextView
        get() = itemView.findViewById(R.id.column_header_text)
}