package com.drs.auralife.data.remote.api

import com.drs.auralife.data.remote.api.model.film.Episode
import com.drs.auralife.data.remote.api.model.film.Movie
import com.drs.auralife.domain.model.Film
import com.drs.auralife.domain.model.FilmDetails
import com.drs.auralife.domain.model.FilmEpisode

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

    fun com.drs.auralife.data.remote.api.model.film.FilmDetails.toDomainFilmDetails(): FilmDetails {
        return FilmDetails(
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

    private fun mapEpisodeToDomain(episode: Episode): List<FilmEpisode> {
        return episode.serverData.map { data ->
            FilmEpisode(
                name = data.name,
                filename = data.filename,
                linkM3u8 = data.linkM3u8,
            )
        }
    }
}
