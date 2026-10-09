package com.orbit.data.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.orbit.model.Article

/**
 * Offset-based [PagingSource] over the Spaceflight News API.
 *
 * The load key is the article offset: page N starts at `offset = key`, and the
 * next key is `offset + results.size`. The API signals the end of the list with
 * either a short page or a null `next` link.
 */
class ArticlesPagingSource(
    private val api: SpaceflightApi,
    private val query: String?,
) : PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val offset = params.key ?: 0
        return try {
            val response = api.getArticles(
                limit = params.loadSize,
                offset = offset,
                search = query?.takeIf { it.isNotBlank() },
            )
            val articles = response.results.map { it.toDomain() }
            val endReached = articles.size < params.loadSize || response.next == null
            LoadResult.Page(
                data = articles,
                prevKey = if (offset == 0) null else (offset - params.loadSize).coerceAtLeast(0),
                nextKey = if (endReached) null else offset + articles.size,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        val anchor = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchor) ?: return null
        return page.prevKey?.plus(state.config.pageSize)
            ?: page.nextKey?.minus(state.config.pageSize)
    }
}
