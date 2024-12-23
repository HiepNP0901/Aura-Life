package com.drs.auralife.data

import com.drs.auralife.data.model.SoundDetails
import com.drs.auralife.data.model.Sounds
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

const val BASE_URL = "https://freesound.org"
const val API_KEY = "BEESpXy25cIK3en5rGmK6prkrzUbu7epBYmkJLvC"

interface SoundsService {
    @GET("apiv2/search/text/")
    suspend fun searchSounds(
        @Query("query") key: String,
        @Query("token") token: String = API_KEY,
    ): Response<Sounds>

    @GET
    suspend fun getNextSounds(
        @Url url: String,
        @Query("token") token: String = API_KEY,
    ): Response<Sounds>

    @GET("apiv2/sounds/{id}/")
    suspend fun getSoundById(
        @Path("id") id: Int,
        @Query("token") token: String = API_KEY,
    ): Response<SoundDetails>
}
