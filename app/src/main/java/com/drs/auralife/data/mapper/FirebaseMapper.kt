package com.drs.auralife.data.mapper

import android.content.Context
import com.drs.auralife.data.firebase.realtime.database.category.Category as CategoryFirebase
import com.drs.auralife.data.firebase.realtime.database.user.history.History as HistoryFirebase
import com.drs.auralife.data.firebase.realtime.database.user.premium.Premium as PremiumFirebase
import com.drs.auralife.data.firebase.realtime.database.user.library.Library as LibraryFirebase
import com.drs.auralife.data.firebase.realtime.database.user.library.FilmLibrary as FilmLibraryFirebase
import com.drs.auralife.domain.model.Category as CategoryDomain
import com.drs.auralife.domain.model.HistoryItem
import com.drs.auralife.domain.model.PremiumStatus
import com.drs.auralife.domain.model.Library as LibraryDomain
import com.drs.auralife.domain.model.LibraryFilm as LibraryFilmDomain
import java.util.Locale

object FirebaseMapper {

    fun CategoryFirebase.toDomainCategory(): CategoryDomain {
        val locale = Locale.getDefault().language
        return CategoryDomain(
            slug = this.slug,
            name = if (locale == "vi") this.vi else this.en,
            localizedName = if (locale == "vi") this.en else this.vi,
        )
    }

    fun HistoryFirebase.toDomainHistoryItem(): HistoryItem {
        return HistoryItem(
            slug = this.slug,
            title = "", // Title will be populated from film details if needed
            watchedAt = this.date.toLongOrNull() ?: System.currentTimeMillis(),
        )
    }

    fun PremiumFirebase.toDomainPremiumStatus(): PremiumStatus {
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

    fun FilmLibraryFirebase.toDomainLibraryFilm(): LibraryFilmDomain {
        return LibraryFilmDomain(
            slug = this.slug,
            currentEpisode = this.episode,
        )
    }

    fun LibraryFirebase.toDomainLibrary(): LibraryDomain {
        return LibraryDomain(
            name = this.name,
            posterUrl = this.posterUrl,
            films = this.listFilm.map { it.toDomainLibraryFilm() },
        )
    }

    fun List<CategoryFirebase>.toDomainCategories(): List<CategoryDomain> {
        return this.map { it.toDomainCategory() }
    }

    fun List<HistoryFirebase>.toDomainHistoryItems(): List<HistoryItem> {
        return this.map { it.toDomainHistoryItem() }
    }

    fun List<LibraryFirebase>.toDomainLibraries(): List<LibraryDomain> {
        return this.map { it.toDomainLibrary() }
    }
}
