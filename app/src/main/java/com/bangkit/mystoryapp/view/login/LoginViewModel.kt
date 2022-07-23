package com.bangkit.mystoryapp.view.login

import android.util.Log
import androidx.lifecycle.*
import com.bangkit.mystoryapp.model.LoginBody
import com.bangkit.mystoryapp.network.api.ApiConfig
import com.bangkit.mystoryapp.network.response.AuthResponse
import com.bangkit.mystoryapp.utility.UserPreference
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: UserPreference) : ViewModel(){
    private val _loginUser = MutableLiveData<Boolean>()
    val loginUser: MutableLiveData<Boolean> = _loginUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: MutableLiveData<Boolean> = _isError

    fun login(body: LoginBody){
        _isLoading.value = true
        val client = ApiConfig.getApiService().login(body)
        client.enqueue(object : Callback<AuthResponse>{
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                _isLoading.value = false
                _isError.value = false
                _loginUser.value = false

                if(response.isSuccessful){
                    val loginResult = response.body()!!.loginResult
                    val token = loginResult.token
                    val name = loginResult.name
                    val userId = loginResult.userId
                    val email = body.email

                    Log.d(TAG, "Token: $token, Name: $name, UserId: $userId, Email: $email")

                    saveToken(token)
                    saveId(userId)
                    saveName(name)
                    saveEmail(email)

                    _loginUser.value = true
                }else{
                    _isError.value = true
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                val message = "onFailure: ${t.message.toString()}"
                _isError.value = true
                Log.e(TAG, message)
            }

        })
    }
    fun saveToken(token: String){
        viewModelScope.launch {
            pref.saveUserToken(token)
        }
    }

    fun saveId(id: String){
        viewModelScope.launch {
            pref.saveUserId(id)
        }
    }

    fun saveEmail(email: String){
        viewModelScope.launch{
            pref.saveUserEmail(email)
        }
    }
    fun saveName(name: String){
        viewModelScope.launch {
            pref.saveUserName(name)
        }
    }

    companion object{
        private const val TAG = "LoginViewModel"
    }
}