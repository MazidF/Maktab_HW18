package com.example.musicplayer.data.local

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
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


abstract class IDao<Item, PrimaryKey>(
    private val tableName: String
) {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertItems(vararg items: Item): List<Long>

    @Delete
    abstract suspend fun deleteItems(vararg items: Item): Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun updateItems(vararg items: Item): Int

    abstract fun get(primaryKey: PrimaryKey): Flow<Item?>

    abstract fun getItems(): Flow<List<Item>>

    abstract suspend fun getCount(): Int

    @RawQuery
    protected abstract fun search(query: SupportSQLiteQuery): Flow<Item>

    protected fun search(condition: String, args: Array<Any>? = null): Flow<Item> {
        val query = "select * from $tableName where $condition"
        return search(SimpleSQLiteQuery(query, args))
    }
}