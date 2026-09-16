package com.example.newsagreggator.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news_sync_metadata")
data class NewsSyncMetadataEntity(
    @PrimaryKey val id: Int = SINGLETON_ID,
    val lastSuccessfulRefreshEpochMillis: Long,
) {
    companion object {
        const val SINGLETON_ID = 1
    }
}
