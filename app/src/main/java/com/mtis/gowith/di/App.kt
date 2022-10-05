package com.mtis.gowith.di

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// 컴파일 타임 시 표준 컴포넌트 빌딩에 필요한 클래스 초기화
// Hilt를 사용하는 모든 앱은 @HiltAndroidApp이 달린 Application Class를 반드시 포함해야 한다.
@HiltAndroidApp
class App : Application() {
    companion object {
        private lateinit var application: App
        fun getInstance() : App = application
    }

    override fun onCreate() {
        super.onCreate()
        application = this
    }

}