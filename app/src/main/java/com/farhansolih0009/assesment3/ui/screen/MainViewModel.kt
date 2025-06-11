package com.farhansolih0009.assesment3.ui.screen

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farhansolih0009.assesment3.model.Film
import com.farhansolih0009.assesment3.network.ApiStatus
import com.farhansolih0009.assesment3.network.FilmApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

class MainViewModel : ViewModel() {
    var data = mutableStateOf(emptyList<Film>())
        private set

    var status = MutableStateFlow(ApiStatus.LOADING)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    fun retrieveData(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            status.value = ApiStatus.LOADING
            try {
                data.value = FilmApi.service.getFilm(userId)
                status.value = ApiStatus.SUCCESS
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                status.value = ApiStatus.FAILED
            }
        }
    }

    fun saveData(userId: String, name: String, bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = FilmApi.service.postFilm(
                    userId,
                    name.toRequestBody("text/plain".toMediaTypeOrNull()),
                    bitmap.toMultipartBody()
                )
                if (result.status == "success")
                    retrieveData(userId)
                else
                    throw Exception(result.message)
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun updateData(userId: String, filmId: String, newName: String, newBitmap: Bitmap?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val nameRequestBody = newName.toRequestBody("text/plain".toMediaTypeOrNull())
                val imagePart = newBitmap?.toMultipartBody()

                val result = FilmApi.service.updateFilm(
                    userId,
                    filmId,
                    nameRequestBody,
                    imagePart
                )

                if (result.status == "success") {
                    retrieveData(userId)
                } else {
                    throw Exception(result.message)
                }
            } catch (e: Exception) {
                Log.d("MainViewModel", "Update failed: ${e.message}")
                errorMessage.value = "Error updating: ${e.message}"
            }
        }
    }

    fun deleteData(userId: String, filmId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = FilmApi.service.deleteFilmWithPath(userId, filmId)
                if (result.status == "success") {
                    retrieveData(userId)
                } else {
                    throw Exception(result.message)
                }
            } catch (e: Exception) {
                Log.d("MainViewModel", "Delete operation failed: ${e.message}")
                errorMessage.value = "Error deleting: ${e.message}"
            }
        }
    }

    private fun Bitmap.toMultipartBody(): MultipartBody.Part {
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        val requestBody = byteArray.toRequestBody(
            "image/jpeg".toMediaTypeOrNull(), 0, byteArray.size
        )
        return MultipartBody.Part.createFormData(
            "image", "image.jpg", requestBody
        )
    }

    fun clearMessage() {
        errorMessage.value = null
    }
}