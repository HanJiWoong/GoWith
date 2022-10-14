package com.mtis.gowith.domain.model

data class VersionInfo(
    val osType:String,
    val appVersionCode:String,
    val appVersionName:String,
    val splashID:String,
    val fcmToken:String
)
