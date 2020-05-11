package com.hyungilee.commutingmanagement.ui.salaryverification

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.hyungilee.commutingmanagement.R

class SalaryVerficationFragment : Fragment() {

    companion object {
        fun newInstance() = SalaryVerficationFragment()
    }

    private lateinit var viewModel: SalaryVerficationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.salary_verfication_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SalaryVerficationViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
