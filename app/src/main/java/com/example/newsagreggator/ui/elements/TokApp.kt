package com.example.newsagreggator.ui.elements

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.newsagreggator.R
import com.example.newsagreggator.speech.ArticleSpeechController
import com.example.newsagreggator.speech.SpeechArticle
import com.example.newsagreggator.speech.SpeechActionResult
import com.example.newsagreggator.ui.elements.screens.ForYouScreen
import com.example.newsagreggator.ui.elements.screens.DigestScreen
import com.example.newsagreggator.ui.elements.screens.HistoryScreen
import com.example.newsagreggator.ui.elements.screens.SavedScreen
import com.example.newsagreggator.ui.elements.screens.SettingsScreen
import com.example.newsagreggator.ui.elements.screens.TokHomeScreen
import com.example.newsagreggator.ui.model.NewsCardUiModel
import com.example.newsagreggator.ui.model.sampleNewsArticles
import com.example.newsagreggator.ui.state.SecondaryScreen
import com.example.newsagreggator.ui.theme.NewsAgreggatorTheme
import com.example.newsagreggator.ui.viewmodel.TokViewModel
import kotlinx.coroutines.launch

private enum class TokTab(
    @StringRes val labelResId: Int,
    @DrawableRes val iconResId: Int,
) {
    Home(R.string.nav_home, R.drawable.ic_home),
    ForYou(R.string.nav_for_you, R.drawable.ic_spark),
    Saved(R.string.nav_saved, R.drawable.ic_bookmark_outline),
    Settings(R.string.nav_settings, R.drawable.ic_settings),
}

