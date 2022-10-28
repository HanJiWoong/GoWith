package com.mtis.gowith.common

import android.util.Log
import com.google.gson.Gson

object GsonConverter {
    // String to String
    fun resultJson(msg:String):String {
        val jsonStr = Gson().toJson(msg)

        Log.e("JsonStr",jsonStr)

        return jsonStr
    }
}