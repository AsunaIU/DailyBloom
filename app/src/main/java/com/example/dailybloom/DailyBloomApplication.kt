package com.example.dailybloom

import android.app.Application
import android.util.Log
import com.example.dailybloom.data.local.HabitDatabase
import com.example.dailybloom.data.local.HabitRepository
import com.example.dailybloom.data.local.HabitRepositoryImpl
import com.example.dailybloom.data.remote.HabitApi
import com.example.dailybloom.data.source.LocalHabitDataSource
import com.example.dailybloom.data.source.RemoteHabitDataSource
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class DailyBloomApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @OptIn(ExperimentalSerializationApi::class)
    override fun onCreate() {
        super.onCreate()
        Log.d("DailyBloomApplication", "Application onCreate() started")

        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("OkHttp", message)
        }
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

        Log.d("DailyBloomApplication", "Building Retrofit with base URL: ${BuildConfig.BASE_URL}")
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        val habitApi: HabitApi = retrofit.create(HabitApi::class.java)
        Log.d("DailyBloomApplication", "HabitApi created")

        val database = HabitDatabase.getDatabase(this)
        Log.d("DailyBloomApplication", "Database initialized")

        val localDataSource = LocalHabitDataSource(database.habitDao())
        val remoteDataSource = RemoteHabitDataSource(habitApi)
        Log.d("DailyBloomApplication", "Data sources initialized")

        val repositoryImpl = HabitRepositoryImpl(
            localDataSource,
            remoteDataSource,
            applicationScope
        )
        Log.d("DailyBloomApplication", "RepositoryImpl created")

        HabitRepository.initialize(repositoryImpl)
        Log.d("DailyBloomApplication", "Repository initialized")
    }
}