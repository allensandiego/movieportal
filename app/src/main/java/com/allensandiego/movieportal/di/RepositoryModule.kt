package com.allensandiego.movieportal.di

import com.allensandiego.movieportal.data.repository.TMDBRepository
import com.allensandiego.movieportal.data.repository.TMDBRepositoryImpl
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
    abstract fun bindTMDBRepository(
        tmdbRepositoryImpl: TMDBRepositoryImpl
    ): TMDBRepository
}
