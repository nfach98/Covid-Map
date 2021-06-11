package com.nfach98.covidmap.model

import com.google.gson.annotations.SerializedName

data class History (
    @SerializedName("from")
    val from: String,

    @SerializedName("lat_from")
    val fromLat: Float,

    @SerializedName("lng_from")
    val fromLng: Float,

    @SerializedName("to")
    val to: String,

    @SerializedName("lat_to")
    val toLat: Float,

    @SerializedName("lng_to")
    val toLng: Float,

    @SerializedName("date")
    val date: String,

    @SerializedName("id")
    val id: Int,
)