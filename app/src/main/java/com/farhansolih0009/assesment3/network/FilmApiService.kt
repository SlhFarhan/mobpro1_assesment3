package com.farhansolih0009.assesment3.network

import com.farhansolih0009.assesment3.model.Film
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

// PENTING: URL ini harus sesuai dengan URL Railway Anda
private const val BASE_URL = "https://api-film-production-b846.up.railway.app/"

// --- Model untuk request dan response Auth ---
data class GoogleLoginRequest(
    val token: String
)

data class AuthResponse(
    val id: Int,
    val email: String,
    val accessToken: String
)
// ---------------------------------------------

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface FilmApiService {
    // Auth
    @POST("api/auth/google")
    suspend fun loginWithGoogle(@Body request: GoogleLoginRequest): AuthResponse

    // Get (Read)
    @GET("api/films")
    suspend fun getFilms(): List<Film>

    // Create
    @Multipart
    @POST("api/films")
    suspend fun addFilm(
        @Header("Authorization") token: String,
        @Part("nama_film") namaFilm: RequestBody,
        @Part("pemeran") pemeran: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody, // <-- TAMBAHKAN INI
        @Part gambar: MultipartBody.Part
    ): Film

    // Update
    @Multipart
    @PUT("api/films/{id}")
    suspend fun updateFilm(
        @Header("Authorization") token: String,
        @Path("id") filmId: Int,
        @Part("nama_film") namaFilm: RequestBody,
        @Part("pemeran") pemeran: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody, // <-- TAMBAHKAN INI
        @Part gambar: MultipartBody.Part?
    ): Film

    // Delete
    @DELETE("api/films/{id}")
    suspend fun deleteFilm(
        @Header("Authorization") token: String,
        @Path("id") filmId: Int
    ): Response<Unit>
}

object FilmApi {
    val service: FilmApiService by lazy {
        retrofit.create(FilmApiService::class.java)
    }
}
