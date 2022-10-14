package com.mtis.gowith.common

import com.google.gson.Gson

object GsonConverter {
    // String to String
    fun resultJson(msg:String):String {
        return Gson().toJson(msg)
    }
}