package com.hyungilee.commutingmanagement.ui.commutingtimeregistration

import android.Manifest
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
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
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
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
import com.hyungilee.commutingmanagement.utils.Constants
import com.hyungilee.commutingmanagement.utils.Constants.CURRENT_DATE
import com.hyungilee.commutingmanagement.utils.Constants.START_WORK_BTN_TAG
import com.hyungilee.commutingmanagement.utils.CurrentDateTime
import com.hyungilee.commutingmanagement.utils.GPSLocation
import com.hyungilee.commutingmanagement.utils.ModelPreferencesManager

class CommutingTimeRegistrationFragment :
    Fragment(), AdapterView.OnItemSelectedListener, LocationListener {

    companion object {
        fun newInstance() = CommutingTimeRegistrationFragment()
    }

    private lateinit var viewModel: CommutingTimeRegistrationViewModel
    private lateinit var spinner: Spinner

    // inactive screen layout
    private lateinit var inactiveLayout: LinearLayout

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

    // Firebase fire store and auth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //　初期画面が起動する時にアプリ内に現在の日付情報をSharedPreferenceに保存します。
        ModelPreferencesManager.with(requireActivity().application)
        ModelPreferencesManager.putStringVal(CurrentDateTime.getCurrentDate(), Constants.CURRENT_DATE)
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
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        checkGPSpermission()

        user = Firebase.auth.currentUser
        //inactiveレイアウトの初期化
        inactiveLayout = view.findViewById(R.id.commuting_check_inactive_screen)

        if (user?.email != null) {
            inactiveLayout.visibility = View.GONE
        }

        //Firebase AuthとFirestoreの初期化
        // Firebase auth　初期化
        auth = Firebase.auth
        if (auth.currentUser != null) {
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

        // 現場の位置情報初期化
        initWorkLocation()
        // 出勤・退勤ボタンの初期化
        initStartEndWorkBtnStatus()

        // 出勤ボタンのクリックイベント処理
        startWorkBtn.setOnClickListener {
            saveCommutingData()
        }
        // 退勤ボタンのクリックイベント処理
        leaveWorkBtn.setOnClickListener {
            calWorkingHours()
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val database = CommutingManagementDatabase(requireContext())
        val repository = CommutingDatabaseRepository(database)
        val viewModelFactory = CommutingTimeRegistrationViewModelFactory(repository)
        viewModel = ViewModelProvider(
            this,
            viewModelFactory
        ).get(CommutingTimeRegistrationViewModel::class.java)
    }

    // Spinnerのアイテムクリックイベントメソット
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    private fun initUIvalue() {
        if (user !== null) {
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
                    numOfPaidVacationDays.text =
                        getString(R.string.slash_with_day, commuteData.num_of_used_paid_vacation_days,commuteData.num_of_paid_vacation_days)
                    numOfWorkingDays.text =
                        getString(R.string.slash_with_day,commuteData.actual_working_days, commuteData.num_of_working_days)
                    val latPlusEarlyLeavingDays =
                        commuteData.num_of_lateness_days!! + commuteData.num_of_early_leaving_days!!
                    numOfLatenessAndEarlyLeavingDays.text = getString(R.string.with_day, latPlusEarlyLeavingDays.toString())
                    numOfAbsenceDays.text = getString(R.string.with_day,commuteData.num_of_absence_days.toString())
                    actualWorkingHours.text = getString(R.string.with_hour,commuteData.num_of_actual_lateness_hours.toString())
                    val latPlusEarlyLeavingHours =
                        commuteData.num_of_actual_lateness_hours!! + commuteData.num_of_early_leaving_hours!!
                    numOfLatenessAndEarlyLeavingHours.text = getString(R.string.with_hour, latPlusEarlyLeavingHours.toString())
                }
            }
        }
    }

    // 出勤・退勤ボタンの初期状態
    private fun initStartLeaveBtnStatus(){
        startWorkBtn.isEnabled = true
        leaveWorkBtn.isEnabled = false
        leaveWorkBtn.setBackgroundResource(R.drawable.inactive_button_rounded_shape)
    }

    // 出勤した後の退勤ボタンの状態
    private fun initLeaveWorkBtnStatus(){
        startWorkBtn.isEnabled = false
        startWorkBtn.setBackgroundResource(R.drawable.inactive_button_rounded_shape)
        leaveWorkBtn.isEnabled = true
        leaveWorkBtn.setBackgroundResource(R.drawable.end_time_button_rounded_shape)
    }

    private fun initStartEndWorkBtnStatus(){
        // 現在の日付とアプリ内で保存されている日付を比較して違う場合()
        val currentDate = CurrentDateTime.getCurrentDate()
        val savedDate = ModelPreferencesManager.getStringVal(CURRENT_DATE)
        val startWorkBtnTag = ModelPreferencesManager.getIntVal(START_WORK_BTN_TAG)
        // 現在の日付とSharedPreference上の日付が違う場合
        if (currentDate != savedDate) {
            // 新しくボタンの状態を初期化します。
            initStartLeaveBtnStatus()
        }
        // 出勤処理しなかった場合
        if(startWorkBtnTag != 1) {
            // 新しくボタンの状態を初期化します。
            initStartLeaveBtnStatus()
        // 出勤処理した場合
        }else{
            // 退勤ボタンの状態を変更します。
            initLeaveWorkBtnStatus()
        }
    }

    private fun initUIelements(view: View) {
        position = view.findViewById(R.id.position)
        name = view.findViewById(R.id.name)
        nameKatakana = view.findViewById(R.id.name_katakana)
        emailAddr = view.findViewById(R.id.emailAddr)
        numOfPaidVacationDays = view.findViewById(R.id.num_of_paid_vacation_days)
        numOfWorkingDays = view.findViewById(R.id.num_of_working_days)
        numOfLatenessAndEarlyLeavingDays =
            view.findViewById(R.id.num_of_lateness_and_early_leaving_days)
        numOfAbsenceDays = view.findViewById(R.id.num_of_absence_days)
        actualWorkingHours = view.findViewById(R.id.actual_working_hours)
        numOfLatenessAndEarlyLeavingHours =
            view.findViewById(R.id.num_of_lateness_and_early_leaving_hours)
        checkbox1 = view.findViewById(R.id.checkbox_option_1)
        checkbox2 = view.findViewById(R.id.checkbox_option_2)
        // button layout
        commutingBtnLayout = view.findViewById(R.id.commuting_button_layout)
        firstOptionLayout = view.findViewById(R.id.first_option)
        // 出勤・退勤ボタンの初期化
        startWorkBtn = view.findViewById(R.id.start_work_btn)
        leaveWorkBtn = view.findViewById(R.id.leave_work_btn)
    }

    private fun checkbox1Event() {
        checkbox1.setOnCheckedChangeListener { _, isChecked ->
            if (checkbox2.isChecked) {
                Snackbar.make(requireView(), "一つのオプションだけ選択してください。", 3000).show()
                firstOptionLayout.visibility = View.GONE
                commutingBtnLayout.visibility = View.VISIBLE
            } else {
                if (isChecked) {
                    firstOptionLayout.visibility = View.VISIBLE
                    commutingBtnLayout.visibility = View.GONE
                } else {
                    firstOptionLayout.visibility = View.GONE
                    commutingBtnLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun checkbox2Event() {
        checkbox2.setOnCheckedChangeListener { _, isChecked ->
            if (checkbox1.isChecked) {
                Snackbar.make(requireView(), "一つのオプションだけ選択してください。", 3000).show()
                firstOptionLayout.visibility = View.VISIBLE
                commutingBtnLayout.visibility = View.GONE
            } else {
                if (isChecked) {
                    commutingBtnLayout.visibility = View.VISIBLE
                    firstOptionLayout.visibility = View.GONE
                } else {
                    firstOptionLayout.visibility = View.GONE
                    commutingBtnLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun saveCommutingData() {
        val checkDistance = GPSLocation.calDistance(
            currentLocationLat!!,
            currentLocationLon!!,
            workLocationLat!!,
            workLocationLon!!
        )

        if (user !== null) {
            val userRef = firestore.collection("users").document(user!!.email!!)
            userRef.get().addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject<User>()
                if (user != null) {

                    // 50メートル以内の位置で出勤ボタンを押した時の処理
                    val commutingData = CommutingData(
                        null,
                        user.full_name.toString(),
                        "出勤",
                        currentLocationLat.toString(),
                        currentLocationLon.toString(),
                        CurrentDateTime.getCurrentDate(),
                        CurrentDateTime.getCurrentTime(),
                        "00:00",
                        "01:00",
                        "00:00",
                        "00:00",
                        "00:00",
                        "00:00"
                    )
                    viewModel.saveCommutingData(commutingData)
                    ModelPreferencesManager.putIntVal(1, START_WORK_BTN_TAG)
                    // 退勤ボタンの状態を初期化する
                    initLeaveWorkBtnStatus()
                }
            }
        }

//        if(checkDistance < DISTANCE_STANDARD){
//        } else {
        // 50メートル以外の位置で出勤ボタンを押した時の処理
//            Snackbar.make(requireView(), "勤務地から50メートル以内で出勤可能です。", 3000).show()
//        }
//        val commutingData = CommutingData(3, "Mike", "5/5", "出勤", "07:00", "08:00")
//        viewModel.saveCommutingData()

    }

    private fun checkGPSpermission() {
        // GPS 権限設定
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1000
            )
        } else {
            locationStart()
            if (::locationManager.isInitialized) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000,
                    50f,
                    this
                )
            }
        }
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

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000
            )

            Log.d("debug", "checkSelfPermission false")
            return
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            1000,
            50f,
            this
        )
    }

    override fun onLocationChanged(location: Location?) {
        // Latitude
        val lat = "Latitude:" + location?.latitude

        // Longitude
        val lon = "Longitude:" + location?.longitude

        currentLocationLat = location?.latitude
        currentLocationLon = location?.longitude

        Toast.makeText(requireContext(), "$lat, $lon", Toast.LENGTH_LONG).show()

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

    // 現場の位置情報初期化
    private fun initWorkLocation() {
        workLocationLat = 50.0
        workLocationLon = 100.0
    }

    // 一応出勤しないと退勤ボタンは押せないように処理します。
    // 退勤ボタンを押した時に処理
    private fun calWorkingHours(): String {
        // 開始時間取得
        var userObject: User? = null
        var overTime = 0
        if (user !== null) {
            val userRef = firestore.collection("users").document(user!!.email!!)
            userRef.get().addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject<User>()
                if (user != null) {
                    userObject = user
                }
            }
        }
        if (userObject != null) {
            // 出勤時間
            val startTime = userObject!!.start_time
            val startTimeHour = startTime?.substringBefore(":")?.toInt()
            val startTimeMin = startTime?.substringAfter(":")?.toInt()
            // 出勤計算用
            val startTimeSum = startTimeHour?.times(60)?.plus(startTimeMin!!)

            // 退勤時間
            val currentTime = CurrentDateTime.getCurrentTime()
            val firstSubset = currentTime.substringBefore(":").toInt()
            val otherSubsets = currentTime.substringAfter(":")
            val otherFirstSubset = otherSubsets.substringBefore(":").toInt()
            // 退勤計算用
            val leaveTimeSum = firstSubset.times(60).plus(otherFirstSubset)
            // 残業時間計算
            overTime = leaveTimeSum - startTimeSum!!
        }

        return "${overTime / 60}:${overTime % 60}"
    }
}
