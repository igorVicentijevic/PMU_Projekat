package com.example.newsagreggator.speech

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

class ArticleSpeechController(context: Context) : TextToSpeech.OnInitListener {
    private val mainHandler = Handler(Looper.getMainLooper())
    private var textToSpeech: TextToSpeech? = null
    private var ready = false
    private var unavailable = false
    private var currentUtteranceId: String? = null

    var speakingArticleId by mutableStateOf<Int?>(null)
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
                override fun onStart(utteranceId: String?) = Unit

                override fun onDone(utteranceId: String?) {
                    clearCompletedUtterance(utteranceId)
                }

                @Deprecated("Deprecated in Android")
                override fun onError(utteranceId: String?) {
                    clearCompletedUtterance(utteranceId)
                }
            }
        )
        ready = true
    }

    fun toggleArticle(
        articleId: Int,
        text: String,
    ): SpeechActionResult {
        if (unavailable) return SpeechActionResult.Unavailable
        if (!ready) return SpeechActionResult.Initializing

        val engine = textToSpeech ?: return SpeechActionResult.Failed
        if (speakingArticleId == articleId) {
            engine.stop()
            currentUtteranceId = null
            speakingArticleId = null
            return SpeechActionResult.Stopped
        }

        val utteranceId = "article-$articleId-${System.nanoTime()}"
        val result = engine.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            utteranceId,
        )
        if (result == TextToSpeech.ERROR) {
            currentUtteranceId = null
            speakingArticleId = null
            return SpeechActionResult.Failed
        }

        currentUtteranceId = utteranceId
        speakingArticleId = articleId
        return SpeechActionResult.Started
    }

    fun shutdown() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        currentUtteranceId = null
        speakingArticleId = null
        ready = false
    }

    private fun clearCompletedUtterance(utteranceId: String?) {
        if (utteranceId != currentUtteranceId) return
        mainHandler.post {
            if (utteranceId == currentUtteranceId) {
                currentUtteranceId = null
                speakingArticleId = null
            }
        }
    }
}
