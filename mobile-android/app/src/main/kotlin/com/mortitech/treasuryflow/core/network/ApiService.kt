package com.mortitech.treasuryflow.core.network

import com.mortitech.treasuryflow.feature.auth.data.remote.dto.*
import com.mortitech.treasuryflow.feature.example.data.remote.dto.*
import retrofit2.http.*

interface ApiService {

    // --- Auth ---

    @POST("auth/oauth/callback")
    suspend fun oauthCallback(@Body request: OAuthCallbackRequest): AuthResponse


    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse


    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): AuthResponse

    @GET("auth/me")
    suspend fun getCurrentUser(): UserResponse

    // --- Examples ---

    @GET("examples")
    suspend fun listExamples(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: String = "createdAt,desc",
    ): ExampleListResponse

    @POST("examples")
    suspend fun createExample(@Body request: CreateExampleRequest): ExampleResponse

    @GET("examples/{id}")
    suspend fun getExample(@Path("id") id: String): ExampleResponse

    @PUT("examples/{id}")
    suspend fun updateExample(
        @Path("id") id: String,
        @Body request: UpdateExampleRequest,
    ): ExampleResponse

    @DELETE("examples/{id}")
    suspend fun deleteExample(@Path("id") id: String)
}
