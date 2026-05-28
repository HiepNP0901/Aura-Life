package com.drs.auralife.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.data.FilmAPI
import com.drs.auralife.data.RetrofitClient
import com.drs.auralife.data.repository.FilmRepositoryImpl
import com.drs.auralife.domain.model.Film
import com.drs.auralife.domain.model.FilmDetails
import com.drs.auralife.domain.usecase.GetFilmDetailsUseCase
import com.drs.auralife.domain.usecase.GetFilmsByCategoryUseCase
import com.drs.auralife.domain.usecase.GetLatestFilmsUseCase
import com.drs.auralife.domain.usecase.SearchFilmsUseCase
import kotlinx.coroutines.launch

import com.drs.auralife.data.model.film.FilmDetails as FilmDetailsResponse
import com.drs.auralife.data.model.films.Films
import com.drs.auralife.data.model.search.SearchResults

class FilmsViewModel(
    private val getLatestFilmsUseCase: GetLatestFilmsUseCase,
    private val getFilmsByCategoryUseCase: GetFilmsByCategoryUseCase,
    private val searchFilmsUseCase: SearchFilmsUseCase,
    private val getFilmDetailsUseCase: GetFilmDetailsUseCase,
    private val legacyApi: FilmAPI? = null,
) : ViewModel() {

    constructor(context: Context) : this(
        buildUseCases(context).first,
        buildUseCases(context).second,
        buildUseCases(context).third,
        buildUseCases(context).fourth,
        RetrofitClient.create(context).create(FilmAPI::class.java),
    )

    companion object {
        private fun buildUseCases(context: Context): UseCaseBundle {
            val api = RetrofitClient.create(context).create(FilmAPI::class.java)
            val repository = FilmRepositoryImpl(api)
            return UseCaseBundle(
                GetLatestFilmsUseCase(repository),
                GetFilmsByCategoryUseCase(repository),
                SearchFilmsUseCase(repository),
                GetFilmDetailsUseCase(repository),
            )
        }
    }

    private data class UseCaseBundle(
        val first: GetLatestFilmsUseCase,
        val second: GetFilmsByCategoryUseCase,
        val third: SearchFilmsUseCase,
        val fourth: GetFilmDetailsUseCase,
    )

    fun fetchLatestFilms(
        page: Int,
        callback: (List<Film>?) -> Unit,
    ) {
        viewModelScope.launch {
            Log.d("TAG", this.coroutineContext.toString())
            callback(getLatestFilmsUseCase(page))
        }
    }

    fun fetchFilmsByCategory(
        slug: String,
        page: Int,
        callback: (List<Film>?) -> Unit,
    ) {
        viewModelScope.launch {
            Log.d("TAG", this.coroutineContext.toString())
            callback(getFilmsByCategoryUseCase(slug, page))
        }
    }

    fun searchFilms(
        keyword: String,
        limit: Int,
        callback: (List<Film>?) -> Unit,
    ) {
        viewModelScope.launch {
            Log.d("TAG", this.coroutineContext.toString())
            callback(searchFilmsUseCase(keyword, limit))
        }
    }

    fun fetchFilmDetails(
        slug: String,
        callback: (FilmDetails?) -> Unit,
    ) {
        viewModelScope.launch {
            Log.d("TAG", this.coroutineContext.toString())
            callback(getFilmDetailsUseCase(slug))
        }
    }

    fun fetchLatestFilmsLegacy(
        page: Int,
        callback: (Films?) -> Unit,
    ) {
        viewModelScope.launch {
            legacyApi?.let {
                callback(it.getLatestFilms(page))
            } ?: callback(null)
        }
    }

    fun fetchFilmsByCategoryLegacy(
        slug: String,
        page: Int,
        callback: (SearchResults?) -> Unit,
    ) {
        viewModelScope.launch {
            legacyApi?.let {
                callback(it.getFilmsByCategory(slug, page))
            } ?: callback(null)
        }
    }

    fun searchFilmsLegacy(
        keyword: String,
        limit: Int,
        callback: (SearchResults?) -> Unit,
    ) {
        viewModelScope.launch {
            legacyApi?.let {
                callback(it.searchFilms(keyword, limit))
            } ?: callback(null)
        }
    }

    fun fetchFilmDetailsLegacy(
        slug: String,
        callback: (FilmDetailsResponse?) -> Unit,
    ) {
        viewModelScope.launch {
            legacyApi?.let {
                callback(it.getFilmDetails(slug))
            } ?: callback(null)
        }
    }
}
