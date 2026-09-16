package com.example.newsagreggator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.newsagreggator.data.RssNewsRepository
import com.example.newsagreggator.data.local.RoomArticleStateRepository
import com.example.newsagreggator.data.local.TokDatabase
import com.example.newsagreggator.data.preferences.DataStoreUserPreferencesRepository
import com.example.newsagreggator.data.remote.RtsNewsService
import com.example.newsagreggator.data.remote.RtsRssNewsDataSource
import com.example.newsagreggator.ui.elements.TokApp
import com.example.newsagreggator.ui.theme.NewsAgreggatorTheme
import com.example.newsagreggator.ui.viewmodel.TokViewModel
import com.example.newsagreggator.ui.viewmodel.tokViewModelFactory
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModelFactory = remember {
                val database = TokDatabase.create(applicationContext)
                val newsService = Retrofit.Builder()
                    .baseUrl("https://www.rts.rs/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build()
                    .create<RtsNewsService>()
                tokViewModelFactory(
                    newsRepository = RssNewsRepository(
                        remoteNewsDataSource = RtsRssNewsDataSource(newsService)
                    ),
                    userPreferencesRepository =
                        DataStoreUserPreferencesRepository(applicationContext),
                    articleStateRepository =
                        RoomArticleStateRepository(database.articleStateDao()),
                )
            }
            val tokViewModel: TokViewModel = viewModel(
                factory = viewModelFactory
            )
            val uiState by tokViewModel.uiState.collectAsStateWithLifecycle()
            val systemDarkTheme = isSystemInDarkTheme()
            val darkTheme = uiState.darkThemeOverride ?: systemDarkTheme

            NewsAgreggatorTheme(darkTheme = darkTheme) {
                TokApp(
                    darkTheme = darkTheme,
                    tokViewModel = tokViewModel,
                )
            }
        }
    }
}
