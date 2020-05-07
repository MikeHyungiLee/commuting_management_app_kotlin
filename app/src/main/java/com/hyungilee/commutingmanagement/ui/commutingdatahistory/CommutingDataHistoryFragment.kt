package com.hyungilee.commutingmanagement.ui.commutingdatahistory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

import com.hyungilee.commutingmanagement.R
import com.hyungilee.commutingmanagement.ui.commutingdatahistory.adapter.CommutingDataHistoryAdapter
import kotlinx.android.synthetic.main.commuting_time_history_fragment.*

class CommutingDataHistoryFragment : Fragment() {

    companion object {
        fun newInstance() = CommutingDataHistoryFragment()
    }

    private lateinit var viewModel: CommutingDataHistoryViewModel
    private lateinit var commutingDataHistoryAdapter: CommutingDataHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.commuting_time_history_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewModelFactory = CommutingDataHistoryViewModelFactory(activity?.application!!)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CommutingDataHistoryViewModel::class.java)

        viewModel.getAllCommutingData().observe(viewLifecycleOwner, Observer { list ->
            commutingDataHistoryAdapter = CommutingDataHistoryAdapter()
            commutingDataHistoryAdapter.setCommutingDataList(list)
            commuting_time_history_rv.also {rv->
                rv.layoutManager = LinearLayoutManager(requireContext())
                rv.adapter = commutingDataHistoryAdapter
            }
        })
    }

}
