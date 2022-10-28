package com.mtis.gowith.domain.model.webinterface.call

data class VersionInfoInterface(
    val osType:String,
    val appVersionCode:String,
    val appVersionName:String,
    val splashID:String,
    val fcmToken:String
)
