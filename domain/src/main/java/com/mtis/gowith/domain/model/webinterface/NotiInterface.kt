package com.mtis.gowith.domain.model.webinterface

import com.google.gson.annotations.SerializedName

data class NotiInterface(
    @SerializedName("alaram_type")
    val alaramType:String,
    @SerializedName("location")
    val location:String,
    @SerializedName("member_id")
    var memberId:String? = null,
    @SerializedName("member_name")
    var memberName:String? = null,
    @SerializedName("shared_maneger_id")
    var sharedManagerId:String? = null,
    @SerializedName("shared_maneger_name")
    var sharedManagerName:String? = null
)
