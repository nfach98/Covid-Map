package com.nfach98.covidmap.api.response


import com.google.gson.annotations.SerializedName

data class ResponseMap(
    @SerializedName("feature_collection")
    val featureCollection: String
)