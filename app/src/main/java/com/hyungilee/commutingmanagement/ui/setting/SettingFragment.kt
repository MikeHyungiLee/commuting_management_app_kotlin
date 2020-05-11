package com.hyungilee.commutingmanagement.ui.setting

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.opengl.Visibility
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

import com.hyungilee.commutingmanagement.R
import com.hyungilee.commutingmanagement.ui.login.LoginActivity
import com.hyungilee.commutingmanagement.utils.Constants
import com.hyungilee.commutingmanagement.utils.ModelPreferencesManager
import kotlinx.android.synthetic.main.setting_fragment.*
import kotlin.math.sign

class SettingFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    // ログインボタン
    private lateinit var loginButton: Button
    // ログアウトボタン
    private lateinit var logoutButton: Button

    companion object {
        fun newInstance() = SettingFragment()
    }

    private lateinit var viewModel: SettingViewModel
    override fun onStart() {
        super.onStart()
        val loginStatus = ModelPreferencesManager.getIntVal(Constants.PREFERENCE_FROM_LOGIN_SCREEN)
        if(loginStatus == 1){
            logoutButton.visibility = View.VISIBLE
            loginButton.isEnabled = false
        }else{
            logoutButton.visibility = View.GONE
            loginButton.isEnabled = true
        }
    }

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
        // Firebase auth　初期化
        auth = Firebase.auth
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SettingViewModel::class.java)
    }

    private fun signOut(){
        ModelPreferencesManager.putIntVal(0, Constants.PREFERENCE_FROM_LOGIN_SCREEN)
        logout_button.visibility = View.GONE
        loginButton.isEnabled = true
        auth.signOut()
    }
}
