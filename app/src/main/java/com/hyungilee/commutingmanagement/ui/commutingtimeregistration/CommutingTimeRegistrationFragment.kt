package com.hyungilee.commutingmanagement.ui.commutingtimeregistration

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider

import com.hyungilee.commutingmanagement.R
import com.hyungilee.commutingmanagement.data.db.CommutingManagementDatabase
import com.hyungilee.commutingmanagement.data.entity.CommutingData
import com.hyungilee.commutingmanagement.data.repository.CommutingDatabaseRepository
import kotlinx.android.synthetic.main.commuting_time_registration_fragment.*

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
        val database = CommutingManagementDatabase(requireContext())
        val repository = CommutingDatabaseRepository(database)
        val viewModelFactory = CommutingTimeRegistrationViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CommutingTimeRegistrationViewModel::class.java)

//        start_btn.setOnClickListener {
//              val commutingData = CommutingData(null, "S", "5/5", "出勤", "07:00", "00:00")
//              viewModel.saveCommutingData(commutingData)
//        }
    }

}
