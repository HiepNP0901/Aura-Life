package com.drs.auralife.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.drs.auralife.data.local.dao.BannerCacheDao
import com.drs.auralife.data.local.dao.CategoryCacheDao
import com.drs.auralife.data.local.dao.FilmDao
import com.drs.auralife.data.local.dao.FilmDetailsDao
import com.drs.auralife.data.local.dao.HistoryDao
import com.drs.auralife.data.local.dao.LibraryDao
import com.drs.auralife.data.local.dao.LibraryFilmCrossRefDao
import com.drs.auralife.data.local.entity.BannerCacheEntity
import com.drs.auralife.data.local.entity.CategoryCacheEntity
import com.drs.auralife.data.local.entity.FilmDetailsEntity
import com.drs.auralife.data.local.entity.FilmEntity
import com.drs.auralife.data.local.entity.HistoryEntity
import com.drs.auralife.data.local.entity.LibraryEntity
import com.drs.auralife.data.local.entity.LibraryFilmCrossRef

@Database(
    entities = [
        FilmEntity::class,
        FilmDetailsEntity::class,
        BannerCacheEntity::class,
        CategoryCacheEntity::class,
        HistoryEntity::class,
        LibraryEntity::class,
        LibraryFilmCrossRef::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun filmDao(): FilmDao
    abstract fun filmDetailsDao(): FilmDetailsDao
    abstract fun bannerCacheDao(): BannerCacheDao
    abstract fun categoryCacheDao(): CategoryCacheDao
    abstract fun historyDao(): HistoryDao
    abstract fun libraryDao(): LibraryDao
    abstract fun libraryFilmCrossRefDao(): LibraryFilmCrossRefDao
}
