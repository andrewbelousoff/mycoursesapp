package com.example.courses

import android.app.Application
import com.example.courses.feature.di.featureCoursesModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Жесткая и каноничная синхронизация Koin по ТЗ
        startKoin {
            androidContext(this@MyApplication)
            modules(featureCoursesModule)
        }
    }
}
