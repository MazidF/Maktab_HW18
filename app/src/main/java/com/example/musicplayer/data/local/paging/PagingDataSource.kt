package com.example.musicplayer.data.local.paging

import androidx.paging.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

abstract class PagingDataSource<T : Any>(
    private val perPage: Int
) : PagingSource<Int, T>() {

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return with(state) {
            val anchor = anchorPosition?.let {
                closestPageToPosition(it)
            }
            anchor?.run {
                prevKey?.plus(1) ?: nextKey?.minus(1)
            }
        }
    }

    protected fun prevKey(key: Int): Int? {
        return if (key <= 1) {
            null
        } else {
            key - 1
        }
    }

    protected fun nextKey(key: Int): Int? {
        return key + 1
    }

    abstract suspend fun produceData(page: Int, perPage: Int): List<T>

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val page = params.key ?: 1
        return try {
            val data = produceData(page, perPage) /*dao.getItem((page - 1) * perPage, perPage)*/
            LoadResult.Page(
                data = data,
                prevKey = prevKey(page),
                nextKey = nextKey(page)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    fun toFlow(scope: CoroutineScope, config: PagingConfig): Flow<PagingData<T>> {
        return Pager(config) {
            this
        }.flow.cachedIn(scope)
    }
}
