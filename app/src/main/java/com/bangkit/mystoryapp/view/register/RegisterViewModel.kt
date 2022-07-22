package com.bangkit.mystoryapp.view.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.mystoryapp.model.RegisterBody
import com.bangkit.mystoryapp.network.api.ApiConfig
import com.bangkit.mystoryapp.network.response.AuthResponse
import com.bangkit.mystoryapp.utility.UserPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel(pref: UserPreference) : ViewModel() {
    private val _registeredUser = MutableLiveData<Boolean>()
    val registeredUser: LiveData<Boolean> = _registeredUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: MutableLiveData<Boolean> = _isError

    fun register(body: RegisterBody) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().register(body)
        client.enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                _isLoading.value = false
                _isError.value = false
                _registeredUser.value = false

                if (response.isSuccessful) {
                    val message =
                        "onSuccess: ${response.body()!!.message}, ${response.body()!!.error}"
                    Log.d(TAG, message)
                    _registeredUser.value = false
                } else {
                    val message = "onFailure: ${response.message()}"
                    _isError.value = true
                    Log.e(TAG, message)
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                val message = "onFailure: ${t.message.toString()}"
                _isError.value = true
                Log.e(TAG, message)
            }

        })
    }

    companion object {
        private const val TAG = "RegisterViewModel"
    }
}