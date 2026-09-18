package com.example.newsagreggator.ui.elements

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.newsagreggator.R
import com.example.newsagreggator.commands.DetectNearestCityCommand
import com.example.newsagreggator.commands.RefreshNewsCommand
import com.example.newsagreggator.commands.ToggleFollowedCategoryCommand
import com.example.newsagreggator.commands.UpdateRefreshIntervalCommand
import com.example.newsagreggator.data.sample.InMemoryArticleStateRepository
import com.example.newsagreggator.data.sample.InMemoryCityResolver
import com.example.newsagreggator.data.sample.InMemoryCurrentLocationProvider
import com.example.newsagreggator.data.sample.InMemoryNetworkMonitor
import com.example.newsagreggator.data.sample.InMemoryNewsRefreshScheduler
import com.example.newsagreggator.data.sample.InMemorySelectedCityRepository
import com.example.newsagreggator.data.sample.InMemoryUserPreferencesRepository
import com.example.newsagreggator.data.sample.SampleNewsRepository
import com.example.newsagreggator.util.SerbianTextNormalizer
import com.example.newsagreggator.ui.speech.ArticleSpeechController
import com.example.newsagreggator.ui.speech.SpeechArticle
import com.example.newsagreggator.ui.speech.SpeechActionResult
import com.example.newsagreggator.ui.elements.screens.ForYouScreen
import com.example.newsagreggator.ui.elements.screens.DigestScreen
import com.example.newsagreggator.ui.elements.screens.HistoryScreen
import com.example.newsagreggator.ui.elements.screens.SavedScreen
import com.example.newsagreggator.ui.elements.screens.SettingsScreen
import com.example.newsagreggator.ui.elements.screens.TokHomeScreen
import com.example.newsagreggator.ui.stateholders.NewsCardUiModel
import com.example.newsagreggator.ui.stateholders.LocationUiState
import com.example.newsagreggator.ui.stateholders.SecondaryScreen
import com.example.newsagreggator.ui.stateholders.TokTab
import com.example.newsagreggator.ui.elements.theme.NewsAgreggatorTheme
import com.example.newsagreggator.ui.stateholders.TokViewModel
import kotlinx.coroutines.launch

private val TokTab.labelResId: Int
    get() = when (this) {
        TokTab.Home -> R.string.nav_home
        TokTab.ForYou -> R.string.nav_for_you
        TokTab.Saved -> R.string.nav_saved
        TokTab.Settings -> R.string.nav_settings
    }

private val TokTab.iconResId: Int
    get() = when (this) {
        TokTab.Home -> R.drawable.ic_home
        TokTab.ForYou -> R.drawable.ic_spark
        TokTab.Saved -> R.drawable.ic_bookmark_outline
        TokTab.Settings -> R.drawable.ic_settings
    }

