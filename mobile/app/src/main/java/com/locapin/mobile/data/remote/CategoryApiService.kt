package com.locapin.mobile.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CategoryApiService {
    @GET("categories")
    suspend fun getCategories(): ApiEnvelope<List<CategoryDto>>

    @POST("admin/categories")
    suspend fun createCategory(@Body request: AdminCategoryRequest): ApiEnvelope<CategoryDto>

    @PUT("admin/categories/{id}")
    suspend fun updateCategory(@Path("id") id: String, @Body request: AdminCategoryRequest): ApiEnvelope<CategoryDto>

    @DELETE("admin/categories/{id}")
    suspend fun deleteCategory(@Path("id") id: String): ApiEnvelope<Unit>
}
