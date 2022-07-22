package com.bangkit.mystoryapp.view.register

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bangkit.mystoryapp.R
import com.bangkit.mystoryapp.ViewModelFactory
import com.bangkit.mystoryapp.databinding.ActivityRegisterBinding
import com.bangkit.mystoryapp.model.RegisterBody
import com.bangkit.mystoryapp.utility.UserPreference
import com.bangkit.mystoryapp.view.login.LoginActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class RegisterActivity : AppCompatActivity() {
    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var binding: ActivityRegisterBinding
    private var register: Job = Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupAction()
    }

    private fun setupViewModel() {
        val pref = UserPreference.getInstance(dataStore)
        registerViewModel = ViewModelProvider(this, ViewModelFactory(pref))[RegisterViewModel::class.java]

    }

    private fun setupAction() {
        binding.apply {
            lifecycleScope.launchWhenResumed {
                if (register.isActive) register.cancel()
                register = launch {
                    signupButton.setOnClickListener {
                        val name = nameEditText.text.toString()
                        val email = emailEditText.text.toString()
                        val password = passwordEditText.text.toString()
                        when {
                            name.isEmpty() -> {
                                nameEditText.error = "Masukkan nama"
                            }
                            email.isEmpty() -> {
                                emailEditText.error = "Masukkan email"
                            }
                            email.isNotEmpty() -> {
                                emailEditText.error = "Email Anda tidak valid"
                            }
                            password.isEmpty() -> {
                                passwordEditText.error = "Masukkan password"
                            }
                            else -> {
                                Log.e(TAG, "Name: $name, Email: $email, Password: $password")
                                val body = RegisterBody(name, email, password)
                                registerViewModel.register(body)
                            }
                        }

                        registerViewModel.isError.observe(this@RegisterActivity) {
                            if (it == true) {
                                showToast(getString(R.string.registration_failed))
                            }
                        }

                        registerViewModel.isLoading.observe(this@RegisterActivity) {
                            showLoading(it)
                        }

                        registerViewModel.registeredUser.observe(this@RegisterActivity) {
                            if (it == true) {
                                showToast("Registrasi berhasil")
                                val intent =
                                    Intent(this@RegisterActivity, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(loading: Boolean) {
        binding.apply {
            pbLoading.visibility = if (loading) View.VISIBLE else View.GONE
            nameEditText.isClickable = !loading
            nameEditText.isEnabled = !loading
            emailEditText.isClickable = !loading
            emailEditText.isEnabled = !loading
            passwordEditText.isClickable = !loading
            passwordEditText.isEnabled = !loading
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val TAG = "RegisterActivity"
    }
}
