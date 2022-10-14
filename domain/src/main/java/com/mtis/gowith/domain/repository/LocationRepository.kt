package com.mtis.gowith.domain.repository

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    fun getRealTimeLocationData(): Flow<Location>

    fun updateRealTimeLocationData(location: Location)
}