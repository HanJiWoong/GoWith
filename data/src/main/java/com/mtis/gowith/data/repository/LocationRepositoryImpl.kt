package com.mtis.gowith.data.repository

import android.location.Location
import com.mtis.gowith.data.source.local.LocationService
import com.mtis.gowith.domain.repository.LocationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepositoryImpl @Inject constructor(
    private val locationService: LocationService
):LocationRepository {

    fun getCurrentLocationData() = locationService.currentLocationData

    override fun getRealTimeLocationData() = locationService.realTimeLocationData

    fun updateCurrentLocationData(location: Location){
        locationService.updateCurrentLocationData(location)
    }

    override fun updateRealTimeLocationData(location: Location){
        locationService.updateRealTimeLocationData(location)
    }
}