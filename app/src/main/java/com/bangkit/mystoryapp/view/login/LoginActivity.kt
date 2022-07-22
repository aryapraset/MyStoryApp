package com.bangkit.mystoryapp.view.login

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bangkit.mystoryapp.ViewModelFactory
import com.bangkit.mystoryapp.databinding.ActivityLoginBinding
import com.bangkit.mystoryapp.model.LoginBody
import com.bangkit.mystoryapp.utility.UserPreference
import com.bangkit.mystoryapp.view.main.MainActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private var login: Job = Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupViewModel() {
        val pref = UserPreference.getInstance(dataStore)
        loginViewModel = ViewModelProvider(this, ViewModelFactory(pref))[LoginViewModel::class.java]
    }

    private fun setupAction() {
        binding.apply {
            loginButton.setOnClickListener {
                lifecycleScope.launchWhenResumed {
                    if (login.isActive) login.cancel()
                    login = launch {
                        val email = emailEditText.text.toString().trim()
                        val password = passwordEditText.text.toString().trim()
                        when {
                            email.isEmpty() -> {
                                emailEditText.error = "Masukkan email"
                            }
                            password.isEmpty() -> {
                                binding.passwordEditText.error = "Masukkan password"
                            }
                            else -> {
                                Log.e(TAG, "Email: $email, Password: $password")
                                val body = LoginBody(email, password)
                                loginViewModel.login(body)
                                loginViewModel.isLoading.observe(this@LoginActivity) {
                                    showLoading(it)
                                }
                                loginViewModel.isError.observe(this@LoginActivity) {
                                    if (it == true) {
                                        showToast("Login gagal")
                                    }
                                }
                                loginViewModel.loginUser.observe(this@LoginActivity) {
                                    if (it == true) {
                                        showToast("Login berhasil")
                                        Intent(
                                            this@LoginActivity,
                                            MainActivity::class.java
                                        ).also { intent ->
                                            startActivity(intent)
                                            finish()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(loading: Boolean) {
        binding.apply {
            pbLoading.visibility = if (loading) View.VISIBLE else View.GONE
            emailEditText.isClickable = !loading
            emailEditText.isEnabled = !loading
            passwordEditText.isClickable = !loading
            passwordEditText.isEnabled = !loading
            loginButton.isClickable = !loading
        }
    }
    companion object {
        private const val TAG = "LoginActivity"
    }
}