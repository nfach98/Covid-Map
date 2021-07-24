package com.nfach98.covidmap.model.koordinat_titik


import com.google.gson.annotations.SerializedName

data class Kelurahan(
    @SerializedName("aktif")
    val aktif: String,

    @SerializedName("alamat_lurah")
    val alamatLurah: Any,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("deleted_at")
    val deletedAt: Any,

    @SerializedName("id_kec")
    val idKec: String,

    @SerializedName("id_kel")
    val idKel: String,

    @SerializedName("id_kel_dinkes")
    val idKelDinkes: String,

    @SerializedName("id_kel_old")
    val idKelOld: String,

    @SerializedName("id_puskesmas")
    val idPuskesmas: String,

    @SerializedName("kd_kelmetro")
    val kdKelmetro: String,

    @SerializedName("kd_pos")
    val kdPos: String,

    @SerializedName("lurah")
    val lurah: String,

    @SerializedName("nama_cktr")
    val namaCktr: String,

    @SerializedName("nm_kel")
    val nmKel: String,

    @SerializedName("nm_kel_dinkes")
    val nmKelDinkes: String,

    @SerializedName("nm_kel_mbr")
    val nmKelMbr: String,

    @SerializedName("updated_at")
    val updatedAt: Any
)