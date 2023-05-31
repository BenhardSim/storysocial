package com.example.storiessocial.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.storiessocial.R
import com.example.storiessocial.ViewModelFactory
import com.example.storiessocial.databinding.ActivityLoginBinding
import com.example.storiessocial.model.local.prefrence.UserModel
import com.example.storiessocial.view.main.MainActivity
import com.example.storiessocial.view.signup.SignupActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity(){

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private var loginJob: Job = Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.noAccount.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
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

        val emailText =  ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailLayout = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val emailEdit = ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(500)

        val passText =  ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passLayout = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val passEdit = ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(500)

        val noAccount =  ObjectAnimator.ofFloat(binding.noAccount, View.ALPHA, 1f).setDuration(500)
        val loginBtn =  ObjectAnimator.ofFloat(binding.loginbtn, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(emailText, emailLayout,emailEdit,passText,passLayout,passEdit)
        }

        AnimatorSet().apply {
            playSequentially(title,msg,together,noAccount,loginBtn)
            start()
        }
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(application)
        )[LoginViewModel::class.java]
    }

    private fun setupAction() {
        binding.loginbtn.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            when{
                email.isEmpty() -> {
                    val emptyNameWarning: String = getString(R.string.emptyNameWarning)
                    binding.edLoginEmail.error = emptyNameWarning
                    showLoading(false)
                }
                password.isEmpty() -> {
                    val emptyNameWarning: String = getString(R.string.emptyNameWarning)
                    binding.edLoginPassword.error = emptyNameWarning
                    showLoading(false)
                }
                binding.edLoginEmail.error == null && binding.edLoginPassword.error == null -> {
                    /* set view */
                    showLoading(true)
                    binding.emailEditTextLayout.error = null
                    binding.passwordEditTextLayout.error = null

                    /* login logic */
                    lifecycleScope.launchWhenResumed {
                        if (loginJob.isActive) loginJob.cancel()
                        loginJob = launch {

                            loginViewModel.login(email, password).collect { result ->
                                /* if response was successful*/
                                result.onSuccess { res ->
                                    res.loginResult?.token?.let { token ->
                                        /* saving token to preference */
                                        loginViewModel.saveUser(UserModel(email, password, token,true))
                                        val msg: String = getString(R.string.loginSucces)
                                        Toast.makeText(
                                            this@LoginActivity,
                                            msg,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        delay(500)
                                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                        startActivity(intent)
                                    }
                                }
                                result.onFailure {
                                    val msg: String = getString(R.string.loginFail)
                                    Toast.makeText(
                                        this@LoginActivity,
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