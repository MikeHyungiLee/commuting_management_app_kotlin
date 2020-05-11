package com.hyungilee.commutingmanagement.ui.login

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hyungilee.commutingmanagement.R
import com.hyungilee.commutingmanagement.databinding.ActivityLoginBinding
import com.hyungilee.commutingmanagement.utils.BaseActivity
import com.hyungilee.commutingmanagement.utils.Constants.EMAIL_PASSWORD_TAG
import com.hyungilee.commutingmanagement.utils.Constants.INTENT_OBJECT
import com.hyungilee.commutingmanagement.utils.Constants.PREFERENCE_FROM_LOGIN_SCREEN
import com.hyungilee.commutingmanagement.utils.Constants.PREFERENCE_LOGOUT_STATUS
import com.hyungilee.commutingmanagement.utils.ModelPreferencesManager

class LoginActivity : BaseActivity(), View.OnClickListener {

    // FirebaseAuth
    private lateinit var auth: FirebaseAuth
    // login_id and password binding
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setProgressBar(binding.progressBar)

        ModelPreferencesManager.with(application)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // buttons binding
        binding.emailSignInButton.setOnClickListener(this)
        binding.emailCreateAccountButton.setOnClickListener(this)
        binding.verifyEmailButton.setOnClickListener(this)
        binding.reloadButton.setOnClickListener(this)

        // Firebase Auth 初期化
        auth = Firebase.auth
    }

    // user チェック
    override fun onStart() {
        super.onStart()
        // userがログインしているかどうかを確認する
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(user: FirebaseUser?){
        hideProgressBar()
        if(user != null){
            binding.emailPasswordFields.visibility = View.VISIBLE
            binding.emailVerificationLayout.visibility = View.GONE

            if(user.isEmailVerified){
                binding.emailPasswordButtons.visibility = View.VISIBLE
                val loginScreenFlag = ModelPreferencesManager.getIntVal(PREFERENCE_FROM_LOGIN_SCREEN)
                if(loginScreenFlag == 1) {
                    // メール認証完了・ログイン完了(Main画面に遷移する-UserIdも一緒に持って遷移する)
                    val intent = intent
                    val loginUserInfo = auth.currentUser
                    intent.putExtra(INTENT_OBJECT, loginUserInfo)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }else{
                binding.verifyEmailButton.visibility = View.VISIBLE
                binding.emailPasswordButtons.visibility = View.VISIBLE
            }
        }else{

            binding.emailPasswordButtons.visibility = View.VISIBLE
            binding.emailPasswordFields.visibility = View.VISIBLE
            binding.emailVerificationLayout.visibility = View.GONE

        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.emailCreateAccountButton -> {
                createAccount(binding.fieldEmail.text.toString(), binding.fieldPassword.text.toString())
            }
            R.id.emailSignInButton -> signIn(binding.fieldEmail.text.toString(), binding.fieldPassword.text.toString())
        }
    }

    private fun createAccount(email: String, password: String) {
        Log.d(EMAIL_PASSWORD_TAG, "createAccount:$email")
        if (!validateForm()) {
            return
        }
        showProgressBar()
        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(EMAIL_PASSWORD_TAG, "createUserWithEmail:success")
                    // 認証メール送信
                    val user = auth.currentUser
                    updateUI(user)
                    sendEmailVerification()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(EMAIL_PASSWORD_TAG, "createUserWithEmail:failure", task.exception)
                    updateUI(null)
                }
                hideProgressBar()
            }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = binding.fieldEmail.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.fieldEmail.error = "Required."
            valid = false
        } else {
            binding.fieldEmail.error = null
        }

        val password = binding.fieldPassword.text.toString()
        if (TextUtils.isEmpty(password)) {
            binding.fieldPassword.error = "Required."
            valid = false
        } else {
            binding.fieldPassword.error = null
        }

        return valid
    }

    private fun signIn(email: String, password: String) {
        Log.d(EMAIL_PASSWORD_TAG, "signIn:$email")
        if (!validateForm()) {
            return
        }
        showProgressBar()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(EMAIL_PASSWORD_TAG, "signInWithEmail:success")
                    ModelPreferencesManager.putIntVal(1, PREFERENCE_FROM_LOGIN_SCREEN)
                    ModelPreferencesManager.putIntVal(1, PREFERENCE_LOGOUT_STATUS)
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(EMAIL_PASSWORD_TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
                if (!task.isSuccessful) {
                    binding.status.text = "Authentication failed."
                }
                hideProgressBar()
            }
    }

    private fun sendEmailVerification() {

        val user = auth.currentUser!!
        user.sendEmailVerification()
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {
                    binding.status.text = "認証メールを送信しました。リンクをクリックしてアカウントを認証してください。"
                } else {
                    Log.e(EMAIL_PASSWORD_TAG, "sendEmailVerification", task.exception)
                    Toast.makeText(baseContext,
                        "Failed to send verification email.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}
