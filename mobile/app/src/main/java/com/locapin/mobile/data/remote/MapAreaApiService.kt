package com.locapin.mobile.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MapAreaApiService {
    @GET("map/areas")
    suspend fun getMapAreas(): ApiEnvelope<List<MapZoneDto>>

    @POST("admin/map-areas")
    suspend fun createMapArea(@Body request: AdminMapAreaRequest): ApiEnvelope<MapZoneDto>

    @PUT("admin/map-areas/{id}")
    suspend fun updateMapArea(@Path("id") id: String, @Body request: AdminMapAreaRequest): ApiEnvelope<MapZoneDto>

    @DELETE("admin/map-areas/{id}")
    suspend fun deleteMapArea(@Path("id") id: String): ApiEnvelope<Unit>
}
