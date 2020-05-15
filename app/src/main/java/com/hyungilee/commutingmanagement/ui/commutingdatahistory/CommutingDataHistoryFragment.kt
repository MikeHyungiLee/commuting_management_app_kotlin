package com.hyungilee.commutingmanagement.ui.commutingdatahistory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

import com.hyungilee.commutingmanagement.R
import com.hyungilee.commutingmanagement.data.db.CommutingManagementDatabase
import com.hyungilee.commutingmanagement.data.entity.CommutingData
import com.hyungilee.commutingmanagement.data.repository.CommutingDatabaseRepository
import com.hyungilee.commutingmanagement.ui.commutingdatahistory.adapter.CommutingDataHistoryBindAdapter
import com.hyungilee.commutingmanagement.ui.commutingdatahistory.adapter.CommutingDataHistoryRecyclerViewClickListener
import kotlinx.android.synthetic.main.commuting_time_history_fragment.*

@Suppress("UNCHECKED_CAST")
class CommutingDataHistoryFragment : Fragment(), CommutingDataHistoryRecyclerViewClickListener {

    companion object {
        fun newInstance() = CommutingDataHistoryFragment()
    }

    private lateinit var viewModel: CommutingDataHistoryViewModel
   private var mainView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val database = CommutingManagementDatabase.invoke(requireContext())
        val repository = CommutingDatabaseRepository(database)
        val viewModelFactory = CommutingDataHistoryViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CommutingDataHistoryViewModel::class.java)

        if (mainView != null) {
            var parent = mainView!!.parent as ViewGroup?
            if (parent == null) {
                parent = container
            }
            parent!!.removeView(mainView)
            return mainView as View
        }
        mainView = inflater.inflate(R.layout.commuting_time_history_fragment, container, false)

        return mainView as View
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        viewModel.getAllCommutingData().observe(viewLifecycleOwner, Observer { list ->

            commuting_time_history_rv.also {rv->
                rv.layoutManager = LinearLayoutManager(requireContext())
                rv.adapter = CommutingDataHistoryBindAdapter(list, this)
            }
        })

//        viewModel.getAllCommutingData().observe(viewLifecycleOwner, Observer { list ->
//            Toast.makeText(requireContext(), "CheckListVal: $list", Toast.LENGTH_LONG).show()
//        })


//        var[0]id: Int?,
//        @ColumnInfo(name = "username")
//        var [1]user_name: String,
//        @ColumnInfo(name = "category")
//        var [2]category: String,
//        @ColumnInfo(name = "location_lat")
//        var [3]location_lat: String,
//        @ColumnInfo(name = "location_lon")
//        var [4]location_lon: String,
//        @ColumnInfo(name = "date")
//        var [5]date: String,
//        @ColumnInfo(name = "startTime")
//        var [6]start_time: String,
//        @ColumnInfo(name = "leaveTime")
//        var [7]leave_time: String,
//        @ColumnInfo(name = "restTime")
//        var [8]rest_time: String,
//        @ColumnInfo(name = "workHours")
//        var [9]work_hours: String,
//        @ColumnInfo(name = "overTimeHours")
//        var [10]over_time: String,
//        @ColumnInfo(name = "midnightWorkHour")
//        var [11]midnight_work_hours: String,
//        @ColumnInfo(name = "holidayWorkHour")
//        var [12]holiday_work_hours: String


//        (1 until 32).forEach {row->
//            viewModel.getCommutingRowData(row).observe(viewLifecycleOwner, Observer { list ->
//                val randomCellsList = mutableListOf<Any>()
//                if(list != null) {
//                    val cellList = mutableListOf<Any>()
//                    (1 .. 11).forEach { column ->
//                        var data: Any = ""
//                        when(column){
//                            1->{data = list.category}
//                            2->{data = list.location_lat}
//                            3->{data = list.location_lon}
//                            4->{data = list.date}
//                            5->{data = list.start_time}
//                            6->{data = list.leave_time}
//                            7->{data = list.rest_time}
//                            8->{data = list.work_hours}
//                            9->{data = list.over_time}
//                            10->{data = list.midnight_work_hours}
//                            11->{data = list.holiday_work_hours}
//                        }
//                        cellList.add(RandomDataCell(data, "$column,$row"))
////                        Toast.makeText(requireContext(), data.toString(), Toast.LENGTH_LONG).show()
//                    }
//                    randomCellsList.add(cellList)
//                }
//                //Toast.makeText(requireContext(), randomCellsList.toString(), Toast.LENGTH_LONG).show()
////                initializeData(randomCellsList as List<List<Any>>)
//            })
//
//        }


    }

    override fun onCommutingDataHistoryItemClick(view: View, commutingData: CommutingData) {
        when(view.id){
            R.id.detail_button -> detailButton(commutingData)
        }
    }

    private fun detailButton(commutingData: CommutingData){
        Toast.makeText(requireContext(), "Show me the detail: ${commutingData.start_time}", Toast.LENGTH_LONG).show()
    }
}
