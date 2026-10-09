package com.orbit

import androidx.paging.PagingData
import com.orbit.data.ArticlesRepository
import com.orbit.model.Article
import com.orbit.ui.FeedViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FeedViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private class FakeArticlesRepository : ArticlesRepository {
        val queries = mutableListOf<String?>()

        override fun articlesPagingData(query: String?): Flow<PagingData<Article>> {
            queries += query
            return flowOf(PagingData.empty())
        }
    }

    @Test
    fun `initial collection requests unfiltered feed`() = runTest {
        val repository = FakeArticlesRepository()
        val viewModel = FeedViewModel(repository)

        val job = launch { viewModel.articles.collect { } }
        advanceUntilIdle()

        assertEquals(listOf<String?>(null), repository.queries)
        job.cancel()
    }

    @Test
    fun `query change produces a new paging flow after debounce`() = runTest {
        val repository = FakeArticlesRepository()
        val viewModel = FeedViewModel(repository)

        val job = launch { viewModel.articles.collect { } }
        advanceUntilIdle()

        viewModel.onQueryChange("mars")
        advanceUntilIdle()

        assertEquals(listOf(null, "mars"), repository.queries)
        job.cancel()
    }

    @Test
    fun `rapid typing is debounced to the final query`() = runTest {
        val repository = FakeArticlesRepository()
        val viewModel = FeedViewModel(repository)

        val job = launch { viewModel.articles.collect { } }
        advanceUntilIdle()

        viewModel.onQueryChange("m")
        advanceTimeBy(100)
        viewModel.onQueryChange("ma")
        advanceTimeBy(100)
        viewModel.onQueryChange("mars")
        advanceUntilIdle()

        assertEquals(listOf(null, "mars"), repository.queries)
        job.cancel()
    }

    @Test
    fun `blank query maps to null for the repository`() = runTest {
        val repository = FakeArticlesRepository()
        val viewModel = FeedViewModel(repository)

        val job = launch { viewModel.articles.collect { } }
        advanceUntilIdle()

        viewModel.onQueryChange("   ")
        advanceUntilIdle()

        // "   " trims to blank -> null, same as the initial query, so no new flow.
        assertEquals(listOf<String?>(null), repository.queries)
        job.cancel()
    }
}
