package com.hyungilee.commutingmanagement.ui.commutingtimehistory

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.hyungilee.commutingmanagement.R

class CommutingTimeHistoryFragment : Fragment() {

    companion object {
        fun newInstance() = CommutingTimeHistoryFragment()
    }

    private lateinit var viewModel: CommutingTimeHistoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.commuting_time_history_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CommutingTimeHistoryViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
