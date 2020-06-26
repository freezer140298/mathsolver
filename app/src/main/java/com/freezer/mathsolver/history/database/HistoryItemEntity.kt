package com.freezer.mathsolver.history.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity(tableName = "history_table", indices = arrayOf(Index(value = ["id"])))
class HistoryItemEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(name = "item")
    var item: String,
    @ColumnInfo(name = "timestamp")
    var timeStamp : String
)