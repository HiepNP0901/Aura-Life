package com.drs.auralife.core.network

import com.drs.auralife.core.network.model.CategoryResponse
import com.drs.auralife.core.network.model.FilmDetails
import com.drs.auralife.core.network.model.Films
import com.drs.auralife.core.network.model.SearchResults
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FilmAPI {
    @GET("the-loai")
    suspend fun getCategories(): List<CategoryResponse>

    @GET("danh-sach/phim-moi-cap-nhat")
    suspend fun getLatestFilms(
        @Query("page") page: Int,
    ): Films

    @GET("v1/api/the-loai/{slug}")
    suspend fun getFilmsByCategory(
        @Path("slug") slug: String,
        @Query("page") page: Int,
    ): SearchResults

    @GET("v1/api/tim-kiem")
    suspend fun searchFilms(
        @Query("keyword") keyword: String,
        @Query("limit") limit: Int,
    ): SearchResults

    @GET("phim/{slug}")
    suspend fun getFilmDetails(
        @Path("slug") slug: String,
    ): FilmDetails
}
