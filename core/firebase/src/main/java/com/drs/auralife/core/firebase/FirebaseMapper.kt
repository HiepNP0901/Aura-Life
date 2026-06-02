package com.drs.auralife.core.firebase

import com.drs.auralife.core.firebase.model.history.History
import com.drs.auralife.core.firebase.model.library.FilmLibrary
import com.drs.auralife.core.firebase.model.library.Library
import com.drs.auralife.core.firebase.model.premium.Premium
import com.drs.auralife.domain.model.HistoryItem
import com.drs.auralife.domain.model.PremiumStatus
import com.drs.auralife.data.remote.firebase.model.banner.Banner as BannerFirebase
import com.drs.auralife.domain.model.Banner as BannerDomain
import com.drs.auralife.domain.model.Library as LibraryDomain
import com.drs.auralife.domain.model.LibraryFilm as LibraryFilmDomain

object FirebaseMapper {

    fun BannerFirebase.toDomainBanner(): BannerDomain {
        return BannerDomain(
            imageUrl = this.imageUrl,
            filmSlug = this.filmSlug,
        )
    }

    fun List<BannerFirebase>.toDomainBanners(): List<BannerDomain> {
        return this.map { it.toDomainBanner() }
    }

    fun History.toDomainHistoryItem(): HistoryItem {
        return HistoryItem(
            slug = this.slug,
            title = "", // Title will be populated from film details if needed
            watchedAt = this.date.toLongOrNull() ?: System.currentTimeMillis(),
            episode = this.episode,
            position = this.position,
        )
    }

    fun Premium.toDomainPremiumStatus(): PremiumStatus {
        return PremiumStatus(
            isPremium = this.status,
            expiryTimestamp = try {
                if (this.expireDate.isNotEmpty()) {
                    this.expireDate.toLong()
                } else {
                    null
                }
            } catch (e: NumberFormatException) {
                null
            },
        )
    }

    fun FilmLibrary.toDomainLibraryFilm(): LibraryFilmDomain {
        return LibraryFilmDomain(
            slug = this.slug,
            currentEpisode = this.episode,
        )
    }

    fun Library.toDomainLibrary(): LibraryDomain {
        return LibraryDomain(
            name = this.name,
            posterUrl = this.posterUrl,
            films = this.listFilm.map { it.toDomainLibraryFilm() },
        )
    }

    fun List<History>.toDomainHistoryItems(): List<HistoryItem> {
        return this.map { it.toDomainHistoryItem() }
    }

    fun List<Library>.toDomainLibraries(): List<LibraryDomain> {
        return this.map { it.toDomainLibrary() }
    }
}
