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
import com.hyungilee.commutingmanagement.data.db.CommutingManagementDatabase
import com.hyungilee.commutingmanagement.data.repository.CommutingDatabaseRepository
import com.hyungilee.commutingmanagement.ui.commutingdatahistory.adapter.CommutingDataHistoryAdapter
import com.hyungilee.commutingmanagement.ui.commutingdatahistory.adapter.RandomDataFactory
import com.hyungilee.commutingmanagement.ui.commutingdatahistory.adapter.RandomDataTableViewAdapter
import kotlinx.android.synthetic.main.commuting_time_history_fragment.*
import ph.ingenuity.tableview.TableView

class CommutingDataHistoryFragment : Fragment() {

    companion object {
        fun newInstance() = CommutingDataHistoryFragment()
    }

    private lateinit var viewModel: CommutingDataHistoryViewModel
    private lateinit var commutingDataHistoryAdapter: CommutingDataHistoryAdapter

    private lateinit var tableView: TableView
    private var mainView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (mainView != null) {
            var parent = mainView!!.parent as ViewGroup?
            if (parent == null) {
                parent = container
            }
            parent!!.removeView(mainView)
            return mainView as View
        }
        mainView = inflater.inflate(R.layout.commuting_time_history_fragment, container, false)
        initializeViews()
        initializeData()
        return mainView as View
    }

    private fun initializeViews() {
        tableView = mainView!!.findViewById(R.id.random_data_tableview)
    }

    @Suppress("UNCHECKED_CAST")
    private fun initializeData() {
        // この部分でデータベースを初期化する

        val randomDataFactory = RandomDataFactory(500, 500)
        val tableAdapter = RandomDataTableViewAdapter(mainView!!.context)
        val cellsList = randomDataFactory.randomCellsList as List<List<Any>>
        val rowHeadersList = randomDataFactory.randomRowHeadersList as List<Any>
        val columnHeadersList = randomDataFactory.randomColumnHeadersList as List<Any>
        tableView.adapter = tableAdapter
        tableAdapter.setAllItems(cellsList, columnHeadersList, rowHeadersList)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val database = CommutingManagementDatabase.invoke(requireContext())
        val repository = CommutingDatabaseRepository(database)
        val viewModelFactory = CommutingDataHistoryViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CommutingDataHistoryViewModel::class.java)

        viewModel.getAllCommutingData().observe(viewLifecycleOwner, Observer { list ->
            commutingDataHistoryAdapter = CommutingDataHistoryAdapter()
            commutingDataHistoryAdapter.setCommutingDataList(list)
//            commuting_time_history_rv.also {rv->
//                rv.layoutManager = LinearLayoutManager(requireContext())
//                rv.adapter = commutingDataHistoryAdapter
//            }

        })

    }

}
