package com.orbit.data.network

import com.orbit.model.Article
import java.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArticlesResponseDto(
    val count: Int = 0,
    val next: String? = null,
    val previous: String? = null,
    val results: List<ArticleDto> = emptyList(),
)

@Serializable
data class ArticleDto(
    val id: Long = 0,
    val title: String = "",
    val url: String = "",
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("news_site") val newsSite: String = "",
    val summary: String = "",
    @SerialName("published_at") val publishedAt: String = "",
)

fun ArticleDto.toDomain(): Article = Article(
    id = id,
    title = title,
    url = url,
    imageUrl = imageUrl?.takeIf { it.isNotBlank() },
    newsSite = newsSite,
    summary = summary,
    publishedAt = runCatching { Instant.parse(publishedAt) }.getOrDefault(Instant.EPOCH),
)
