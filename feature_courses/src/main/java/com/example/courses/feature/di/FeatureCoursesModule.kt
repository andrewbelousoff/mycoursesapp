package com.example.courses.feature.di

import com.example.courses.feature.data.repository.CoursesRepositoryImpl
import com.example.courses.core.domain.repository.CoursesRepository
import com.example.courses.feature.presentation.viewmodel.CoursesViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val featureCoursesModule = module {
    // Используем androidApplication(), чтобы репозиторий получил вечный, безопасный контекст для Room
    single<CoursesRepository> { 
        CoursesRepositoryImpl(context = androidApplication()) 
    }
    
    // Koin сам возьмет созданный выше репозиторий через метод get() и передаст его во ViewModel
    viewModel { 
        CoursesViewModel(repository = get()) 
    }
}
