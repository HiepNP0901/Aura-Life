package com.drs.auralife.ui.film

import androidx.lifecycle.ViewModel
import com.drs.auralife.data.FilmRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.drs.auralife.data.model.FilmDetails
import com.drs.auralife.data.model.films.Films

class FilmsViewModel(private val repository: FilmRepository) : ViewModel() {

    private val _films = MutableLiveData<Films?>()
    val films: LiveData<Films?> get() = _films

    fun fetchLatestFilms(page: Int) {
        repository.getLatestFilms(page) { films ->
            _films.postValue(films)
        }
    }

    fun fetchFilmsByCategory(slug: String, page: Int) {
        repository.getFilmsByCategory(slug, page) { films ->
            _films.postValue(films)
        }
    }

    fun fetchFilmsByGenre(slug: String, page: Int) {
        repository.getFilmsByGenre(slug, page) { films ->
            _films.postValue(films)
        }
    }

    fun fetchFilmsByCountry(slug: String, page: Int) {
        repository.getFilmsByCountry(slug, page) { films ->
            _films.postValue(films)
        }
    }

    fun fetchFilmsByYear(slug: String, page: Int) {
        repository.getFilmsByYear(slug, page) { films ->
            _films.postValue(films)
        }
    }

    fun searchFilms(keyword: String) {
        repository.searchFilms(keyword) { films ->
            _films.postValue(films)
        }
    }



    private val _filmDetails = MutableLiveData<FilmDetails?>()
    val filmDetails: LiveData<FilmDetails?> get() = _filmDetails

    fun fetchFilmDetails(slug: String) {
        repository.getFilmDetails(slug) { filmDetails ->
            _filmDetails.postValue(filmDetails)
        }
    }
}
