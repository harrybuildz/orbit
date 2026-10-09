package com.orbit

import com.orbit.data.network.ArticleDto
import com.orbit.data.network.ArticlesResponseDto
import com.orbit.data.network.SpaceflightApi

/**
 * In-memory [SpaceflightApi] serving slices of a fixed article list, with an
 * optional error to simulate network failures.
 */
class FakeSpaceflightApi(
    private val articles: List<ArticleDto> = emptyList(),
    private val error: Throwable? = null,
) : SpaceflightApi {

    var lastSearch: String? = null
        private set

    override suspend fun getArticles(limit: Int, offset: Int, search: String?): ArticlesResponseDto {
        error?.let { throw it }
        lastSearch = search
        val slice = articles.drop(offset).take(limit)
        val hasMore = offset + slice.size < articles.size
        return ArticlesResponseDto(
            count = articles.size,
            next = if (hasMore) "https://api.spaceflightnewsapi.net/v4/articles/?offset=${offset + limit}" else null,
            previous = null,
            results = slice,
        )
    }
}

fun articleDto(id: Long): ArticleDto = ArticleDto(
    id = id,
    title = "Article $id",
    url = "https://example.com/articles/$id",
    imageUrl = "https://example.com/images/$id.jpg",
    newsSite = "Example News",
    summary = "Summary $id",
    publishedAt = "2026-01-01T00:00:00Z",
)
