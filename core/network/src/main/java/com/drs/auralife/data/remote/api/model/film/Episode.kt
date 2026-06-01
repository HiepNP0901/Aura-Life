package com.drs.auralife.data.remote.api.model.film

import com.google.gson.annotations.SerializedName

data class Episode(
    @SerializedName("server_data") val serverData: List<ServerData>,
    @SerializedName("server_name") val serverName: String,
)
