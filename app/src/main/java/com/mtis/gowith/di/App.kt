package com.mtis.gowith.di

import android.app.Application
import android.content.Context
import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.HiltAndroidApp

// 컴파일 타임 시 표준 컴포넌트 빌딩에 필요한 클래스 초기화
// Hilt를 사용하는 모든 앱은 @HiltAndroidApp이 달린 Application Class를 반드시 포함해야 한다.
@HiltAndroidApp
class App : Application() {
    companion object {
        private lateinit var application: App
        fun getInstance(): App = application
        fun ApplicatoinContext() : Context {
            return getInstance().applicationContext
        }
    }

    private var mRequestQueue: RequestQueue? = null

    override fun onCreate() {
        super.onCreate()
        application = this

        initFirebase()
    }

    private fun initFirebase() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if(task.isSuccessful) {
                Log.e("Application","token : ${task.result}")
            }
        }
        Firebase.messaging.subscribeToTopic("weather").addOnCompleteListener { task ->

        }
    }

    fun getRequestQueue(): RequestQueue? {
        if (mRequestQueue == null) mRequestQueue = Volley.newRequestQueue(applicationContext)
        return mRequestQueue
    }

}