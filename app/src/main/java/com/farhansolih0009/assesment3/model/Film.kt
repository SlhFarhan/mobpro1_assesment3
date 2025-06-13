package com.farhansolih0009.assesment3.model

import com.squareup.moshi.Json

data class Film(
    val id: Int,
    @Json(name = "nama_film")
    val namaFilm: String,
    val pemeran: String,
    val deskripsi: String?, // <-- Tambahkan ini
    @Json(name = "gambar")
    val gambar: String,
    val userId: Int?
)