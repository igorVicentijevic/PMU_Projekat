package com.example.newsagreggator.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        ArticleEntity::class,
        ArticleStateEntity::class,
        NewsSyncMetadataEntity::class,
    ],
    version = 3,
    exportSchema = true,
)
abstract class TokDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao

    abstract fun articleStateDao(): ArticleStateDao

    companion object {
        fun create(context: Context): TokDatabase = Room.databaseBuilder(
            context.applicationContext,
            TokDatabase::class.java,
            "tok.db",
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
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

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS articles (
                        id TEXT NOT NULL,
                        title TEXT NOT NULL,
                        summary TEXT NOT NULL,
                        source TEXT NOT NULL,
                        category TEXT NOT NULL,
                        publishedAtEpochMillis INTEGER NOT NULL,
                        imageUrl TEXT,
                        articleUrl TEXT NOT NULL,
                        PRIMARY KEY(id)
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS news_sync_metadata (
                        id INTEGER NOT NULL,
                        lastSuccessfulRefreshEpochMillis INTEGER NOT NULL,
                        PRIMARY KEY(id)
                    )
                    """.trimIndent()
                )
            }
        }
    }
}
