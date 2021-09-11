package com.nfach98.covidmap.api.response


import com.google.gson.annotations.SerializedName
import com.nfach98.covidmap.model.koordinat_line.Data

data class ResponseMap(
    @SerializedName("feature_collection")
    val featureCollection: String,

    @SerializedName("data")
    val listData: ArrayList<Data>,
)