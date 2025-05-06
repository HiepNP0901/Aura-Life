package com.drs.auralife.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.data.model.film.FilmDetails
import com.drs.auralife.data.model.films.Films
import com.drs.auralife.data.model.search.SearchResults
import kotlinx.coroutines.launch

class FilmsViewModel(
    context: Context,
) : ViewModel() {
    private val api: FilmAPI

    init {
        val retrofit = RetrofitClient.create(context)
        api = retrofit.create(FilmAPI::class.java)
    }

    fun fetchLatestFilms(
        page: Int,
        callback: (Films?) -> Unit,
    ) {
        viewModelScope.launch {
            Log.d("TAG", this.coroutineContext.toString())
            callback(api.getLatestFilms(page))
        }
    }

    fun fetchFilmsByCategory(
        slug: String,
        page: Int,
        callback: (SearchResults?) -> Unit,
    ) {
        viewModelScope.launch {
            Log.d("TAG", this.coroutineContext.toString())
            callback(api.getFilmsByCategory(slug, page))
        }
    }

    fun searchFilms(
        keyword: String,
        limit: Int,
        callback: (SearchResults?) -> Unit,
    ) {
        viewModelScope.launch {
            Log.d("TAG", this.coroutineContext.toString())
            callback(api.searchFilms(keyword, limit))
        }
    }

    fun fetchFilmDetails(
        slug: String,
        callback: (FilmDetails?) -> Unit,
    ) {
        viewModelScope.launch {
            Log.d("TAG", this.coroutineContext.toString())
            callback(api.getFilmDetails(slug))
        }
    }
}
