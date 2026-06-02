package com.drs.auralife.core.network.mapper

import com.drs.auralife.core.network.model.film.Episode
import com.drs.auralife.core.network.model.film.FilmDetails
import com.drs.auralife.core.network.model.film.Movie
import com.drs.auralife.domain.model.Film
import java.time.Instant

object FilmMapper {
    fun Movie.toDomainFilm(): Film {
        val modifiedAt = try {
            modified?.time?.let { Instant.parse(it).toEpochMilli() } ?: 0
        } catch (e: Exception) {
            0
        }
        return Film(
            id = movieID ?: slug,
            slug = slug,
            title = name ?: originName ?: "",
            posterUrl = posterUrl.orEmpty(),
            thumbUrl = thumbUrl.orEmpty(),
            description = content.orEmpty(),
            category = category?.firstOrNull()?.name.orEmpty(),
            episodeCount = episodeTotal?.toIntOrNull() ?: 0,
            modifiedAt = modifiedAt,
        )
    }

    fun FilmDetails.toDomainFilmDetails(): com.drs.auralife.domain.model.FilmDetails {
        return com.drs.auralife.domain.model.FilmDetails(
            id = movie.movieID ?: movie.slug,
            slug = movie.slug,
            title = movie.name ?: movie.originName ?: "",
            originName = movie.originName ?: "",
            posterUrl = movie.posterUrl.orEmpty(),
            thumbUrl = movie.thumbUrl.orEmpty(),
            trailerUrl = movie.trailerUrl,
            description = movie.content.orEmpty(),
            episodeCurrent = movie.episodeCurrent,
            episodeTotal = movie.episodeTotal,
            quality = movie.quality,
            language = movie.lang,
            duration = movie.time,
            year = movie.year,
            status = movie.status,
            directors = movie.director,
            actors = movie.actor,
            categories = movie.category?.map { it.name },
            countries = movie.country?.map { it.name },
            episodes = episodes.flatMap { episode -> mapEpisodeToDomain(episode) },
        )
    }

    private fun mapEpisodeToDomain(episode: Episode): List<com.drs.auralife.domain.model.Episode> {
        return episode.serverData.map { data ->
            com.drs.auralife.domain.model.Episode(
                name = data.name,
                filename = data.filename,
                linkM3u8 = data.linkM3u8,
            )
        }
    }
}
