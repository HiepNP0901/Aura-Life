package com.drs.auralife.data

import android.content.Context
import android.util.Log
import com.drs.auralife.data.model.film.FilmDetails
import com.drs.auralife.data.model.films.Films
import com.drs.auralife.data.model.search.SearchResults
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FilmRepository(context: Context) {
    private val api: FilmAPI

    init {
        val retrofit = RetrofitClient.create(context)
        api = retrofit.create(FilmAPI::class.java)
    }

    private fun <T> makeApiCall(call: Call<T>, onResult: (T?) -> Unit) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    onResult(response.body())
                } else {
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                Log.d("aaa", "onFailure: $t")
                onResult(null)
            }
        })
    }

    fun getLatestFilms(page: Int, onResult: (Films?) -> Unit) {
        makeApiCall(api.getLatestFilms(page), onResult)
    }

    fun getFilmsByCategory(slug: String, page: Int, onResult: (Films?) -> Unit) {
        makeApiCall(api.getFilmsByCategory(slug, page), onResult)
    }

    fun searchFilms(keyword: String, limit: Int, onResult: (SearchResults?) -> Unit) {
        makeApiCall(api.searchFilms(keyword, limit), onResult)
    }

    fun getFilmDetails(slug: String, onResult: (FilmDetails?) -> Unit) {
        makeApiCall(api.getFilmDetails(slug), onResult)
    }
}