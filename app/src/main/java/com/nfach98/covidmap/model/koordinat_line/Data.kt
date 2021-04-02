package com.nfach98.covidmap.model.koordinat_line


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("color")
    val color: String,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("draw")
    val draw: String,

    @SerializedName("id")
    val id: String,

    @SerializedName("jml_status_confirm")
    val jmlStatusConfirm: String,

    @SerializedName("jml_status_test")
    val jmlStatusTest: String,

    @SerializedName("kecamatan")
    val kecamatan: Any,

    @SerializedName("kelurahan")
    val kelurahan: String,

    @SerializedName("latlng")
    val latlng: String,

    @SerializedName("nama_jalan")
    val namaJalan: String,

    @SerializedName("status_confirm")
    val statusConfirm: String,

    @SerializedName("status_test")
    val statusTest: String,

    @SerializedName("tanggal_test")
    val tanggalTest: Any
)