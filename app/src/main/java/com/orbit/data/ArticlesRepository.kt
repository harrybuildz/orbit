package com.orbit.data

import androidx.paging.PagingData
import com.orbit.model.Article
import kotlinx.coroutines.flow.Flow

interface ArticlesRepository {
    fun articlesPagingData(query: String?): Flow<PagingData<Article>>
}
