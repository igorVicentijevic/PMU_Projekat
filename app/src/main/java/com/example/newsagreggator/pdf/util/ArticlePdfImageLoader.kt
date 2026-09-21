package com.example.newsagreggator.pdf.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

class ArticlePdfImageLoader @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    suspend fun load(
        imageUrl: String?,
        @DrawableRes fallbackResId: Int,
    ): Bitmap? = withContext(Dispatchers.IO) {
        imageUrl
            ?.takeIf(String::isNotBlank)
            ?.let(::loadRemoteImage)
            ?: loadFallbackImage(fallbackResId)
    }

    private fun loadRemoteImage(imageUrl: String): Bitmap? {
        val connection = try {
            URL(imageUrl).openConnection() as? HttpURLConnection
        } catch (_: IOException) {
            null
        } ?: return null

        return try {
            connection.connectTimeout = CONNECT_TIMEOUT_MILLIS
            connection.readTimeout = READ_TIMEOUT_MILLIS
            connection.instanceFollowRedirects = true
            connection.setRequestProperty("User-Agent", USER_AGENT)
            connection.inputStream.use(BitmapFactory::decodeStream)
        } catch (_: IOException) {
            null
        } catch (_: SecurityException) {
            null
        } finally {
            connection.disconnect()
        }
    }

    private fun loadFallbackImage(
        @DrawableRes fallbackResId: Int,
    ): Bitmap? = ContextCompat.getDrawable(context, fallbackResId)
        ?.toBitmap()

    private companion object {
        const val CONNECT_TIMEOUT_MILLIS = 10_000
        const val READ_TIMEOUT_MILLIS = 15_000
        const val USER_AGENT = "Tok Android"
    }
}