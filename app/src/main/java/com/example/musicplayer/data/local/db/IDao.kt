package com.example.musicplayer.data.local.db

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

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

    abstract suspend fun getItems(from: Int, perPage: Int): List<Item>

    abstract suspend fun getCount(): Int

    @RawQuery
    protected abstract fun search(query: SupportSQLiteQuery): Flow<Item>

    protected fun search(condition: String, args: Array<Any>? = null): Flow<Item> {
        val query = "select * from $tableName where $condition"
        return search(SimpleSQLiteQuery(query, args))
    }
}