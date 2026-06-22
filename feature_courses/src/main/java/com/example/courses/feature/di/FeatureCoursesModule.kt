package com.example.courses.feature.di

import com.example.courses.feature.data.repository.CoursesRepositoryImpl
import com.example.courses.core.domain.repository.CoursesRepository
import com.example.courses.feature.presentation.viewmodel.CoursesViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val featureCoursesModule = module {
    single<CoursesRepository> { 
        CoursesRepositoryImpl(context = androidApplication()) 
    }
    viewModel { CoursesViewModel(repository = get()) }
}

