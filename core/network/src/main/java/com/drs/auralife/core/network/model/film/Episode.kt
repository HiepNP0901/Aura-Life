package com.drs.auralife.core.network.model.film

import com.google.gson.annotations.SerializedName

data class Episode(
    @SerializedName("server_data") val serverData: List<ServerData>,
    @SerializedName("server_name") val serverName: String,
)
