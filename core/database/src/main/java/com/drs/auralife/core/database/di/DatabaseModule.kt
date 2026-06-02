package com.drs.auralife.core.database.di

import android.content.Context
import androidx.room.Room
import com.drs.auralife.core.database.dao.BannerCacheDao
import com.drs.auralife.core.database.dao.CategoryCacheDao
import com.drs.auralife.core.database.dao.FilmDao
import com.drs.auralife.core.database.dao.FilmDetailsDao
import com.drs.auralife.core.database.dao.HistoryDao
import com.drs.auralife.core.database.dao.LibraryDao
import com.drs.auralife.core.database.dao.LibraryFilmCrossRefDao
import com.drs.auralife.core.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "aura_life.db",
            ).fallbackToDestructiveMigration(false).build()
    }

    @Provides
    fun provideFilmDao(db: AppDatabase): FilmDao = db.filmDao()

    @Provides
    fun provideFilmDetailsDao(db: AppDatabase): FilmDetailsDao = db.filmDetailsDao()

    @Provides
    fun provideBannerCacheDao(db: AppDatabase): BannerCacheDao = db.bannerCacheDao()

    @Provides
    fun provideCategoryCacheDao(db: AppDatabase): CategoryCacheDao = db.categoryCacheDao()

    @Provides
    fun provideHistoryDao(db: AppDatabase): HistoryDao = db.historyDao()

    @Provides
    fun provideLibraryDao(db: AppDatabase): LibraryDao = db.libraryDao()

    @Provides
    fun provideLibraryFilmCrossRefDao(db: AppDatabase): LibraryFilmCrossRefDao = db.libraryFilmCrossRefDao()
}