@Composable
fun TokApp(
    darkTheme: Boolean,
    modifier: Modifier = Modifier,
    tokViewModel: TokViewModel,
) {
    val uiState by tokViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val speechController = remember(context) { ArticleSpeechController(context) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permissionGranted ->
        tokViewModel.setBreakingNewsEnabled(permissionGranted)
        if (!permissionGranted) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = context.getString(
                        R.string.notification_permission_denied
                    ),
                    withDismissAction = true,
                )
            }
        }
    }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (locationGranted) {
            tokViewModel.detectNearestCity()
        } else {
            tokViewModel.onLocationPermissionDenied()
        }
    }
    val requestGpsLocation: () -> Unit = {
        val locationGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ) == PackageManager.PERMISSION_GRANTED

        if (locationGranted) {
            tokViewModel.detectNearestCity()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )
        }
    }
    val setBreakingNewsEnabled: (Boolean) -> Unit = { enabled ->
        if (!enabled) {
            tokViewModel.setBreakingNewsEnabled(false)
        } else if (
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            tokViewModel.setBreakingNewsEnabled(true)
        } else {
            notificationPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }
    val toggleSaved: (String) -> Unit = { articleId ->
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
        val speechText = articleSpeechText(article)
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
                    text = articleSpeechText(article),
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
    LaunchedEffect(uiState.articleRefreshFailed) {
        if (uiState.articleRefreshFailed) {
            tokViewModel.clearArticleRefreshError()
            snackbarHostState.showSnackbar(
                message = context.getString(R.string.article_refresh_failed),
                withDismissAction = true,
            )
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
                        selected = tab == uiState.selectedTab,
                        onClick = { tokViewModel.selectTab(tab) },
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
                    articles = uiState.articles.filter {
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
                articles = uiState.articles
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
                        uiState.articles
                            .filter {
                                it.categoryResId in uiState.followedCategories
                            }
                            .take(5)
                    )
                },
                onShareArticle = shareArticle,
                modifier = Modifier.padding(innerPadding),
            )
            null -> when (uiState.selectedTab) {
            TokTab.Home -> TokHomeScreen(
                articles = uiState.articles,
                searchQuery = uiState.searchQuery,
                normalizedSearchQuery = uiState.normalizedSearchQuery,
                selectedCategory = uiState.selectedCategory,
                savedArticleIds = uiState.savedArticleIds,
                readArticleIds = uiState.readArticleIds,
                speakingArticleId = speechController.speakingArticleId,
                compactLayout = uiState.compactLayout,
                isRefreshing = uiState.isRefreshing,
                lastSuccessfulRefreshEpochMillis =
                    uiState.lastSuccessfulRefreshEpochMillis,
                isOffline = !uiState.hasInternetConnection,
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
                onRefreshArticles = tokViewModel::refreshArticles,
                onCompactLayoutChange = tokViewModel::setCompactLayout,
                onSearchQueryChange = tokViewModel::setSearchQuery,
                onCategorySelected = tokViewModel::selectCategory,
                modifier = Modifier.padding(innerPadding),
            )
            TokTab.ForYou -> {
                val selectedLocation =
                    uiState.location as? LocationUiState.Selected
                val articles = if (selectedLocation != null) {
                    uiState.articles.filter { article ->
                        selectedLocation.cityId in article.relatedCityIds
                    }
                } else {
                    uiState.articles.filter { article ->
                        article.categoryResId in uiState.followedCategories
                    }
                }
                ForYouScreen(
                    articles = articles,
                    followedCategories = if (selectedLocation == null) {
                        uiState.followedCategories
                    } else {
                        emptySet()
                    },
                    location = uiState.location,
                    savedArticleIds = uiState.savedArticleIds,
                    readArticleIds = uiState.readArticleIds,
                    speakingArticleId = speechController.speakingArticleId,
                    compactLayout = uiState.compactLayout,
                    onToggleSaved = toggleSaved,
                    onReadArticle = readArticle,
                    onToggleSpeech = toggleSpeech,
                    onShareArticle = shareArticle,
                    onLocationAction = requestGpsLocation,
                    modifier = Modifier.padding(innerPadding),
                )
            }
            TokTab.Saved -> SavedScreen(
                articles = uiState.articles.filter {
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
                onDarkThemeChange = tokViewModel::setDarkTheme,
                onCompactLayoutChange = tokViewModel::setCompactLayout,
                onRefreshIntervalChange = tokViewModel::setRefreshInterval,
                onBreakingNewsChange = setBreakingNewsEnabled,
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
    article: NewsCardUiModel,
): String = buildString {
    append(article.title)
    append(". ")
    append(article.summary)
    append(". ")
    append(article.source)
}

private fun launchShareChooser(
    context: Context,
    article: NewsCardUiModel,
) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(
            Intent.EXTRA_TEXT,
            "${article.title}\n${article.url}",
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
    val newsRepository = SampleNewsRepository()
    val userPreferencesRepository = InMemoryUserPreferencesRepository()
    val selectedCityRepository = InMemorySelectedCityRepository()
    val cityResolver = InMemoryCityResolver()
    val currentLocationProvider = InMemoryCurrentLocationProvider()
    val textNormalizer = SerbianTextNormalizer()
    NewsAgreggatorTheme(darkTheme = false) {
        TokApp(
            darkTheme = false,
            tokViewModel = TokViewModel(
                newsRepository = newsRepository,
                userPreferencesRepository = userPreferencesRepository,
                articleStateRepository = InMemoryArticleStateRepository(),
                networkMonitor = InMemoryNetworkMonitor(),
                newsRefreshScheduler = InMemoryNewsRefreshScheduler(),
                refreshNewsCommand = RefreshNewsCommand(newsRepository),
                updateRefreshIntervalCommand =
                    UpdateRefreshIntervalCommand(userPreferencesRepository),
                toggleFollowedCategoryCommand =
                    ToggleFollowedCategoryCommand(userPreferencesRepository),
                detectNearestCityCommand = DetectNearestCityCommand(
                    currentLocationProvider = currentLocationProvider,
                    cityResolver = cityResolver,
                    selectedCityRepository = selectedCityRepository,
                ),
                selectedCityRepository = selectedCityRepository,
                cityResolver = cityResolver,
                textNormalizer = textNormalizer,
            ),
        )
    }
}
