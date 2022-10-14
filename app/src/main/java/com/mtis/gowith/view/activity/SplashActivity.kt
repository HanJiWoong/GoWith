package com.mtis.gowith.view.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseApp
import com.mtis.gowith.R
import com.mtis.gowith.base.BaseActivity
import com.mtis.gowith.databinding.ActivitySplashBinding
import com.mtis.gowith.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash) {
    private val TAG: String = "SplashActivity"
    private val REQUEST_PERMISSION = 1000

    private val splashViewModel by viewModels<SplashViewModel>()

    // 권한 요청
    private val requestMultiplePermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            results.forEach {
                if (!it.value) {
                    shortShowToast("${it.key} 권한 허용 필요")
                }
            }
        }


    override fun init() {

        splashViewModel.startCheckPermissions()

        splashViewModel.permissionString.observe(this, { permission ->
            when {
                shouldShowRequestPermissionRationale(permission) -> {

                    showPermissionContextPopup(permission)

                }
                else -> {
                    requestPermissions(
                        arrayOf(permission),
                        REQUEST_PERMISSION
                    )
                    Log.i("BUTTON", "else")
                }
            }
        })

        Log.e(TAG, "get Intent = ${intent.extras?.getString("data")}")
    }

    fun showPermissionContextPopup(permission:String) {

        var title:String = ""
        var message:String = ""

        when(permission) {
            android.Manifest.permission.READ_EXTERNAL_STORAGE -> {
                title = getString(R.string.str_permission_request_title_album)
                message = getString(R.string.str_permission_request_message_album)
            }
            android.Manifest.permission.ACCESS_COARSE_LOCATION -> {
                title = getString(R.string.str_permission_request_title_location)
                message = getString(R.string.str_permission_request_message_location)
            }
            android.Manifest.permission.ACCESS_BACKGROUND_LOCATION -> {
                title = getString(R.string.str_permission_request_title_location)
                message = getString(R.string.str_permission_request_message_location)
            }
            android.Manifest.permission.NFC-> {
                title = getString(R.string.str_permission_request_title_nfc)
                message = getString(R.string.str_permission_request_message_nfc)
            }
            android.Manifest.permission.BLUETOOTH-> {
                title = getString(R.string.str_permission_request_title_bluetooth)
                message = getString(R.string.str_permission_request_message_bluetooth)
            }
            android.Manifest.permission.BLUETOOTH_CONNECT-> {
                title = getString(R.string.str_permission_request_title_bluetooth)
                message = getString(R.string.str_permission_request_message_bluetooth)
            }
            android.Manifest.permission.BLUETOOTH_SCAN-> {
                title = getString(R.string.str_permission_request_title_bluetooth)
                message = getString(R.string.str_permission_request_message_bluetooth)
            }
            android.Manifest.permission.BLUETOOTH_ADMIN -> {
                title = getString(R.string.str_permission_request_title_bluetooth)
                title = getString(R.string.str_permission_request_message_bluetooth)
            }
            android.Manifest.permission.READ_CONTACTS -> {
                title = getString(R.string.str_permission_request_title_contact)
                message = getString(R.string.str_permission_request_message_contact)
            }
            android.Manifest.permission.POST_NOTIFICATIONS -> {
                title = getString(R.string.str_permission_request_title_notification)
                message = getString(R.string.str_permission_request_message_notification)
            }
        }

        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("동의") { _, _ ->
                requestPermissions(
                    arrayOf(permission),
                    REQUEST_PERMISSION
                )
            }
            .setNegativeButton("취소") { _, _ ->
                finish()
            }
            .create()
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!splashViewModel.nextCheckPermissions()) {
                        val intent = Intent(this@SplashActivity, MainActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_do_not_move)
                        finish()
                    }
                } else {
                    AlertDialog.Builder(this)
                        .setTitle("권한이 필요합니다.")
                        .setMessage("앱을 사용하기 위해서는 ${permissions[0]}권한이 필요합니다. 앱을 종료합니다.")
                        .setPositiveButton("확인") { _, _ ->
                            finish()
                        }
                        .create()
                        .show()
                }
            }
            else -> {

            }
        }
    }

}