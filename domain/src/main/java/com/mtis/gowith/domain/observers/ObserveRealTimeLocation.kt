package com.mtis.gowith.domain.observers

import android.location.Location
import com.mtis.gowith.domain.FlowInteractor
import com.mtis.gowith.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveRealTimeLocation @Inject constructor(
    private val locationRepository: LocationRepository
): FlowInteractor<Unit, Location>() {
    override fun createObservable(params: Unit): Flow<Location> {
        return locationRepository.getRealTimeLocationData()
    }
}