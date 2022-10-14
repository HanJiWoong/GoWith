package com.mtis.gowith.domain.interactors

import android.location.Location
import com.mtis.gowith.domain.Interactor
import com.mtis.gowith.domain.repository.LocationRepository
import javax.inject.Inject

class UpdateRealTimeLocation @Inject constructor(
    private val locationRepository: LocationRepository
) : Interactor<UpdateRealTimeLocation.Params, Unit>() {

    override fun doWork(params: Params) {
        locationRepository.updateRealTimeLocationData(params.location)
    }

    data class Params(
        val location: Location
    )
}