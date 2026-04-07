package com.locapin.mobile.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface AttractionApiService {
    @GET("destinations")
    suspend fun getAttractions(
        @Query("q") query: String? = null,
        @Query("category") categoryId: String? = null,
        @Query("sort") sort: String? = null,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20,
        @Query("lat") lat: Double? = null,
        @Query("lng") lng: Double? = null
    ): ApiEnvelope<List<DestinationDto>>

    @GET("destinations/{id}")
    suspend fun getAttractionDetail(@Path("id") id: String): ApiEnvelope<DestinationDto>

    @POST("admin/attractions")
    suspend fun createAttraction(@Body request: AdminAttractionRequest): ApiEnvelope<DestinationDto>

    @PUT("admin/attractions/{id}")
    suspend fun updateAttraction(@Path("id") id: String, @Body request: AdminAttractionRequest): ApiEnvelope<DestinationDto>

    @DELETE("admin/attractions/{id}")
    suspend fun deleteAttraction(@Path("id") id: String): ApiEnvelope<Unit>
}
