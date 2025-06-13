package com.farhansolih0009.assesment3.ui.screen

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farhansolih0009.assesment3.model.Film
import com.farhansolih0009.assesment3.network.AuthResponse
import com.farhansolih0009.assesment3.network.FilmApi
import com.farhansolih0009.assesment3.network.GoogleLoginRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

enum class ApiStatus { LOADING, SUCCESS, FAILED }

class MainViewModel : ViewModel() {

    var data = mutableStateOf(emptyList<Film>())
        private set
    var status = MutableStateFlow(ApiStatus.LOADING)
        private set
    var errorMessage = mutableStateOf<String?>(null)
        private set

    fun loginWithGoogle(googleIdToken: String, onLoginSuccess: (AuthResponse) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = GoogleLoginRequest(token = googleIdToken)
                val response = FilmApi.service.loginWithGoogle(request)
                onLoginSuccess(response)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Google Login Failure: ${e.message}")
                errorMessage.value = "Google Login Error: ${e.message}"
            }
        }
    }

    fun retrieveData() {
        viewModelScope.launch(Dispatchers.IO) {
            status.value = ApiStatus.LOADING
            try {
                data.value = FilmApi.service.getFilms()
                status.value = ApiStatus.SUCCESS
            } catch (e: Exception) {
                Log.e("MainViewModel", "Retrieve Failure: ${e.message}")
                status.value = ApiStatus.FAILED
            }
        }
    }

    fun saveData(authToken: String, namaFilm: String, pemeran: String, deskripsi: String, bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bearerToken = "Bearer $authToken"

                val namaFilmBody = namaFilm.toRequestBody("text/plain".toMediaTypeOrNull())
                val pemeranBody = pemeran.toRequestBody("text/plain".toMediaTypeOrNull())
                val deskripsiBody = deskripsi.toRequestBody("text/plain".toMediaTypeOrNull())
                val gambarPart = bitmap.toMultipartBody()

                FilmApi.service.addFilm(bearerToken, namaFilmBody, pemeranBody, deskripsiBody, gambarPart)
                retrieveData()
            } catch (e: Exception) {
                Log.e("MainViewModel", "Save Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun updateData(authToken: String, filmToUpdate: Film, newName: String, newPemeran: String, newDeskripsi: String, newBitmap: Bitmap?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bearerToken = "Bearer $authToken"

                val namaFilmBody = newName.toRequestBody("text/plain".toMediaTypeOrNull())
                val pemeranBody = newPemeran.toRequestBody("text/plain".toMediaTypeOrNull())
                val deskripsiBody = newDeskripsi.toRequestBody("text/plain".toMediaTypeOrNull())
                val gambarPart = newBitmap?.toMultipartBody()

                FilmApi.service.updateFilm(bearerToken, filmToUpdate.id, namaFilmBody, pemeranBody, deskripsiBody, gambarPart)
                retrieveData()
            } catch (e: Exception) {
                Log.e("MainViewModel", "Update Failure: ${e.message}")
                errorMessage.value = "Error updating: ${e.message}"
            }
        }
    }

    fun deleteFilm(authToken: String, filmId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bearerToken = "Bearer $authToken"
                val response = FilmApi.service.deleteFilm(bearerToken, filmId)
                if (response.isSuccessful) {
                    retrieveData()
                } else {
                    throw Exception("Failed to delete. Code: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Delete Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    private fun Bitmap.toMultipartBody(): MultipartBody.Part {
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        val requestBody = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull(), 0, byteArray.size)
        return MultipartBody.Part.createFormData("gambar", "image.jpg", requestBody)
    }

    fun clearMessage() {
        errorMessage.value = null
    }

    fun getFilmById(id: Int): com.farhansolih0009.assesment3.model.Film? {
        return data.value.find { it.id == id }
    }
}
