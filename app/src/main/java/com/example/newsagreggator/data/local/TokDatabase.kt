package com.example.newsagreggator.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [ArticleStateEntity::class],
    version = 2,
    exportSchema = true,
)
abstract class TokDatabase : RoomDatabase() {
    abstract fun articleStateDao(): ArticleStateDao

    companion object {
        fun create(context: Context): TokDatabase = Room.databaseBuilder(
            context.applicationContext,
            TokDatabase::class.java,
            "tok.db",
        )
            .addMigrations(MIGRATION_1_2)
            .build()

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE article_state RENAME TO article_state_old"
                )
                db.execSQL(
                    """
                    CREATE TABLE article_state (
                        articleId TEXT NOT NULL,
                        isSaved INTEGER NOT NULL,
                        isRead INTEGER NOT NULL,
                        PRIMARY KEY(articleId)
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO article_state (articleId, isSaved, isRead)
                    SELECT CAST(articleId AS TEXT), isSaved, isRead
                    FROM article_state_old
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE article_state_old")
            }
        }
    }
}
