package com.nfach98.covidmap.model


import com.google.gson.annotations.SerializedName
import com.nfach98.covidmap.model.koordinat_titik.Data
import com.nfach98.covidmap.model.koordinat_titik.Kelurahan

data class ResponseKoordinatTitik(
    @SerializedName("data")
    val data: List<Data>,

    @SerializedName("kelurahan")
    val kelurahan: List<Kelurahan>,

    @SerializedName("line_coordinats")
    val lineCoordinats: List<List<List<Double>>>
)