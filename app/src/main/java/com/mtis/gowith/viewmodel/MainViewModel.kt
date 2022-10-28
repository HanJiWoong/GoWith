package com.mtis.gowith.viewmodel

import android.content.Context
import android.location.Location
import android.net.Uri
import android.nfc.NfcAdapter
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.gson.Gson
import com.mtis.gowith.base.BaseViewModel
import com.mtis.gowith.common.LocationService
import com.mtis.gowith.domain.interactors.UpdateRealTimeLocation
import com.mtis.gowith.domain.model.ImageFile
import com.mtis.gowith.domain.model.webinterface.call.CommonInterface
import com.mtis.gowith.domain.model.webinterface.call.StateInterface
import com.mtis.gowith.domain.observers.ObserveRealTimeLocation
import com.mtis.gowith.widget.utils.webview.jsbridge.OnBridgeCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val updateRealTimeLocation: UpdateRealTimeLocation,
    private val observeRealTimeLocation: ObserveRealTimeLocation
) : BaseViewModel() {


    val realTimeLocation: StateFlow<Location?> = observeRealTimeLocation(Unit)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    fun startRealTimeLocationCheck() {
        LocationService.startLocationUpdates(mLocationCallback)
    }

    fun stopRealTimeLocationCheck() {
        LocationService.stopLocationUpdate(mLocationCallback)
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation
            updateRealTimeLocation(UpdateRealTimeLocation.Params(locationResult.lastLocation!!))
        }
    }


    fun checkNFCState(context: Context):Boolean {
        val nfcAdapter: NfcAdapter = NfcAdapter.getDefaultAdapter(context)

        return nfcAdapter.isEnabled
    }
}