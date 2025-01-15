package com.drs.auralife.data

import com.drs.auralife.data.model.film.FilmDetails
import com.drs.auralife.data.model.films.Films
import com.drs.auralife.data.model.search.SearchResults
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FilmAPI {
    @GET("danh-sach/phim-moi-cap-nhat")
    fun getLatestFilms(@Query("page") page: Int): Call<Films>

    @GET("v1/api/danh-sach/{slug}")
    fun getFilmsByCategory(
        @Path("slug") slug: String,
        @Query("page") page: Int
    ): Call<Films>

    @GET("v1/api/tim-kiem")
    fun searchFilms(
        @Query("keyword") keyword: String,
        @Query("limit") limit: Int
    ): Call<SearchResults>

    @GET("phim/{slug}")
    fun getFilmDetails(
        @Path("slug") slug: String
    ): Call<FilmDetails>
}
