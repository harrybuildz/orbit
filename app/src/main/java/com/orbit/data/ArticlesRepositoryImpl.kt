package com.orbit.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.orbit.data.network.ArticlesPagingSource
import com.orbit.data.network.SpaceflightApi
import com.orbit.model.Article
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class ArticlesRepositoryImpl @Inject constructor(
    private val api: SpaceflightApi,
) : ArticlesRepository {

    override fun articlesPagingData(query: String?): Flow<PagingData<Article>> =
        Pager(
            config = PagingConfig(pageSize = PAGE_SIZE),
            pagingSourceFactory = { ArticlesPagingSource(api, query) },
        ).flow

    private companion object {
        const val PAGE_SIZE = 20
    }
}
