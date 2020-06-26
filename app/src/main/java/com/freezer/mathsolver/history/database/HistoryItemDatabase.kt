package com.freezer.mathsolver.history.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(HistoryItemEntity::class), version = 1, exportSchema = false)
abstract class HistoryItemDatabase : RoomDatabase(){
    abstract fun historyItemDao() : HistoryItemDao

    companion object {
        @Volatile
        private var INSTACE: HistoryItemDatabase? = null
        fun getDatabase(context : Context) : HistoryItemDatabase {
            val tempInstance = INSTACE
            if(tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, HistoryItemDatabase::class.java, "history_table").build()
                INSTACE = instance
                return instance
            }
        }
    }
}