@Composable
fun TokApp(
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    tokViewModel: TokViewModel = viewModel(),
) {
    val uiState by tokViewModel.uiState.collectAsState()
    var selectedTab by rememberSaveable { mutableStateOf(TokTab.Home) }
    val context = LocalContext.current
    val speechController = remember(context) { ArticleSpeechController(context) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val toggleSaved: (Int) -> Unit = { articleId ->
        val wasSaved = articleId in uiState.savedArticleIds
        tokViewModel.setArticleSaved(articleId, !wasSaved)
        coroutineScope.launch {
            val result = snackbarHostState.showSnackbar(
                message = context.getString(
                    if (wasSaved) {
                        R.string.article_removed_from_saved
                    } else {
                        R.string.article_saved
                    }
                ),
                actionLabel = context.getString(R.string.undo),
                withDismissAction = true,
            )
            val expectedSavedState = !wasSaved
            if (
                result == SnackbarResult.ActionPerformed &&
                (articleId in tokViewModel.uiState.value.savedArticleIds) ==
                    expectedSavedState
            ) {
                tokViewModel.setArticleSaved(articleId, wasSaved)
            }
        }
    }
    val readArticle: (NewsCardUiModel) -> Unit = { article ->
        if (launchOriginalArticle(context, article)) {
            tokViewModel.markArticleRead(article.id)
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = context.getString(R.string.article_open_failed),
                    withDismissAction = true,
                )
            }
        }
    }
    val shareArticle: (NewsCardUiModel) -> Unit = { article ->
        launchShareChooser(context, article)
    }
    val showSpeechError: (SpeechActionResult) -> Unit = { result ->
        val messageResId = when (result) {
            SpeechActionResult.Initializing -> R.string.tts_initializing
            SpeechActionResult.Unavailable -> R.string.tts_unavailable
            else -> R.string.tts_failed
        }
        coroutineScope.launch {
            snackbarHostState.showSnackbar(
                message = context.getString(messageResId),
                withDismissAction = true,
            )
        }
    }
    val toggleSpeech: (NewsCardUiModel) -> Unit = { article ->
        val speechText = articleSpeechText(context, article)
        val speechResult = speechController.toggleArticle(article.id, speechText)
        when (speechResult) {
            SpeechActionResult.Started,
            SpeechActionResult.Stopped,
            -> Unit
            SpeechActionResult.Initializing,
            SpeechActionResult.Unavailable,
            SpeechActionResult.Failed,
            -> showSpeechError(speechResult)
        }
    }
    val toggleDigestSpeech: (List<NewsCardUiModel>) -> Unit = { articles ->
        val result = speechController.toggleDigest(
            articles.map { article ->
                SpeechArticle(
                    articleId = article.id,
                    text = articleSpeechText(context, article),
                )
            }
        )
        if (
            result != SpeechActionResult.Started &&
            result != SpeechActionResult.Stopped
        ) {
            showSpeechError(result)
        }
    }

    DisposableEffect(speechController) {
        onDispose { speechController.shutdown() }
    }
    LaunchedEffect(speechController.speakingArticleId) {
        speechController.speakingArticleId?.let { articleId ->
            tokViewModel.markArticleRead(articleId)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = androidx.compose.ui.unit.Dp.Unspecified,
            ) {
                TokTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = tab == selectedTab,
                        onClick = {
                            selectedTab = tab
                            tokViewModel.closeSecondaryScreen()
                        },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (
                                        tab == TokTab.Saved &&
                                        uiState.savedArticleIds.isNotEmpty()
                                    ) {
                                        Badge {
                                            Text(uiState.savedArticleIds.size.toString())
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    painter = painterResource(tab.iconResId),
                                    contentDescription = null,
                                )
                            }
                        },
                        label = { Text(stringResource(tab.labelResId)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor =
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        ),
                    )
                }
            }
        },
    ) { innerPadding ->
        when (uiState.secondaryScreen) {
            SecondaryScreen.History -> HistoryScreen(
                    articles = sampleNewsArticles.filter {
                        it.id in uiState.readArticleIds
                    },
                    savedArticleIds = uiState.savedArticleIds,
                    speakingArticleId = speechController.speakingArticleId,
                    compactLayout = uiState.compactLayout,
                    onBack = tokViewModel::closeSecondaryScreen,
                    onClearHistory = tokViewModel::clearReadingHistory,
                    onToggleSaved = toggleSaved,
                    onToggleSpeech = toggleSpeech,
                    onShareArticle = shareArticle,
                    modifier = Modifier.padding(innerPadding),
                )
            SecondaryScreen.Digest -> DigestScreen(
                articles = sampleNewsArticles
                    .filter { it.categoryResId in uiState.followedCategories }
                    .take(5),
                savedArticleIds = uiState.savedArticleIds,
                readArticleIds = uiState.readArticleIds,
                speakingArticleId = speechController.speakingArticleId,
                isDigestSpeaking = speechController.isDigestSpeaking,
                compactLayout = uiState.compactLayout,
                onBack = tokViewModel::closeSecondaryScreen,
                onToggleSaved = toggleSaved,
                onReadArticle = readArticle,
                onToggleSpeech = toggleSpeech,
                onToggleDigestSpeech = {
                    toggleDigestSpeech(
                        sampleNewsArticles
                            .filter {
                                it.categoryResId in uiState.followedCategories
                            }
                            .take(5)
                    )
                },
                onShareArticle = shareArticle,
                modifier = Modifier.padding(innerPadding),
            )
            null -> when (selectedTab) {
            TokTab.Home -> TokHomeScreen(
                savedArticleIds = uiState.savedArticleIds,
                readArticleIds = uiState.readArticleIds,
                speakingArticleId = speechController.speakingArticleId,
                compactLayout = uiState.compactLayout,
                onToggleSaved = toggleSaved,
                onReadArticle = readArticle,
                onToggleSpeech = toggleSpeech,
                onShareArticle = shareArticle,
                onOpenHistory = {
                    tokViewModel.openSecondaryScreen(SecondaryScreen.History)
                },
                onOpenDigest = {
                    tokViewModel.openSecondaryScreen(SecondaryScreen.Digest)
                },
                onCompactLayoutChange = tokViewModel::setCompactLayout,
                modifier = Modifier.padding(innerPadding),
            )
            TokTab.ForYou -> ForYouScreen(
                articles = sampleNewsArticles.filter {
                    it.categoryResId in uiState.followedCategories
                },
                followedCategories = uiState.followedCategories,
                savedArticleIds = uiState.savedArticleIds,
                readArticleIds = uiState.readArticleIds,
                speakingArticleId = speechController.speakingArticleId,
                compactLayout = uiState.compactLayout,
                onToggleSaved = toggleSaved,
                onReadArticle = readArticle,
                onToggleSpeech = toggleSpeech,
                onShareArticle = shareArticle,
                modifier = Modifier.padding(innerPadding),
            )
            TokTab.Saved -> SavedScreen(
                articles = sampleNewsArticles.filter {
                    it.id in uiState.savedArticleIds
                },
                readArticleIds = uiState.readArticleIds,
                speakingArticleId = speechController.speakingArticleId,
                compactLayout = uiState.compactLayout,
                onRemoveSaved = toggleSaved,
                onReadArticle = readArticle,
                onToggleSpeech = toggleSpeech,
                onShareArticle = shareArticle,
                modifier = Modifier.padding(innerPadding),
            )
            TokTab.Settings -> SettingsScreen(
                darkTheme = darkTheme,
                compactLayout = uiState.compactLayout,
                followedCategories = uiState.followedCategories,
                refreshIntervalMinutes = uiState.refreshIntervalMinutes,
                breakingNewsEnabled = uiState.breakingNewsEnabled,
                onDarkThemeChange = onDarkThemeChange,
                onCompactLayoutChange = tokViewModel::setCompactLayout,
                onRefreshIntervalChange = tokViewModel::setRefreshInterval,
                onBreakingNewsChange = tokViewModel::setBreakingNewsEnabled,
                onToggleCategory = tokViewModel::toggleFollowedCategory,
                modifier = Modifier.padding(innerPadding),
            )
            }
        }
    }
}

private fun launchOriginalArticle(
    context: Context,
    article: NewsCardUiModel,
): Boolean {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url)).apply {
        addCategory(Intent.CATEGORY_BROWSABLE)
    }
    return try {
        context.startActivity(intent)
        true
    } catch (_: ActivityNotFoundException) {
        false
    }
}

private fun articleSpeechText(
    context: Context,
    article: NewsCardUiModel,
): String = buildString {
    append(context.getString(article.titleResId))
    append(". ")
    append(context.getString(article.summaryResId))
    append(". ")
    append(context.getString(article.sourceResId))
}

private fun launchShareChooser(
    context: Context,
    article: NewsCardUiModel,
) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(
            Intent.EXTRA_TEXT,
            "${context.getString(article.titleResId)}\n${article.url}",
        )
    }
    context.startActivity(
        Intent.createChooser(
            shareIntent,
            context.getString(R.string.share_article_chooser),
        )
    )
}

@Preview(name = "Tok aplikacija", showBackground = true)
@Composable
private fun TokAppPreview() {
    NewsAgreggatorTheme(darkTheme = false) {
        TokApp(
            darkTheme = false,
            onDarkThemeChange = {},
        )
    }
}
