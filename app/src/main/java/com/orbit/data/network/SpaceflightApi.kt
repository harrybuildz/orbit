package com.orbit.data.network

import retrofit2.http.GET
import retrofit2.http.Query

/** Spaceflight News API v4 (no authentication required). */
interface SpaceflightApi {

    @GET("v4/articles/")
    suspend fun getArticles(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("search") search: String? = null,
    ): ArticlesResponseDto

    companion object {
        const val BASE_URL = "https://api.spaceflightnewsapi.net/"
    }
}
