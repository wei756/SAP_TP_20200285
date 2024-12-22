package kr.ac.kumoh.ce.s20200285.sap_tp_20200285.api

import kr.ac.kumoh.ce.s20200285.sap_tp_20200285.entities.Artist
import kr.ac.kumoh.ce.s20200285.sap_tp_20200285.entities.Schedule
import retrofit2.http.GET
import retrofit2.http.Query

interface ConcertApi {
    @GET("schedules")
    suspend fun getSchedules(
        @Query("apikey") apiKey: String = ConcertApiConfig.API_KEY,
        @Query("order") order: String = "startDate.asc",
        @Query("limit") limit: String = "200",
        @Query("offset") offset: String = "0"
    ): List<Schedule>

    @GET("schedules")
    suspend fun getSchedulesByArtist(
        @Query("apikey") apiKey: String = ConcertApiConfig.API_KEY,
        @Query("order") order: String = "startDate.asc",
        @Query("artist") artist: String,
        @Query("limit") limit: String = "100",
        @Query("offset") offset: String = "0"
    ): List<Schedule>

    @GET("schedules")
    suspend fun getSchedulesByArtists(
        @Query("apikey") apiKey: String = ConcertApiConfig.API_KEY,
        @Query("order") order: String = "startDate.asc",
        @Query("or") or: String,
        @Query("limit") limit: String = "100",
        @Query("offset") offset: String = "0"
    ): List<Schedule>

    @GET("artists")
    suspend fun getArtists(
        @Query("apikey") apiKey: String = ConcertApiConfig.API_KEY,
        @Query("order") order: String = "name.asc",
        @Query("limit") limit: String = "100",
        @Query("offset") offset: String = "0"
    ): List<Artist>

    @GET("artists")
    suspend fun getArtistsByName(
        @Query("apikey") apiKey: String = ConcertApiConfig.API_KEY,
        @Query("order") order: String = "name.asc",
        @Query("name") name: String,
        @Query("limit") limit: String = "100",
        @Query("offset") offset: String = "0"
    ): List<Artist>
}