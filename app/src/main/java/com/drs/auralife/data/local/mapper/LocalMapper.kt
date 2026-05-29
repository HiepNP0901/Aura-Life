package com.drs.auralife.data.local.mapper

import com.drs.auralife.data.local.entity.BannerCacheEntity
import com.drs.auralife.data.local.entity.CategoryCacheEntity
import com.drs.auralife.data.local.entity.FilmDetailsEntity
import com.drs.auralife.data.local.entity.FilmEntity
import com.drs.auralife.data.local.entity.HistoryEntity
import com.drs.auralife.data.local.entity.LibraryEntity
import com.drs.auralife.data.local.entity.LibraryFilmCrossRef
import com.drs.auralife.data.local.entity.LibraryWithFilms
import com.drs.auralife.domain.model.Category
import com.drs.auralife.domain.model.Film
import com.drs.auralife.domain.model.FilmDetails
import com.drs.auralife.domain.model.HistoryItem
import com.drs.auralife.domain.model.Library
import com.drs.auralife.domain.model.LibraryFilm

object LocalMapper {

    // --- Film ---

    fun Film.toFilmEntity() = FilmEntity(
        slug = slug,
        title = title,
        posterUrl = posterUrl,
        thumbUrl = thumbUrl,
        description = description,
        category = category,
        episodeCount = episodeCount,
    )

    fun FilmEntity.toDomainFilm() = Film(
        id = slug,
        slug = slug,
        title = title,
        posterUrl = posterUrl,
        thumbUrl = thumbUrl,
        description = description,
        category = category,
        episodeCount = episodeCount,
    )

    // --- FilmDetails ---

    fun FilmDetails.toFilmDetailsEntity() = FilmDetailsEntity(
        slug = slug,
        title = title,
        originName = originName,
        posterUrl = posterUrl,
        thumbUrl = thumbUrl,
        trailerUrl = trailerUrl,
        description = description,
        episodeCurrent = episodeCurrent,
        episodeTotal = episodeTotal,
        quality = quality,
        language = language,
        duration = duration,
        year = year,
        status = status,
        directors = directors?.joinToString(","),
        actors = actors?.joinToString(","),
        categories = categories?.joinToString(","),
        countries = countries?.joinToString(","),
    )

    fun FilmDetailsEntity.toDomainFilmDetails() = FilmDetails(
        id = slug,
        slug = slug,
        title = title,
        originName = originName,
        posterUrl = posterUrl,
        thumbUrl = thumbUrl,
        trailerUrl = trailerUrl,
        description = description,
        episodeCurrent = episodeCurrent,
        episodeTotal = episodeTotal,
        quality = quality,
        language = language,
        duration = duration,
        year = year,
        status = status,
        directors = directors?.split(",")?.filter { it.isNotBlank() },
        actors = actors?.split(",")?.filter { it.isNotBlank() },
        categories = categories?.split(",")?.filter { it.isNotBlank() },
        countries = countries?.split(",")?.filter { it.isNotBlank() },
        episodes = emptyList(),
    )

    // --- BannerCache ---

    fun Pair<String, String>.toBannerCacheEntity() = BannerCacheEntity(
        imageUrl = first,
        filmSlug = second,
    )

    fun BannerCacheEntity.toBannerPair() = imageUrl to filmSlug

    // --- CategoryCache ---

    fun Category.toCategoryCacheEntity() = CategoryCacheEntity(
        slug = slug,
        name = name,
        localizedName = localizedName,
    )

    fun CategoryCacheEntity.toDomainCategory() = Category(
        slug = slug,
        name = name,
        localizedName = localizedName,
    )

    // --- History ---

    fun HistoryItem.toHistoryEntity() = HistoryEntity(
        slug = slug,
        title = title,
        watchedAt = watchedAt,
        episode = episode,
        position = position,
    )

    fun HistoryEntity.toDomainHistoryItem() = HistoryItem(
        slug = slug,
        title = title,
        watchedAt = watchedAt,
        episode = episode,
        position = position,
    )

    // --- Library ---

    fun Library.toLibraryEntity() = LibraryEntity(
        name = name,
        posterUrl = posterUrl,
    )

    fun Library.toLibraryFilmCrossRefs(): List<LibraryFilmCrossRef> = films.map { film ->
        LibraryFilmCrossRef(
            libraryName = name,
            filmSlug = film.slug,
            currentEpisode = film.currentEpisode,
        )
    }

    fun LibraryWithFilms.toDomainLibrary() = Library(
        name = library.name,
        posterUrl = library.posterUrl,
        films = films.map { filmEntity ->
            LibraryFilm(
                slug = filmEntity.slug,
                currentEpisode = filmEntity.episodeCount.toString(),
            )
        },
    )
}
