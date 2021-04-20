package com.nfach98.covidmap.api.response


import com.google.gson.annotations.SerializedName

data class ResponseUser(
    @SerializedName("token")
    val token: String,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("email")
    val email: Any,

    @SerializedName("email_verified_at")
    val emailVerifiedAt: Any,

    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("avatar")
    val avatar: String,

    @SerializedName("updated_at")
    val updatedAt: String,

    @SerializedName("username")
    val username: String
)