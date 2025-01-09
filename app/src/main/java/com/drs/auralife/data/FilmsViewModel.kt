package com.drs.auralife.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.drs.auralife.data.model.film.FilmDetails
import com.drs.auralife.data.model.films.Films
import com.drs.auralife.data.model.search.SearchResults

@Suppress("unused")
class FilmsViewModel(private val repository: FilmRepository) : ViewModel() {

    private val _films = MutableLiveData<Films?>()
    private val _filmDetails = MutableLiveData<FilmDetails?>()
    private val _searchResults = MutableLiveData<SearchResults?>()

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

    fun searchFilms(keyword: String, limit: Int, callback: (SearchResults?) -> Unit) {
        repository.searchFilms(keyword, limit) { results ->
            _searchResults.postValue(results)
            callback(results)
        }
    }

    fun fetchFilmDetails(slug: String, callback: (FilmDetails?) -> Unit) {
        repository.getFilmDetails(slug) { filmDetails ->
            _filmDetails.postValue(filmDetails)
            callback(filmDetails)
        }
    }
}
