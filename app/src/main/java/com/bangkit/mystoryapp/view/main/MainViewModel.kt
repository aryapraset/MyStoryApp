package com.bangkit.mystoryapp.view.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.bangkit.mystoryapp.network.api.ApiConfig
import com.bangkit.mystoryapp.network.response.StoryResponse
import com.bangkit.mystoryapp.utility.UserPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val pref: UserPreference) : ViewModel() {
    private val _storyUser = MutableLiveData<List<StoryResponse.Story>>()
    val storyUser: LiveData<List<StoryResponse.Story>> = _storyUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getAllStory(token: String?){
        _isLoading.value = true
        val client = ApiConfig.getApiService().getAllStory("Bearer $token")
        client.enqueue(object : Callback<StoryResponse>{
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                _isLoading.value = false
                if(response.isSuccessful){
                    _storyUser.value = response.body()?.listStory
                    Log.e(TAG, "isSuccessful: ${response.body()?.message}")
                }else{
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }
    fun getUserToken(): LiveData<String?> = pref.getUserToken().asLiveData()

    companion object{
        private const val TAG = "MainViewModel"
    }
}