package com.drs.auralife.di

import com.drs.auralife.data.repository.AvatarRepositoryImpl
import com.drs.auralife.data.repository.BannerRepositoryImpl
import com.drs.auralife.data.repository.CategoryRepositoryImpl
import com.drs.auralife.data.repository.FilmRepositoryImpl
import com.drs.auralife.data.repository.HistoryRepositoryImpl
import com.drs.auralife.data.repository.LibraryRepositoryImpl
import com.drs.auralife.data.repository.PremiumRepositoryImpl
import com.drs.auralife.domain.repository.AvatarRepository
import com.drs.auralife.domain.repository.BannerRepository
import com.drs.auralife.domain.repository.CategoryRepository
import com.drs.auralife.domain.repository.FilmRepository
import com.drs.auralife.domain.repository.HistoryRepository
import com.drs.auralife.domain.repository.LibraryRepository
import com.drs.auralife.domain.repository.PremiumRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFilmRepository(impl: FilmRepositoryImpl): FilmRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindHistoryRepository(impl: HistoryRepositoryImpl): HistoryRepository

    @Binds
    @Singleton
    abstract fun bindLibraryRepository(impl: LibraryRepositoryImpl): LibraryRepository

    @Binds
    @Singleton
    abstract fun bindBannerRepository(impl: BannerRepositoryImpl): BannerRepository

    @Binds
    @Singleton
    abstract fun bindPremiumRepository(impl: PremiumRepositoryImpl): PremiumRepository

    @Binds
    @Singleton
    abstract fun bindAvatarRepository(impl: AvatarRepositoryImpl): AvatarRepository
}
