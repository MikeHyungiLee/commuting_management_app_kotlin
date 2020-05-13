package com.hyungilee.commutingmanagement.ui.commutingtimeregistration

import android.Manifest
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
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
import com.hyungilee.commutingmanagement.utils.Constants.DISTANCE_STANDARD
import kotlinx.android.synthetic.main.commuting_time_registration_fragment.*


class CommutingTimeRegistrationFragment :
    Fragment(), AdapterView.OnItemSelectedListener, View.OnClickListener,
    LocationListener{

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

    // 位置情報
    private lateinit var locationManager: LocationManager
    private var currentLocationLon: Double? = 0.0
    private var currentLocationLat: Double? = 0.0
    private var workLocationLon: Double? = 0.0
    private var workLocationLat: Double? = 0.0

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

        // GPS 権限設定
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1000)
        } else {
            locationStart()
            if (::locationManager.isInitialized) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000,
                    50f,
                    this)
            }

        }

        // 現場の位置情報初期化
        initWorkLocation()

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
            R.id.start_work_btn -> saveCommutingData()
//            val commutingData = CommutingData(3, "Mike", "5/5", "出勤", "07:00", "08:00")
//            mainViewModel.saveCommutingData(commutingData)
        }
    }

    private fun saveCommutingData(){
        val checkDistance = calDistance()

        if(checkDistance < DISTANCE_STANDARD){
            // 50メートル以内の位置で出勤ボタンを押した時の処理

        }else{
            // 50メートル以外の位置で出勤ボタンを押した時の処理
            Snackbar.make(requireView(), "勤務地から50メートル以内で出勤可能です。", 3000).show()
        }
//        val commutingData = CommutingData(3, "Mike", "5/5", "出勤", "07:00", "08:00")
//        viewModel.saveCommutingData()

    }

    override fun onLocationChanged(location: Location?) {
        // Latitude
        val lat = "Latitude:" + location?.latitude

        // Longitude
        val lon = "Longitude:" + location?.longitude

        currentLocationLat = location?.latitude
        currentLocationLon = location?.longitude



        Toast.makeText(requireContext(), "$lat, $lon", Toast.LENGTH_LONG).show()
        // 37 -122
    }


    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun onProviderEnabled(provider: String?) {
        TODO("Not yet implemented")
    }

    override fun onProviderDisabled(provider: String?) {
        TODO("Not yet implemented")
    }

    // GPS機能起動するメソッド
    private fun locationStart() {
        Log.d("debug", "locationStart()")

        // Instances of LocationManager class must be obtained using Context.getSystemService(Class)
        locationManager = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager

        val locationManager = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("debug", "location manager Enabled")
        } else {
            // to prompt setting up GPS
            val settingsIntent = Intent(ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(settingsIntent)
            Log.d("debug", "not gpsEnable, startActivity")
        }

        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)

            Log.d("debug", "checkSelfPermission false")
            return
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            1000,
            50f,
            this)
    }

    // 現場の位置情報初期化
    private fun initWorkLocation(){
        workLocationLat = 50.0
        workLocationLon = 100.0
    }

    // 距離計算メソッド
    private fun calDistance(): Float{
        //現在の位置情報
        val currentLoc = Location("Current_Location")
        currentLoc.latitude = currentLocationLat!!
        currentLoc.longitude = currentLocationLon!!

        //勤務地の位置情報
        val workLoc = Location("Work_Location")
        workLoc.latitude = workLocationLat!!
        workLoc.longitude = workLocationLon!!

        //現在位置から勤務地までの距離を返却
        return currentLoc.distanceTo(workLoc)
    }

}
