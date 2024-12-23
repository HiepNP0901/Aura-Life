package com.drs.auralife.data.model

import com.google.gson.annotations.SerializedName

data class Sounds(
    @SerializedName("count") val count: Int,
    @SerializedName("previous") val previous: String?,
    @SerializedName("next") val next: String?,
    @SerializedName("results") val results: List<SoundDetails>
)