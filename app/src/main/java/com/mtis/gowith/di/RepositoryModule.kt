package com.mtis.gowith.di

import com.mtis.gowith.data.repository.LocationRepositoryImpl
import com.mtis.gowith.data.source.local.LocationService
import com.mtis.gowith.domain.repository.LocationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Repository 등록 클래스
@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Provides
    @Singleton
    fun provideLocationRepository(locationService: LocationService): LocationRepository {
        return LocationRepositoryImpl(locationService)
    }
}