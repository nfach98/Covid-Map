package com.nfach98.covidmap.api

import com.nfach98.covidmap.api.response.ResponseStatus
import com.nfach98.covidmap.api.response.ResponseUser
import com.nfach98.covidmap.model.ResponseKoordinatLine
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiServices {
    /*@GET("area_pasien/load_koordinat_titik")
    fun getKoordinatTitik(): Call<ResponseKoordinatTitik>

    @GET("area_pasien/load_koordinat_line")
    fun getKoordinatLine(): Call<ResponseKoordinatLine>*/

    @POST("api/register")
    fun register(
        @Query("name") name: String,
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("c_password") confirmPassword: String
    ): Call<ResponseUser>

    @POST("api/login")
    fun login(
        @Query("username") username: String,
        @Query("password") password: String
    ): Call<ResponseUser>

    @POST("api/logout")
    fun logout(
        @Header("Authorization") token: String,
    ): Call<ResponseStatus>
}