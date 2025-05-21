//package com.example.data.di
//
//import android.content.Context
//import com.example.data.local.HabitDatabase
//import com.example.data.remote.HabitApi
//import com.example.data.repository.HabitRepositoryImpl
//import com.example.data.source.LocalHabitDataSource
//import com.example.data.source.RemoteHabitDataSource
//import com.example.domain.repository.HabitRepository
//import com.example.data.BuildConfig
//import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.SupervisorJob
//import kotlinx.serialization.json.Json
//import okhttp3.MediaType.Companion.toMediaType
//import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
//import retrofit2.Retrofit
//import java.util.concurrent.TimeUnit
//import javax.inject.Singleton
//
//@Module
//@InstallIn(SingletonComponent::class)
//object DataModule {
//
//    @Provides
//    @Singleton
//    fun provideApplicationScope(): CoroutineScope {
//        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
//    }
//
//    @Provides
//    @Singleton
//    fun provideOkHttpClient(): OkHttpClient {
//        val loggingInterceptor = HttpLoggingInterceptor()
//        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
//
//        return OkHttpClient.Builder()
//            .addInterceptor(loggingInterceptor)
//            .connectTimeout(30, TimeUnit.SECONDS)
//            .readTimeout(30, TimeUnit.SECONDS)
//            .writeTimeout(30, TimeUnit.SECONDS)
//            .build()
//    }
//
//    @Provides
//    @Singleton
//    fun provideJson(): Json {
//        return Json {
//            ignoreUnknownKeys = true
//            coerceInputValues = true
//        }
//    }
//
//    @Provides
//    @Singleton
//    fun provideHabitApi(okHttpClient: OkHttpClient, json: Json): HabitApi {
//        return Retrofit.Builder()
//            .baseUrl(BuildConfig.BASE_URL)
//            .client(okHttpClient)
//            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
//            .build()
//            .create(HabitApi::class.java)
//    }
//
//    @Provides
//    @Singleton
//    fun provideHabitDatabase(@ApplicationContext context: Context): HabitDatabase {
//        return HabitDatabase.getDatabase(context)
//    }
//
//    @Provides
//    @Singleton
//    fun provideLocalHabitDataSource(database: HabitDatabase): LocalHabitDataSource {
//        return LocalHabitDataSource(database.habitDao())
//    }
//
//    @Provides
//    @Singleton
//    fun provideRemoteHabitDataSource(habitApi: HabitApi): RemoteHabitDataSource {
//        return RemoteHabitDataSource(habitApi)
//    }
//
//    @Provides
//    @Singleton
//    fun provideHabitRepository(
//        localDataSource: LocalHabitDataSource,
//        remoteDataSource: RemoteHabitDataSource,
//        applicationScope: CoroutineScope
//    ): HabitRepository {
//        return HabitRepositoryImpl(localDataSource, remoteDataSource, applicationScope)
//    }
//}