package com.mtis.gowith.data.source.local

import android.location.Location
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

class LocationService @Inject constructor() {
    private val _currentLocationData = MutableSharedFlow<Location>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val currentLocationData: Flow<Location> = _currentLocationData

    private val _realTimeLocationData = MutableSharedFlow<Location>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val realTimeLocationData: Flow<Location> = _realTimeLocationData

    fun updateCurrentLocationData(location: Location){
        _currentLocationData.tryEmit(location)
    }

    fun updateRealTimeLocationData(location: Location){
        _realTimeLocationData.tryEmit(location)
    }
}