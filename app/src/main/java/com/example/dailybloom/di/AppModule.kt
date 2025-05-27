package com.example.dailybloom.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module(includes = [
    com.example.data.di.DataModule::class,
])

@InstallIn(SingletonComponent::class)
object AppModule {
    // Пустой модуль: не содержит собственных провайдеров, только включает другие модули через includes
}