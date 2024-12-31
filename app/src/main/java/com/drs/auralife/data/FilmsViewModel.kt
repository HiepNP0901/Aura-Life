package com.drs.auralife.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.drs.auralife.data.model.FilmDetails
import com.drs.auralife.data.model.films.Films

class FilmsViewModel(private val repository: FilmRepository) : ViewModel() {

    private val _films = MutableLiveData<Films?>()
    private val _filmDetails = MutableLiveData<FilmDetails?>()

    fun fetchLatestFilms(page: Int, callback: (Films?) -> Unit) {
        repository.getLatestFilms(page) { films ->
            _films.postValue(films)
            callback(films)
        }
    }

    fun fetchFilmsByCategory(slug: String, page: Int, callback: (Films?) -> Unit) {
        repository.getFilmsByCategory(slug, page) { films ->
            _films.postValue(films)
            callback(films)
        }
    }

    fun fetchFilmsByGenre(slug: String, page: Int, callback: (Films?) -> Unit) {
        repository.getFilmsByGenre(slug, page) { films ->
            _films.postValue(films)
            callback(films)
        }
    }

    fun fetchFilmsByCountry(slug: String, page: Int, callback: (Films?) -> Unit) {
        repository.getFilmsByCountry(slug, page) { films ->
            _films.postValue(films)
            callback(films)
        }
    }

    fun fetchFilmsByYear(slug: String, page: Int, callback: (Films?) -> Unit) {
        repository.getFilmsByYear(slug, page) { films ->
            _films.postValue(films)
            callback(films)
        }
    }

    fun searchFilms(keyword: String, callback: (Films?) -> Unit) {
        repository.searchFilms(keyword) { films ->
            _films.postValue(films)
            callback(films)
        }
    }

    fun fetchFilmDetails(slug: String, callback: (FilmDetails?) -> Unit) {
        repository.getFilmDetails(slug) { filmDetails ->
            _filmDetails.postValue(filmDetails)
            callback(filmDetails)
        }
    }
}
