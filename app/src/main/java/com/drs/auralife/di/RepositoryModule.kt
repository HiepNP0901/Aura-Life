package com.drs.auralife.di

import com.drs.auralife.data.repository.FilmRepositoryImpl
import com.drs.auralife.domain.repository.FilmRepository
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
}
