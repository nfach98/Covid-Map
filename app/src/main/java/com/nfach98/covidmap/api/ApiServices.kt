package com.nfach98.covidmap.api

import com.nfach98.covidmap.model.ResponseKoordinatLine
import com.nfach98.covidmap.model.ResponseKoordinatTitik
import retrofit2.Call
import retrofit2.http.GET

interface ApiServices {
    @GET("area_pasien/load_koordinat_titik")
    fun getKoordinatTitik(): Call<ResponseKoordinatTitik>

    @GET("area_pasien/load_koordinat_line")
    fun getKoordinatLine(): Call<ResponseKoordinatLine>
}