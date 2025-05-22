package com.example.dailybloom

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DailyBloomApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}