package kr.ac.kumoh.ce.s20200285.sap_tp_20200285.api

import kr.ac.kumoh.ce.s20200285.sap_tp_20200285.entities.Schedule
import retrofit2.http.GET
import retrofit2.http.Query

interface ConcertApi {
    @GET("schedules")
    suspend fun getSchedules(
        @Query("apikey") apiKey: String = ConcertApiConfig.API_KEY
    ): List<Schedule>
}