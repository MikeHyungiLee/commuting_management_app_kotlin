package com.hyungilee.commutingmanagement.ui.setting

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

import com.hyungilee.commutingmanagement.R
import com.hyungilee.commutingmanagement.ui.login.LoginActivity
import com.hyungilee.commutingmanagement.ui.setting.models.CommuteData
import com.hyungilee.commutingmanagement.ui.setting.models.User
import com.hyungilee.commutingmanagement.utils.Constants
import com.hyungilee.commutingmanagement.utils.Constants.CURRENT_DATE
import com.hyungilee.commutingmanagement.utils.CurrentDateTime
import com.hyungilee.commutingmanagement.utils.ModelPreferencesManager
import kotlinx.android.synthetic.main.setting_fragment.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SettingFragment : Fragment() {

    // ログインボタン
    private lateinit var loginButton: Button
    // ログアウトボタン
    private lateinit var logoutButton: Button
    // ログインアカウント情報
    private lateinit var user: FirebaseUser
    // ログインemailアカウント
    private lateinit var emailInfo: TextView
    // Firebase fire store
    private lateinit var firestore: FirebaseFirestore

    private lateinit var auth: FirebaseAuth

    companion object {
        fun newInstance() = SettingFragment()
    }

    private lateinit var viewModel: SettingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.setting_fragment, container, false)
        // SharedPreference クラスの初期化
        ModelPreferencesManager.with(requireActivity().application)
        // ログインボタンのイベント処理
        loginButton = view.findViewById(R.id.login_button)
        loginButton.setOnClickListener {
            val loginStatus = ModelPreferencesManager.getIntVal(Constants.PREFERENCE_FROM_LOGIN_SCREEN)
            if(loginStatus == 0) {
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
        // サインアウトボタンのイベント処理
        logoutButton = view.findViewById(R.id.logout_button)
        logoutButton.setOnClickListener {
            signOut()
        }
        emailInfo = view.findViewById(R.id.login_email_account_info)
        // Firebase auth　初期化
        auth = Firebase.auth
        if(auth.currentUser!=null){
            user = auth.currentUser as FirebaseUser
            accountInfoSetting(user)
        }
        // Firestore初期化
        firestore = Firebase.firestore

        return view
    }

    private fun accountInfoSetting(user: FirebaseUser){
        emailInfo.text = user.email
    }

    private fun accountInfoInitialSetting(){
        emailInfo.text = ""
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SettingViewModel::class.java)
    }

    private fun signOut(){
        ModelPreferencesManager.putIntVal(0, Constants.PREFERENCE_FROM_LOGIN_SCREEN)
        logout_button.visibility = View.GONE
        loginButton.isEnabled = true
        accountInfoInitialSetting()
        auth.signOut()
    }

    override fun onStart() {
        super.onStart()
        val loginStatus = ModelPreferencesManager.getIntVal(Constants.PREFERENCE_FROM_LOGIN_SCREEN)
        if(loginStatus == 1){
            logoutButton.visibility = View.VISIBLE
            loginButton.isEnabled = false
            if(auth.currentUser!=null){
                user = auth.currentUser as FirebaseUser
                accountInfoSetting(user)
                writeNewUserTable(user)
                writeNewCommutingDataTable(user)
            }
        }else{
            logoutButton.visibility = View.GONE
            loginButton.isEnabled = true
        }
    }

    private fun writeNewUserTable(user: FirebaseUser){

        val userRef = firestore.collection("users").document(user.email!!)

        // Documentの存在を検査する(ログインしたユーザーのユーザ情報テーブルが存在しない場合はテーブルを作成します。)
        userRef.get().addOnSuccessListener {doc->
            val currentDate = CurrentDateTime.getCurrentDate()
            val currentTime = CurrentDateTime.getCurrentTime()
            if(!doc.exists()){
                userRef.set(User())
                userRef.update(mapOf(
                    "email" to user.email,
                    "created_at" to "$currentDate,$currentTime"
                ))
            }
        }
    }

    private fun writeNewCommutingDataTable(user: FirebaseUser){
        val comRef = firestore.collection("commuting_data").document(user.email!!)

        // Documentの存在を検査する(ログインしたユーザーのユーザ情報テーブルが存在しない場合はテーブルを作成します。)
        comRef.get().addOnSuccessListener {doc->
            val currentDate = CurrentDateTime.getCurrentDate()
            val currentTime = CurrentDateTime.getCurrentTime()
            if(!doc.exists()){
                comRef.set(CommuteData())
                comRef.update(mapOf(
                    "email" to user.email,
                    "created_at" to "$currentDate,$currentTime"
                ))
            }
        }
    }
}
