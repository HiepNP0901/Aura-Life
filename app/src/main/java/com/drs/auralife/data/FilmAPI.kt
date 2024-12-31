package com.drs.auralife.data

import com.drs.auralife.data.model.FilmDetails
import com.drs.auralife.data.model.films.Films
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

const val BASE_URL = "https://phim.nguonc.com"

interface FilmAPI {
    @GET("api/films/phim-moi-cap-nhat")
    fun getLatestFilms(@Query("page") page: Int): Call<Films>

    @GET("api/films/danh-sach/{slug}")
    fun getFilmsByCategory(@Path("slug") slug: String, @Query("page") page: Int): Call<Films>

    @GET("api/films/the-loai/{slug}")
    fun getFilmsByGenre(@Path("slug") slug: String, @Query("page") page: Int): Call<Films>

    @GET("api/films/quoc-gia/{slug}")
    fun getFilmsByCountry(@Path("slug") slug: String, @Query("page") page: Int): Call<Films>

    @GET("api/films/nam-phat-hanh/{slug}")
    fun getFilmsByYear(@Path("slug") slug: String, @Query("page") page: Int): Call<Films>

    @GET("api/films/search")
    fun searchFilms(@Query("keyword") keyword: String): Call<Films>

    @GET("film/{slug}")
    fun getFilmDetails(@Path("slug") slug: String): Call<FilmDetails>
}
