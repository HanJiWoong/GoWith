package com.mtis.gowith.di

import android.app.Application
import android.content.Context
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.mtis.gowith.widget.utils.Utils
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

    override fun onCreate() {
        super.onCreate()
        application = this

//        Utils.clearApplicationData(this)
        initFirebase()
    }

    private fun initFirebase() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if(task.isSuccessful) {
                Log.e("Application","token : ${task.result}")
            }
        }

        /*
         * Jiny
         * 주석 처리된 것이 기존 코드이며, "everyone" 토픽을 갖도록 코드 보완했습니다.
         */
//        Firebase.messaging.subscribeToTopic("weather").addOnCompleteListener { task ->
//        }
        Firebase.messaging.subscribeToTopic("everyone")
            .addOnCompleteListener { task ->
                var msg = "Subscribed"
                if (!task.isSuccessful) {
                    msg = "Subscribe failed"
                }
                Log.d("subscribeToTopic", msg)
            }
    }



}