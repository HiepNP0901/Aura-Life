package com.drs.auralife.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.drs.auralife.core.database.converter.StringListConverter
import com.drs.auralife.core.database.dao.BannerCacheDao
import com.drs.auralife.core.database.dao.CategoryCacheDao
import com.drs.auralife.core.database.dao.FilmDao
import com.drs.auralife.core.database.dao.FilmDetailsDao
import com.drs.auralife.core.database.dao.HistoryDao
import com.drs.auralife.core.database.dao.LibraryDao
import com.drs.auralife.core.database.dao.LibraryFilmCrossRefDao
import com.drs.auralife.core.database.entity.BannerCacheEntity
import com.drs.auralife.core.database.entity.CategoryCacheEntity
import com.drs.auralife.core.database.entity.FilmDetailsEntity
import com.drs.auralife.core.database.entity.FilmEntity
import com.drs.auralife.core.database.entity.HistoryEntity
import com.drs.auralife.core.database.entity.LibraryEntity
import com.drs.auralife.core.database.entity.LibraryFilmCrossRef

@TypeConverters(StringListConverter::class)
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
