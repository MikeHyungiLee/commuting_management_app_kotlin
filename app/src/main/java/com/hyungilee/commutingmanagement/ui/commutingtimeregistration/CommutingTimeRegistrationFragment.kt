package com.hyungilee.commutingmanagement.ui.commutingtimeregistration

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

import com.hyungilee.commutingmanagement.R
import com.hyungilee.commutingmanagement.data.db.CommutingManagementDatabase
import com.hyungilee.commutingmanagement.data.entity.CommutingData
import com.hyungilee.commutingmanagement.data.repository.CommutingDatabaseRepository
import kotlinx.android.synthetic.main.commuting_time_registration_fragment.*

class CommutingTimeRegistrationFragment : Fragment(), AdapterView.OnItemSelectedListener {

    companion object {
        fun newInstance() = CommutingTimeRegistrationFragment()
    }

    private lateinit var viewModel: CommutingTimeRegistrationViewModel

    private lateinit var spinner: Spinner

    private var user: FirebaseUser?= null
    // inactive screen layout
    private lateinit var inactiveLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.commuting_time_registration_fragment, container, false)
        // Spinnerを初期化
        spinner = view.findViewById(R.id.option1_spinner)
        // Spinnerのアイテムクリックリスナー
        spinner.onItemSelectedListener = this
        // Spinnerに表示するリストをString arrayから取得して初期化する
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.commuting_option_array,
            android.R.layout.simple_spinner_item
        ).also {adapter->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        user = Firebase.auth.currentUser
        //inactiveレイアウトの初期化
        inactiveLayout = view.findViewById(R.id.commuting_check_inactive_screen)

        if(user?.email != null){
            inactiveLayout.visibility = View.GONE
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val database = CommutingManagementDatabase(requireContext())
        val repository = CommutingDatabaseRepository(database)
        val viewModelFactory = CommutingTimeRegistrationViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CommutingTimeRegistrationViewModel::class.java)

//        val ref = FirebaseDatabase.getInstance().getReference("commuting_database")
//        // unique id
//        val userId = ref.push().key
//        // new commuting data
//        val commutingData = CommutingData(userId!!, "S", "5/5", "出勤", "07:00", "00:00")
//
//        ref.child(userId).setValue(commutingData).addOnCompleteListener {
//            Toast.makeText(context, "Commuting data saved successfully", Toast.LENGTH_LONG).show()
//        }


    }

    // Spinnerのアイテムクリックイベントメソット
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

}
