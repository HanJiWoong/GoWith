package com.mtis.gowith.common

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.mtis.gowith.di.App
import com.mtis.gowith.widget.utils.LocationUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*
import javax.inject.Singleton


object LocationService {
    private const val REQUEST_PERMISSION_LOCATION = 10

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private var mLocationRequest: LocationRequest = LocationRequest.create().apply {
        priority = Priority.PRIORITY_HIGH_ACCURACY
        interval = 15 * 1000L
    }
    lateinit var locationManager: LocationManager

    // 위치 정보 체크 시작
    fun startLocationUpdates(mLocationCallback: LocationCallback) {
        mFusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(App.ApplicatoinContext())

        // 기기의 위치에 관한 정기 업데이트를 요청하는 메서드 실행
        // 지정한 루퍼 스레드(Looper.myLooper())에서 콜백(mLocationCallback)으로 위치 업데이트를 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (App.ApplicatoinContext()
                    .checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            ) {
                Looper.myLooper()?.let {
                    mFusedLocationProviderClient!!.requestLocationUpdates(
                        mLocationRequest,
                        mLocationCallback,
                        it
                    )
                }
            }
        } else {
            Looper.myLooper()?.let {
                mFusedLocationProviderClient!!.requestLocationUpdates(
                    mLocationRequest,
                    mLocationCallback,
                    it
                )
            }
        }
    }

    fun stopLocationUpdate(mLocationCallback: LocationCallback) {
        mFusedLocationProviderClient?.let {
            it.removeLocationUpdates(mLocationCallback)
        }
    }

    // 액티비티에서 위치 권한 체크
    fun checkPermissionForLocationInActivity(activity: Activity): Boolean {
        // Android 6.0 Marshmallow 이상에서는 위치 권한에 추가 런타임 궈한이 필요
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (App.ApplicatoinContext()
                    .checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                // 권한이 없으므로 권한 요청 알림 보내기
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSION_LOCATION
                )
                false
            }
        } else {
            true
        }
    }

    // 프래그먼트에서 위치권한 체크
    fun checkPermissionForLocationInFragment(fragment: Fragment): Boolean {
        // Android 6.0 Marshmallow 이상에서는 위치 권한에 추가 런타임 궈한이 필요
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (App.ApplicatoinContext()
                    .checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                // 권한이 없으므로 권한 요청 알림 보내기
                fragment.requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_PERMISSION_LOCATION)
                false
            }
        } else {
            true
        }
    }

    // 현재 위치정보 체크
    fun getCurrentLocation(): Location? {
        locationManager =
            App.ApplicatoinContext().getSystemService(LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                App.ApplicatoinContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                App.ApplicatoinContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }

        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
    }

    // 위치 정보의 주소값 반환
    fun getCurrentLocationName(latitude:Double, longitude:Double):String? {
        var mGeoCorder = Geocoder(App.ApplicatoinContext(), Locale.KOREAN)
        var mResultList:List<Address>?
        var nowAddr = "현재 위치를 확인 할 수 없습니다."

        try {
            mResultList = mGeoCorder.getFromLocation(latitude, longitude, 1)
            if (mResultList != null && mResultList.isNotEmpty()) {
                nowAddr = mResultList[0].adminArea
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return nowAddr
    }


}