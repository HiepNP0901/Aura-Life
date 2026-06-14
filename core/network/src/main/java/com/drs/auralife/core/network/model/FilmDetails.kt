package com.drs.auralife.core.network.model

import com.google.gson.annotations.SerializedName

data class FilmDetails(
    val episodes: List<Episode>,
    val movie: Movie,
    val msg: String,
    val status: Boolean,
) {
    data class Episode(
        @SerializedName("server_data") val serverData: List<ServerData>,
        @SerializedName("server_name") val serverName: String,
    ) {
        data class ServerData(
            val filename: String,
            @SerializedName("link_embed") val linkEmbed: String,
            @SerializedName("link_m3u8") val linkM3u8: String,
            val name: String,
            val slug: String,
        )
    }
}
