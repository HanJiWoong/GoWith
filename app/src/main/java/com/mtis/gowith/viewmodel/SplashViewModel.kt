package com.mtis.gowith.viewmodel

import android.os.Build
import androidx.lifecycle.LiveData
import com.mtis.gowith.base.BaseViewModel
import com.mtis.gowith.widget.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor():BaseViewModel() {
    val permissionString: LiveData<String> get() = _perStr
    private val _perStr = SingleLiveEvent<String>()

    private var permissionIter:Int = -1

    // 추가할 권한들
    private var permissionList = arrayListOf<String>(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,          // 사진 액세스
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,         // 사진 액세스
        android.Manifest.permission.CAMERA,                         // 사진 액세스
        android.Manifest.permission.ACCESS_FINE_LOCATION,           // 위치 정보
        android.Manifest.permission.ACCESS_COARSE_LOCATION,         // 위치 정보
//        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,     // 위치 정보 백그라운드 서비스
        android.Manifest.permission.NFC,                            // NFC
//        android.Manifest.permission.BLUETOOTH,                      // 블루투스
//        android.Manifest.permission.BLUETOOTH_ADMIN,
        android.Manifest.permission.READ_CONTACTS                   // 주소록
    )

    fun startCheckPermissions() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionList.add(android.Manifest.permission.POST_NOTIFICATIONS) // API 33 버전 이상일 경우 푸시 알림 권한 추가
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionList.add(android.Manifest.permission.BLUETOOTH_CONNECT)
            permissionList.add(android.Manifest.permission.BLUETOOTH_SCAN)
            permissionList.add(android.Manifest.permission.BLUETOOTH_ADVERTISE)
        }

        permissionIter = 0
        _perStr.value = permissionList.get(permissionIter)
    }

    /**
     * 종료일 경우 false 반환
     */
    fun nextCheckPermissions():Boolean {
        permissionIter += 1

        if(permissionList.size > permissionIter) {
            _perStr.value = permissionList.get(permissionIter)
            return true
        } else {
            return false
        }
        return false
    }
}