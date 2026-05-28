package com.drs.auralife.data.mapper

import com.drs.auralife.data.model.film.Episode
import com.drs.auralife.data.model.film.Movie
import com.drs.auralife.domain.model.Film
import com.drs.auralife.domain.model.FilmDetails

object FilmMapper {
    fun Movie.toDomainFilm(): Film {
        return Film(
            id = movieID ?: slug,
            slug = slug,
            title = name ?: originName ?: "",
            posterUrl = posterUrl.orEmpty(),
            thumbUrl = thumbUrl.orEmpty(),
            description = content.orEmpty(),
            category = category?.firstOrNull()?.name.orEmpty(),
            episodeCount = episodeTotal?.toIntOrNull() ?: 0,
        )
    }

    fun com.drs.auralife.data.model.film.FilmDetails.toDomainFilmDetails(): FilmDetails {
        return FilmDetails(
            id = movie.movieID ?: movie.slug,
            slug = movie.slug,
            title = movie.name ?: movie.originName ?: "",
            posterUrl = movie.posterUrl.orEmpty(),
            thumbUrl = movie.thumbUrl.orEmpty(),
            description = movie.content.orEmpty(),
            videos = episodes.flatMap { episode -> mapEpisodeToUrls(episode) },
            metadata = mapOf(
                "year" to (movie.year?.toString() ?: ""),
                "type" to (movie.type ?: ""),
                "language" to (movie.lang ?: ""),
                "status" to (movie.status ?: ""),
                "quality" to (movie.quality ?: ""),
                "episodeCurrent" to (movie.episodeCurrent ?: ""),
                "episodeTotal" to (movie.episodeTotal ?: ""),
            ),
        )
    }

    private fun mapEpisodeToUrls(episode: Episode): List<String> {
        return episode.serverData.mapNotNull { it.linkEmbed.takeIf { link -> link.isNotBlank() } }
    }
}
