package com.example.courses

import android.app.Application
import com.example.courses.feature.di.featureCoursesModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Железобетонный старт: Koin и контекст инициализируются в первую секунду жизни процесса
        startKoin {
            androidContext(this@MyApplication)
            modules(featureCoursesModule)
        }
    }
}
