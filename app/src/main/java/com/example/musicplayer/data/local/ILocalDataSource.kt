package com.example.musicplayer.data.local

import com.example.musicplayer.data.local.db.IDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

abstract class ILocalDataSource<Item, PrimaryKey>(
    private val dao: IDao<Item, PrimaryKey>
) {

    suspend fun insertItems(vararg items: Item): List<Long> {
        return dao.insertItems(*items)
    }

    suspend fun deleteItems(vararg items: Item): Int {
        return dao.deleteItems(*items)
    }

    suspend fun updateItems(vararg items: Item): Int {
        return dao.updateItems(*items)
    }

    suspend fun get(primaryKey: PrimaryKey): Item? {
        return dao.get(primaryKey).first()
    }

    fun getItems(): Flow<List<Item>> {
        return dao.getItems()
    }

    suspend fun getCount(): Int {
        return dao.getCount()
    }

    abstract fun <T> search(query: T): Flow<Item>?
}
