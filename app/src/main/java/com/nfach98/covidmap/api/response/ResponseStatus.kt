package com.nfach98.covidmap.api.response


import com.google.gson.annotations.SerializedName

data class ResponseStatus(
    @SerializedName("status")
    val status: String
)