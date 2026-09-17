package com.example.newsagreggator.presentation.speech

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Locale

enum class SpeechActionResult {
    Started,
    Stopped,
    Initializing,
    Unavailable,
    Failed,
}

data class SpeechArticle(
    val articleId: String,
    val text: String,
)

class ArticleSpeechController(context: Context) : TextToSpeech.OnInitListener {
    private val mainHandler = Handler(Looper.getMainLooper())
    private var textToSpeech: TextToSpeech? = null
    private var ready = false
    private var unavailable = false
    private val activeUtteranceIds = mutableSetOf<String>()
    private val articleIdsByUtterance = mutableMapOf<String, String>()

    var speakingArticleId by mutableStateOf<String?>(null)
        private set

    var isDigestSpeaking by mutableStateOf(false)
        private set

    init {
        textToSpeech = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        val engine = textToSpeech ?: return
        if (status != TextToSpeech.SUCCESS) {
            unavailable = true
            return
        }

        val languageResult = engine.setLanguage(Locale.forLanguageTag("sr-Latn-RS"))
        if (
            languageResult == TextToSpeech.LANG_MISSING_DATA ||
            languageResult == TextToSpeech.LANG_NOT_SUPPORTED
        ) {
            unavailable = true
            return
        }

        engine.setOnUtteranceProgressListener(
            object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    mainHandler.post {
                        articleIdsByUtterance[utteranceId]?.let { articleId ->
                            speakingArticleId = articleId
                        }
                    }
                }

                override fun onDone(utteranceId: String?) {
                    completeUtterance(utteranceId)
                }

                @Deprecated("Deprecated in Android")
                override fun onError(utteranceId: String?) {
                    completeUtterance(utteranceId)
                }
            }
        )
        ready = true
    }

    fun toggleArticle(
        articleId: String,
        text: String,
    ): SpeechActionResult {
        if (unavailable) return SpeechActionResult.Unavailable
        if (!ready) return SpeechActionResult.Initializing

        val engine = textToSpeech ?: return SpeechActionResult.Failed
        if (speakingArticleId == articleId) {
            stopPlayback(engine)
            return SpeechActionResult.Stopped
        }

        stopPlayback(engine)
        val utteranceId = "article-$articleId-${System.nanoTime()}"
        val result = engine.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            utteranceId,
        )
        if (result == TextToSpeech.ERROR) {
            clearPlaybackState()
            return SpeechActionResult.Failed
        }

        activeUtteranceIds += utteranceId
        articleIdsByUtterance[utteranceId] = articleId
        speakingArticleId = articleId
        return SpeechActionResult.Started
    }

    fun toggleDigest(articles: List<SpeechArticle>): SpeechActionResult {
        if (unavailable) return SpeechActionResult.Unavailable
        if (!ready) return SpeechActionResult.Initializing

        val engine = textToSpeech ?: return SpeechActionResult.Failed
        if (isDigestSpeaking) {
            stopPlayback(engine)
            return SpeechActionResult.Stopped
        }
        if (articles.isEmpty()) return SpeechActionResult.Failed

        stopPlayback(engine)
        articles.forEachIndexed { index, article ->
            val utteranceId =
                "digest-${article.articleId}-$index-${System.nanoTime()}"
            val result = engine.speak(
                article.text,
                if (index == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD,
                null,
                utteranceId,
            )
            if (result == TextToSpeech.ERROR) {
                stopPlayback(engine)
                return SpeechActionResult.Failed
            }
            activeUtteranceIds += utteranceId
            articleIdsByUtterance[utteranceId] = article.articleId
        }

        isDigestSpeaking = true
        speakingArticleId = articles.first().articleId
        return SpeechActionResult.Started
    }

    fun shutdown() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        clearPlaybackState()
        ready = false
    }

    private fun completeUtterance(utteranceId: String?) {
        mainHandler.post {
            if (utteranceId == null || utteranceId !in activeUtteranceIds) {
                return@post
            }
            activeUtteranceIds -= utteranceId
            articleIdsByUtterance.remove(utteranceId)
            if (activeUtteranceIds.isEmpty()) {
                clearPlaybackState()
            }
        }
    }

    private fun stopPlayback(engine: TextToSpeech) {
        engine.stop()
        clearPlaybackState()
    }

    private fun clearPlaybackState() {
        activeUtteranceIds.clear()
        articleIdsByUtterance.clear()
        speakingArticleId = null
        isDigestSpeaking = false
    }
}
