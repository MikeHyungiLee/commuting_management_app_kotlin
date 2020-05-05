package com.hyungilee.commutingmanagement.ui.commutingtimeregistration

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.hyungilee.commutingmanagement.R
import com.hyungilee.commutingmanagement.data.entity.CommutingData
import com.hyungilee.commutingmanagement.ui.main.MainViewModel

class CommutingTimeRegistrationFragment : Fragment() {

    companion object {
        fun newInstance() = CommutingTimeRegistrationFragment()
    }

    private lateinit var viewModel: CommutingTimeRegistrationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.commuting_time_registration_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CommutingTimeRegistrationViewModel::class.java)
        // TODO: Use the ViewModel


//        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
//        mainViewModel.getAllCommutingData().observe(this, Observer {
//            if(cntNum == 0) {
//                result_txt.text = "データアップデート準備"
//                cntNum++
//            }else{
//                result_txt.text = mainViewModel.getAllCommutingData().value.toString()
//            }
//        })
//
//        data_add_btn.setOnClickListener {
//            val commutingData = CommutingData(3, "Mike", "5/5", "出勤", "07:00", "08:00")
//            mainViewModel.saveCommutingData(commutingData)
//        }
    }

}
