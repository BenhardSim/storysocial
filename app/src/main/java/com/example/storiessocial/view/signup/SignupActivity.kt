package com.example.storiessocial.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.storiessocial.R
import com.example.storiessocial.ViewModelFactory
import com.example.storiessocial.databinding.ActivitySignupBinding
import com.example.storiessocial.view.login.LoginActivity
import kotlinx.coroutines.*

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var signupViewModel: SignupViewModel
    private var registerJob: Job = Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.hasAccount.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        setupViewModel()
        setupAction()
        playAnimation()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.logo, View.ROTATION, 0f, 360f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val msg = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(500)

        val nameText =  ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(500)
        val nameLayout = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val nameEdit = ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(500)

        val emailText =  ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailLayout = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val emailEdit = ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(500)

        val passText =  ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passLayout = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val passEdit = ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(500)

        val noAccount =  ObjectAnimator.ofFloat(binding.hasAccount, View.ALPHA, 1f).setDuration(500)
        val loginBtn =  ObjectAnimator.ofFloat(binding.loginbtn, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(nameText,nameLayout,nameEdit,emailText, emailLayout,emailEdit,passText,passLayout,passEdit)
        }

        AnimatorSet().apply {
            playSequentially(title,msg,together,noAccount,loginBtn)
            start()
        }
    }

    private fun setupViewModel() {
        signupViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(application)
        )[SignupViewModel::class.java]
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun setupAction() {
        binding.loginbtn.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            binding.nameEditTextLayout.error = null
            binding.nameEditTextLayout.isErrorEnabled = false
            binding.emailEditTextLayout.error = null
            binding.emailEditTextLayout.isErrorEnabled = false
            binding.passwordEditTextLayout.error = null
            binding.passwordEditTextLayout.isErrorEnabled = false

            when {
                name.isEmpty() -> {
                    val emptyNameWarning: String = getString(R.string.emptyNameWarning)
                    binding.edRegisterName.error = emptyNameWarning
                    showLoading(false)
                }
                email.isEmpty() -> {
                    val emptyEmailWarning: String = getString(R.string.emptyEmailWarning)
                    binding.edRegisterEmail.error = emptyEmailWarning
                    showLoading(false)
                }
                password.isEmpty() -> {
                    val emptyPasswordWarning: String = getString(R.string.emptyPasswordWarning)
                    binding.edRegisterPassword.error = emptyPasswordWarning
                    showLoading(false)
                }
                binding.edRegisterName.error == null && binding.edRegisterEmail.error == null && binding.edRegisterPassword.error == null -> {
                    showLoading(true)
                    lifecycleScope.launchWhenResumed {
                        // Make sure only one job that handle the login process
                        if (registerJob.isActive) registerJob.cancel()
                        registerJob = launch {
                            signupViewModel.register(name,email, password).collect { result ->
                                result.onSuccess { res ->
                                    res.message?.let{ message ->
                                        Log.e("signin","berhasil masuk !")
                                        GlobalScope.launch(Dispatchers.Main) {
                                            AlertDialog.Builder(this@SignupActivity).apply {
                                                setTitle("Yey!")
                                                val msg: String = getString(R.string.regisSuccess)
                                                val msgLogin: String = getString(R.string.login)
                                                setMessage("$msg $message")
                                                setPositiveButton(msgLogin) { _, _ ->
                                                    finish()
                                                }
                                                create()
                                                show()
                                            }
                                        }
                                    }

                                }
                                result.onFailure {
                                    val msg:String = getString(R.string.regisFail)
                                    Toast.makeText(
                                        this@SignupActivity,
                                        msg,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    showLoading(false)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}