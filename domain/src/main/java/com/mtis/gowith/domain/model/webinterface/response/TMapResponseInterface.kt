package com.mtis.gowith.domain.model.webinterface.response

import com.mtis.gowith.domain.model.TMapLaunch

data class TMapResponseInterface(
    val callbackId:String,
    val data:TMapLaunch
)
