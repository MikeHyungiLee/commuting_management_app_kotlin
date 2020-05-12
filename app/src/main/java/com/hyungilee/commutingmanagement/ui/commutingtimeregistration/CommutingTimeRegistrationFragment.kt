package com.hyungilee.commutingmanagement.ui.commutingtimeregistration

import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

import com.hyungilee.commutingmanagement.R
import com.hyungilee.commutingmanagement.data.db.CommutingManagementDatabase
import com.hyungilee.commutingmanagement.data.entity.CommutingData
import com.hyungilee.commutingmanagement.data.repository.CommutingDatabaseRepository
import com.hyungilee.commutingmanagement.ui.setting.models.CommuteData
import com.hyungilee.commutingmanagement.ui.setting.models.User
import kotlinx.android.synthetic.main.commuting_time_registration_fragment.*

class CommutingTimeRegistrationFragment : Fragment(), AdapterView.OnItemSelectedListener, View.OnClickListener {

    companion object {
        fun newInstance() = CommutingTimeRegistrationFragment()
    }

    private lateinit var viewModel: CommutingTimeRegistrationViewModel

    private lateinit var spinner: Spinner

    private var user: FirebaseUser?= null
    // inactive screen layout
    private lateinit var inactiveLayout: LinearLayout
    // Firebase fire store and auth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    // Checkbox
    private lateinit var checkbox1: CheckBox
    private lateinit var checkbox2: CheckBox

    // UI要素
    private lateinit var position: TextView
    private lateinit var name: TextView
    private lateinit var nameKatakana: TextView
    private lateinit var emailAddr: TextView
    private lateinit var numOfPaidVacationDays: TextView
    private lateinit var numOfWorkingDays: TextView
    private lateinit var numOfLatenessAndEarlyLeavingDays: TextView
    private lateinit var numOfAbsenceDays: TextView
    private lateinit var actualWorkingHours: TextView
    private lateinit var numOfLatenessAndEarlyLeavingHours: TextView
    // Button layout
    private lateinit var commutingBtnLayout: LinearLayout
    private lateinit var firstOptionLayout: LinearLayout
    // 出勤・退勤ボタン
    private lateinit var startWorkBtn: Button
    private lateinit var leaveWorkBtn: Button

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

        //Firebase AuthとFirestoreの初期化
        // Firebase auth　初期化
        auth = Firebase.auth
        if(auth.currentUser!=null){
            user = auth.currentUser as FirebaseUser
        }
        // Firestore初期化
        firestore = Firebase.firestore

        // UI要素初期化
        initUIelements(view)
        // Firestoreから取得したデータを使ってUI要素の値を初期化する
        initUIvalue()

        // 打刻チェック画面であるチェックボックスのイベント処理メソッド
        checkbox1Event()
        checkbox2Event()

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val database = CommutingManagementDatabase(requireContext())
        val repository = CommutingDatabaseRepository(database)
        val viewModelFactory = CommutingTimeRegistrationViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CommutingTimeRegistrationViewModel::class.java)
    }

    // Spinnerのアイテムクリックイベントメソット
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    private fun initUIvalue(){
        if(user !== null) {
            val userRef = firestore.collection("users").document(user!!.email!!)
            userRef.get().addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject<User>()
                if (user != null) {
                    position.text = user.position
                    name.text = user.full_name
                    nameKatakana.text = user.katakana
                    emailAddr.text = user.email
                }
            }

            val comRef = firestore.collection("commuting_data").document(user!!.email!!)
            comRef.get().addOnSuccessListener { documentSnapshot ->
                val commuteData = documentSnapshot.toObject<CommuteData>()
                if (commuteData != null) {
                numOfPaidVacationDays.text = "${commuteData.num_of_used_paid_vacation_days}/${commuteData.num_of_paid_vacation_days.toString()}"+"日"
                numOfWorkingDays.text = "${commuteData.actual_working_days}/${commuteData.num_of_working_days.toString()}"+"日"
                val latPlusEarlyLeavingDays = commuteData.num_of_lateness_days!!+commuteData.num_of_early_leaving_days!!
                numOfLatenessAndEarlyLeavingDays.text = latPlusEarlyLeavingDays.toString()+"日"
                numOfAbsenceDays.text = commuteData.num_of_absence_days.toString()+"日"
                actualWorkingHours.text = commuteData.num_of_actual_lateness_hours.toString()+"時間"
                val latPlusEarlyLeavingHours = commuteData.num_of_actual_lateness_hours!!+commuteData.num_of_early_leaving_hours!!
                numOfLatenessAndEarlyLeavingHours.text = latPlusEarlyLeavingHours.toString()+"時間"
                }
            }
        }
    }

    private fun initUIelements(view: View){
        position = view.findViewById(R.id.position)
        name = view.findViewById(R.id.name)
        nameKatakana = view.findViewById(R.id.name_katakana)
        emailAddr = view.findViewById(R.id.emailAddr)
        numOfPaidVacationDays = view.findViewById(R.id.num_of_paid_vacation_days)
        numOfWorkingDays = view.findViewById(R.id.num_of_working_days)
        numOfLatenessAndEarlyLeavingDays = view.findViewById(R.id.num_of_lateness_and_early_leaving_days)
        numOfAbsenceDays = view.findViewById(R.id.num_of_absence_days)
        actualWorkingHours = view.findViewById(R.id.actual_working_hours)
        numOfLatenessAndEarlyLeavingHours = view.findViewById(R.id.num_of_lateness_and_early_leaving_hours)
        checkbox1 = view.findViewById(R.id.checkbox_option_1)
        checkbox2 = view.findViewById(R.id.checkbox_option_2)
        // button layout
        commutingBtnLayout = view.findViewById(R.id.commuting_button_layout)
        firstOptionLayout = view.findViewById(R.id.first_option)
        // 出勤・退勤ボタンの初期化
        startWorkBtn = view.findViewById(R.id.start_work_btn)
        leaveWorkBtn = view.findViewById(R.id.leave_work_btn)
    }

    private fun checkbox1Event(){
        checkbox1.setOnCheckedChangeListener { _, isChecked ->
            if(checkbox2.isChecked){
                Snackbar.make(requireView(),"一つのオプションだけ選択してください。", 3000).show()
                firstOptionLayout.visibility = View.GONE
                commutingBtnLayout.visibility = View.VISIBLE
            }else{
                if(isChecked) {
                    firstOptionLayout.visibility = View.VISIBLE
                    commutingBtnLayout.visibility = View.GONE
                }else{
                    firstOptionLayout.visibility = View.GONE
                    commutingBtnLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun checkbox2Event(){
        checkbox2.setOnCheckedChangeListener { _, isChecked ->
            if(checkbox1.isChecked){
                Snackbar.make(requireView(),"一つのオプションだけ選択してください。", 3000).show()
                firstOptionLayout.visibility = View.VISIBLE
                commutingBtnLayout.visibility = View.GONE
            }else{
                if(isChecked) {
                    commutingBtnLayout.visibility = View.VISIBLE
                    firstOptionLayout.visibility = View.GONE
                }else{
                    firstOptionLayout.visibility = View.GONE
                    commutingBtnLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onClick(v: View) {
        val item_id = v.id
        when(item_id){
//            R.id.start_work_btn ->
//            val commutingData = CommutingData(3, "Mike", "5/5", "出勤", "07:00", "08:00")
//            mainViewModel.saveCommutingData(commutingData)
        }
    }

    fun saveCommutingData(){
//        val commutingData = CommutingData(3, "Mike", "5/5", "出勤", "07:00", "08:00")
//        viewModel.saveCommutingData()

    }

}
