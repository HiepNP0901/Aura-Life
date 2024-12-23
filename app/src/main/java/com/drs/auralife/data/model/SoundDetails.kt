package com.drs.auralife.data.model

import com.google.gson.annotations.SerializedName

data class SoundDetails(
    @SerializedName("id") val id: Int,
    @SerializedName("url") val url: String,
    @SerializedName("name") val name: String,
    @SerializedName("tags") val tags: List<String>,
    @SerializedName("description") val description: String,
    @SerializedName("geotag") val geotag: String,
    @SerializedName("created") val created: String,
    @SerializedName("license") val license: String,
    @SerializedName("type") val type: String,
    @SerializedName("channels") val channels: Int,
    @SerializedName("filesize") val fileSize: Int,
    @SerializedName("bitrate") val bitrate: Int,
    @SerializedName("bitdepth") val bitDepth: Int,
    @SerializedName("duration") val duration: Float,
    @SerializedName("samplerate") val sampleRate: Float,
    @SerializedName("username") val username: String,
    @SerializedName("pack") val pack: String,
    @SerializedName("pack_name") val packName: String,
    @SerializedName("download") val download: String,
    @SerializedName("bookmark") val bookmark: String,
    @SerializedName("previews") val previews: Preview,
    @SerializedName("images") val images: Images,
    @SerializedName("num_downloads") val numDownloads: Int,
    @SerializedName("avg_rating") val avgRating: Float,
    @SerializedName("num_ratings") val numRatings: Int,
    @SerializedName("rate") val rate: String,
    @SerializedName("comments") val comments: String,
    @SerializedName("num_comments") val numComments: Int,
    @SerializedName("comment") val comment: String,
    @SerializedName("similar_sounds") val similarSounds: String,
    @SerializedName("analysis") val analysis: String,
    @SerializedName("analysis_frames") val analysisFrames: String,
    @SerializedName("analysis_stats") val analysisStats: String,
    @SerializedName("is_explicit") val isExplicit: Boolean
)

data class Preview(
    @SerializedName("preview-lq-mp3") val previewLQMP3: String,
    @SerializedName("preview-hq-mp3") val previewHQMP3: String,
    @SerializedName("preview-lq-ogg") val previewLQOGG: String,
    @SerializedName("preview-hq-ogg") val previewHQOGG: String,
)

data class Images(
    @SerializedName("waveform_m") val waveformM: String,
    @SerializedName("waveform_l") val waveformL: String,
    @SerializedName("spectral_m") val spectralM: String,
    @SerializedName("spectral_l") val spectralL: String,
    @SerializedName("waveform_bw_m") val waveformBWM: String,
    @SerializedName("waveform_bw_l") val waveformBWL: String,
    @SerializedName("spectral_bw_m") val spectralBWM: String,
    @SerializedName("spectral_bw_l") val spectralBWL: String,
)