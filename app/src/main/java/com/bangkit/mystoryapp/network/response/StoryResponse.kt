package com.bangkit.mystoryapp.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

class StoryResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("listStory")
    val listStory: List<Story>
) {
    @Parcelize
    data class Story(
        @SerializedName("id")
        val id: String,

        @SerializedName("name")
        val name: String,

        @SerializedName("description")
        val description: String,

        @SerializedName("photoUrl")
        val photoUrl: String,

        @SerializedName("createdAt")
        val createdAt: String
    ) : Parcelable
}