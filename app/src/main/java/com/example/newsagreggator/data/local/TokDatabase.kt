package com.example.newsagreggator.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ArticleStateEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class TokDatabase : RoomDatabase() {
    abstract fun articleStateDao(): ArticleStateDao

    companion object {
        fun create(context: Context): TokDatabase = Room.databaseBuilder(
            context.applicationContext,
            TokDatabase::class.java,
            "tok.db",
        ).build()
    }
}
