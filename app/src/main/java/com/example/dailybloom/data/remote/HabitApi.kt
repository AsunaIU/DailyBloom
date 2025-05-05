package com.example.dailybloom.data.remote

import android.util.Log
import com.example.dailybloom.BuildConfig
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT

interface HabitApi {
    @GET("habit")
    suspend fun getHabits(
        @Header("Authorization") token: String = BuildConfig.API_TOKEN
    ): List<HabitResponse>

    @PUT("habit")
    suspend fun addOrUpdateHabit(
        @Header("Authorization") token: String = BuildConfig.API_TOKEN,
        @Body habit: HabitResponse
    ): HabitResponse

    @DELETE("habit")
    suspend fun deleteHabit(
        @Header("Authorization") token: String = BuildConfig.API_TOKEN,
        @Body habitUid: String
    )
}
//    companion object {
//        // В продакшн‑коде за создание и предоставление таких экземпляров обычно отвечает DI‑контейнер
//        // (например, Dagger/Hilt, Koin), а не ручные вызовы MyApi.create()
//
//        @OptIn(ExperimentalSerializationApi::class)
//        fun create(): HabitApi {
//
//            val convertType = "application/json".toMediaType()
//            val converterFactory = Json.asConverterFactory(convertType)
//
//            val loggingInterceptor = HttpLoggingInterceptor { message -> Log.d("Okhttp", message) }
//            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
//
//            val okhttp = OkHttpClient.Builder()
//                .addNetworkInterceptor(loggingInterceptor)
//                .build()
//
//            val retrofit: Retrofit = Retrofit.Builder()
//                .baseUrl(BuildConfig.BASE_URL)
//                .addConverterFactory(converterFactory)
//                .client(okhttp)
//                .build()
//
//            val habitApi = retrofit.create(HabitApi::class.java)
//            return habitApi
//        }
