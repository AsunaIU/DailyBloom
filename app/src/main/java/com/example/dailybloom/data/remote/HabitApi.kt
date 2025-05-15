package com.example.dailybloom.data.remote

import com.example.dailybloom.BuildConfig
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.POST
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
    ): UidResponse

    @HTTP(method = "DELETE", path = "habit", hasBody = true)
    suspend fun deleteHabit(
        @Header("Authorization") token: String = BuildConfig.API_TOKEN,
        @Body uid: UidResponse
    )
    @POST("habit_done")
    suspend fun setHabitDone(
        @Header("Authorization") token: String = BuildConfig.API_TOKEN,
        @Body habitDone: HabitDoneRequest
    ): UidResponse
}