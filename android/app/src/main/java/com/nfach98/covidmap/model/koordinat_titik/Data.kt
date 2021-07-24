package com.nfach98.covidmap.model.koordinat_titik


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("alamat")
    val alamat: String,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("draw")
    val draw: String,

    @SerializedName("icon_marker")
    val iconMarker: String,

    @SerializedName("id")
    val id: String,

    @SerializedName("jenis_titik")
    val jenisTitik: String,

    @SerializedName("kecamatan")
    val kecamatan: Any,

    @SerializedName("kelurahan")
    val kelurahan: String,

    @SerializedName("latlng")
    val latlng: String,

    @SerializedName("nama_ketua")
    val namaKetua: Any,

    @SerializedName("nama_titik")
    val namaTitik: String,

    @SerializedName("telpon")
    val telpon: Any
)