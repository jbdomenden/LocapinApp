package com.locapin.mobile.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: AuthRequest): ApiEnvelope<AuthResponse>

    @POST("auth/social")
    suspend fun socialAuth(@Body request: SocialAuthRequest): ApiEnvelope<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiEnvelope<AuthResponse>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): ApiEnvelope<Unit>
}
