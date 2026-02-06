package com.tracky.app.data.remote

import com.tracky.app.data.remote.dto.GenerateNarrativeRequest
import com.tracky.app.data.remote.dto.GenerateNarrativeResponse
import com.tracky.app.data.remote.dto.LogAutoRequest
import com.tracky.app.data.remote.dto.LogAutoResponse
import com.tracky.app.data.remote.dto.LogExerciseRequest
import com.tracky.app.data.remote.dto.LogExerciseResponse
import com.tracky.app.data.remote.dto.LogFoodRequest
import com.tracky.app.data.remote.dto.LogFoodResponse
import com.tracky.app.data.remote.dto.ResolveExerciseRequest
import com.tracky.app.data.remote.dto.ResolveExerciseResponse
import com.tracky.app.data.remote.dto.ResolveFoodRequest
import com.tracky.app.data.remote.dto.ResolveFoodResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Retrofit API for Tracky Cloudflare Worker backend
 */
interface TrackyBackendApi {

    @GET("health")
    suspend fun healthCheck(): Response<Map<String, String>>

    /**
     * Parse food from text/image via Gemini
     * Returns structured items for dataset resolution
     */
    @POST("log/food")
    suspend fun logFood(@Body request: LogFoodRequest): Response<LogFoodResponse>

    /**
     * Resolve food items to nutrition data
     * Uses local dataset first, then USDA FDC
     */
    @POST("resolve/food")
    suspend fun resolveFood(@Body request: ResolveFoodRequest): Response<ResolveFoodResponse>

    /**
     * Resolve food items via Internet search (SerpAPI)
     * Fallback if USDA fails
     */
    @POST("resolve/internet")
    suspend fun resolveInternet(@Body request: ResolveFoodRequest): Response<ResolveFoodResponse>

    /**
     * Parse exercise description via Gemini
     */
    @POST("log/exercise")
    suspend fun logExercise(@Body request: LogExerciseRequest): Response<LogExerciseResponse>

    /**
     * Auto-detect and parse food/exercise/mixed/none from text or image
     */
    @POST("log/auto")
    suspend fun logAuto(@Body request: LogAutoRequest): Response<LogAutoResponse>

    /**
     * Resolve exercise to calories using MET compendium
     */
    @POST("resolve/exercise")
    suspend fun resolveExercise(@Body request: ResolveExerciseRequest): Response<ResolveExerciseResponse>

    /**
     * Generate analysis narrative for an entry
     */
    @POST("narrative")
    suspend fun generateNarrative(@Body request: GenerateNarrativeRequest): Response<GenerateNarrativeResponse>
}
