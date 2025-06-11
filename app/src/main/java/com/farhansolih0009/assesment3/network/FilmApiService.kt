package com.farhansolih0009.assesment3.network

import com.farhansolih0009.assesment3.model.Film
import com.farhansolih0009.assesment3.model.OpStatus
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

// PENTING: Ganti dengan URL Render Anda yang sudah di-deploy
private const val BASE_URL = "https://wide-bittersweet-cruiser.glitch.me/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface FilmApiService {
    @GET("films")
    suspend fun getFilm(
        @Header("Authorization") userId: String
    ): List<Film>

    @Multipart
    @POST("films")
    suspend fun postFilm(
        @Header("Authorization") userId: String,
        @Part("name") name: RequestBody,
        @Part image: MultipartBody.Part
    ): OpStatus

    @Multipart
    @PUT("films/{id}")
    suspend fun updateFilm(
        @Header("Authorization") userId: String,
        @Path("id") filmId: String,
        @Part("name") name: RequestBody,
        @Part image: MultipartBody.Part? // Gambar sekarang opsional
    ): OpStatus

    @DELETE("films/{id}")
    suspend fun deleteFilmWithPath(
        @Header("Authorization") userId: String,
        @Path("id") filmId: String
    ): OpStatus
}

object FilmApi {
    val service: FilmApiService by lazy {
        retrofit.create(FilmApiService::class.java)
    }
}

enum class ApiStatus { LOADING, SUCCESS, FAILED }