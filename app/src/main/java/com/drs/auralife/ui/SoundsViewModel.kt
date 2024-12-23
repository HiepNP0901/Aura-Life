package com.drs.auralife.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.data.RetrofitClient
import com.drs.auralife.data.model.SoundDetails
import com.drs.auralife.data.model.Sounds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Suppress("unused")
class SoundViewModel : ViewModel() {
    private fun <T> executeRequest(request: suspend () -> T, onResult: (Result<T>) -> Unit) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) { request() }
                onResult(Result.success(response))
            } catch (e: Exception) {
                onResult(Result.failure(e))
            }
        }
    }

    fun getAllSounds(onResult: (Result<Sounds>) -> Unit) {
        executeRequest(
            request = {
                val response = RetrofitClient.instance.searchSounds("")
                if (response.isSuccessful) {
                    requireNotNull(response.body()) { "Response body is null" }
                } else {
                    throw Exception("Request failed with code ${response.code()}")
                }
            },
            onResult = onResult
        )
    }

    fun getNextSounds(url: String, onResult: (Result<Sounds>) -> Unit) {
        executeRequest(
            request = {
                val response = RetrofitClient.instance.getNextSounds(url)
                if (response.isSuccessful) {
                    requireNotNull(response.body()) { "Response body is null" }
                } else {
                    throw Exception("Request failed with code ${response.code()}")
                }
            },
            onResult = onResult
        )
    }

    fun getSoundById(id: Int, onResult: (Result<SoundDetails>) -> Unit) {
        executeRequest(
            request = {
                val response = RetrofitClient.instance.getSoundById(id)
                if (response.isSuccessful) {
                    requireNotNull(response.body()) { "Response body is null" }
                } else {
                    throw Exception("Request failed with code ${response.code()}")
                }
            },
            onResult = onResult
        )
    }

    fun searchSounds(key: String, onResult: (Result<Sounds>) -> Unit) {
        executeRequest(
            request = {
                val response = RetrofitClient.instance.searchSounds(key)
                if (response.isSuccessful) {
                    requireNotNull(response.body()) { "Response body is null" }
                } else {
                    throw Exception("Request failed with code ${response.code()}")
                }
            },
            onResult = onResult
        )
    }
}