package com.orbit

import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import com.orbit.data.network.ArticlesPagingSource
import com.orbit.model.Article
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ArticlesPagingSourceTest {

    private val pageSize = 20

    private fun refreshParams(): LoadParams<Int> =
        LoadParams.Refresh(key = null, loadSize = pageSize, placeholdersEnabled = false)

    private fun appendParams(offset: Int): LoadParams<Int> =
        LoadParams.Append(key = offset, loadSize = pageSize, placeholdersEnabled = false)

    @Test
    fun `first page load returns data and correct nextKey`() = runTest {
        val api = FakeSpaceflightApi(articles = (1L..50L).map(::articleDto))
        val source = ArticlesPagingSource(api, query = null)

        val result = source.load(refreshParams())

        val page = result as LoadResult.Page<Int, Article>
        assertEquals(pageSize, page.data.size)
        assertEquals("Article 1", page.data.first().title)
        assertNull(page.prevKey)
        assertEquals(pageSize, page.nextKey)
    }

    @Test
    fun `last page returns null nextKey`() = runTest {
        val api = FakeSpaceflightApi(articles = (1L..50L).map(::articleDto))
        val source = ArticlesPagingSource(api, query = null)

        val result = source.load(appendParams(offset = 40))

        val page = result as LoadResult.Page<Int, Article>
        assertEquals(10, page.data.size)
        assertNull(page.nextKey)
    }

    @Test
    fun `exact final page with null next link returns null nextKey`() = runTest {
        val api = FakeSpaceflightApi(articles = (1L..40L).map(::articleDto))
        val source = ArticlesPagingSource(api, query = null)

        val result = source.load(appendParams(offset = 20))

        val page = result as LoadResult.Page<Int, Article>
        assertEquals(pageSize, page.data.size)
        assertNull(page.nextKey)
    }

    @Test
    fun `api exception maps to LoadResult Error`() = runTest {
        val boom = IOException("network down")
        val api = FakeSpaceflightApi(error = boom)
        val source = ArticlesPagingSource(api, query = null)

        val result = source.load(refreshParams())

        assertTrue(result is LoadResult.Error)
        assertEquals(boom, (result as LoadResult.Error).throwable)
    }

    @Test
    fun `query is forwarded to the api`() = runTest {
        val api = FakeSpaceflightApi(articles = (1L..5L).map(::articleDto))
        val source = ArticlesPagingSource(api, query = "mars")

        source.load(refreshParams())

        assertEquals("mars", api.lastSearch)
    }
}
