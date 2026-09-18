package com.example.newsagreggator.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.newsagreggator.data.room.dao.ArticleDao
import com.example.newsagreggator.data.room.dao.ArticleStateDao
import com.example.newsagreggator.data.room.entity.ArticleEntity
import com.example.newsagreggator.data.room.entity.ArticleStateEntity
import com.example.newsagreggator.data.room.entity.NewsSyncMetadataEntity
import com.example.newsagreggator.util.SerbianTextNormalizer

@Database(
    entities = [
        ArticleEntity::class,
        ArticleStateEntity::class,
        NewsSyncMetadataEntity::class,
    ],
    version = 5,
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
            .addMigrations(
                MIGRATION_1_2,
                MIGRATION_2_3,
                MIGRATION_3_4,
                MIGRATION_4_5,
            )
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

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    ALTER TABLE articles
                    ADD COLUMN relatedCityIds TEXT NOT NULL DEFAULT ''
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    ALTER TABLE articles
                    ADD COLUMN normalizedText TEXT NOT NULL DEFAULT ''
                    """.trimIndent()
                )

                val normalizer = SerbianTextNormalizer()
                val normalizedArticles = db.query(
                    """
                    SELECT id, title, summary, source
                    FROM articles
                    """.trimIndent()
                ).use { cursor ->
                    val idIndex = cursor.getColumnIndexOrThrow("id")
                    val titleIndex = cursor.getColumnIndexOrThrow("title")
                    val summaryIndex = cursor.getColumnIndexOrThrow("summary")
                    val sourceIndex = cursor.getColumnIndexOrThrow("source")

                    buildList {
                        while (cursor.moveToNext()) {
                            add(
                                cursor.getString(idIndex) to
                                    normalizer.normalize(
                                        buildString {
                                            append(cursor.getString(titleIndex))
                                            append(' ')
                                            append(cursor.getString(summaryIndex))
                                            append(' ')
                                            append(cursor.getString(sourceIndex))
                                        }
                                    )
                            )
                        }
                    }
                }

                normalizedArticles.forEach { (articleId, normalizedText) ->
                    db.execSQL(
                        """
                        UPDATE articles
                        SET normalizedText = ?
                        WHERE id = ?
                        """.trimIndent(),
                        arrayOf(normalizedText, articleId),
                    )
                }
            }
        }
    }
}
