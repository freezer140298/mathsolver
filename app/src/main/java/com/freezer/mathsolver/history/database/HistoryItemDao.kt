package com.freezer.mathsolver.history.database

import androidx.room.*

@Dao
interface HistoryItemDao {
    @Query("SELECT * FROM history_table ORDER BY id DESC LIMIT 10 OFFSET :page")
    suspend fun getHistoryItems(page : Int) : List<HistoryItemEntity>

    @Query("INSERT INTO history_table(item, timestamp) VALUES(:item, :timestamp)")
    suspend fun insertHistoryItem(item : String, timestamp : String)

    @Query("SELECT * FROM history_table ORDER BY id DESC LIMIT 1")
    suspend fun getLatestHistoryItem() : HistoryItemEntity

    @Delete()
    suspend fun deleteHistoryItem(historyItemEntity: HistoryItemEntity)
}