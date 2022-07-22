package com.bangkit.mystoryapp.network.api

import com.bangkit.mystoryapp.model.LoginBody
import com.bangkit.mystoryapp.model.RegisterBody
import com.bangkit.mystoryapp.network.response.AuthResponse
import com.bangkit.mystoryapp.network.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("register")
    fun register(
        @Body registerBody: RegisterBody
    ): Call<AuthResponse>

    @POST("login")
    fun login(
        @Body loginBody: LoginBody
    ): Call<AuthResponse>

    @GET("story")
    fun getAllStory(
        @Header("Authorization") token: String
    ): Call<StoryResponse>

    @Multipart
    @POST("story")
    fun postStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<StoryResponse>
}