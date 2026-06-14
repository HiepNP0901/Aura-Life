package com.drs.auralife.core.database.mapper

import com.drs.auralife.core.database.entity.BannerCacheEntity
import com.drs.auralife.core.database.entity.CategoryCacheEntity
import com.drs.auralife.core.database.entity.FilmDetailsEntity
import com.drs.auralife.core.database.entity.FilmEntity
import com.drs.auralife.core.database.entity.HistoryEntity
import com.drs.auralife.core.database.entity.LibraryEntity
import com.drs.auralife.core.database.entity.LibraryFilmCrossRef
import com.drs.auralife.core.database.entity.FilmWithCurrentEpisode
import com.drs.auralife.core.database.entity.LibraryWithFilms
import com.drs.auralife.domain.model.Banner
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
        modifiedAt = modifiedAt,
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
        modifiedAt = modifiedAt,
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
        directors = directors,
        actors = actors,
        categories = categories,
        countries = countries,
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
        directors = directors,
        actors = actors,
        categories = categories,
        countries = countries,
        episodes = emptyList(),
    )

    // --- BannerCache ---

    fun Banner.toBannerCacheEntity() = BannerCacheEntity(
        imageUrl = imageUrl,
        filmSlug = filmSlug,
    )

    fun BannerCacheEntity.toDomainBanner() = Banner(
        imageUrl = imageUrl,
        filmSlug = filmSlug,
    )

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
                currentEpisode = "1",
            )
        },
    )

    fun LibraryWithFilms.toDomainLibrary(episodes: List<FilmWithCurrentEpisode>): Library {
        val episodeMap = episodes.associate { it.slug to it.currentEpisode }
        return Library(
            name = library.name,
            posterUrl = library.posterUrl,
            films = films.map { filmEntity ->
                LibraryFilm(
                    slug = filmEntity.slug,
                    currentEpisode = episodeMap[filmEntity.slug] ?: "1",
                )
            },
        )
    }
}
