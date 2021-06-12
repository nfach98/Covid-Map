package com.nfach98.covidmap.api

import com.nfach98.covidmap.api.response.ResponseMap
import com.nfach98.covidmap.api.response.ResponseStatus
import com.nfach98.covidmap.api.response.ResponseUser
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

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
        @Header("token") token: String,
    ): Call<ResponseStatus>

    @POST("api/detail")
    fun detail(
        @Header("token") token: String,
    ): Call<ResponseUser>

    @Multipart
    @POST("api/update")
    fun update(
        @Header("token") token: String,
        @Query("name") name: String,
        @Query("username") username: String,
        @Part file: MultipartBody.Part?
    ): Call<ResponseStatus>

    @POST("api/check-username")
    fun checkUsername(
        @Header("token") token: String,
        @Query("username") username: String
    ): Call<ResponseStatus>

    @POST("api/koord-line")
    fun koordinatLine(
        @Header("token") token: String
    ): Call<ResponseMap>
}