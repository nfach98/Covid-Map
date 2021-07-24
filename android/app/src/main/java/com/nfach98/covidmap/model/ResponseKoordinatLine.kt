package com.nfach98.covidmap.model


import com.google.gson.annotations.SerializedName
import com.nfach98.covidmap.model.koordinat_line.Data
import com.nfach98.covidmap.model.koordinat_line.Kelurahan

data class ResponseKoordinatLine(
    @SerializedName("data")
    val data: List<Data>,

    @SerializedName("kelurahan")
    val kelurahan: List<Kelurahan>,

    @SerializedName("line_coordinats")
    val lineCoordinats: List<List<List<Double>>>
)