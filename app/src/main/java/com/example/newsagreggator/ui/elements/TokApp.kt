package com.example.newsagreggator.ui.elements

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.ClipData
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
import com.example.newsagreggator.digest.strategy.FollowedCategoriesDailyDigestStrategy
import com.example.newsagreggator.digest.strategy.WordCountDigestReadingTimeStrategy
import com.example.newsagreggator.pdf.exporter.PdfExportResult
import com.example.newsagreggator.pdf.service.ArticlePdfService
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
import com.example.newsagreggator.ui.stateholders.ForYouTab
import com.example.newsagreggator.ui.stateholders.SecondaryScreen
import com.example.newsagreggator.ui.stateholders.TokTab
import com.example.newsagreggator.ui.elements.theme.NewsAgreggatorTheme
import com.example.newsagreggator.ui.stateholders.TokViewModel
import com.example.newsagreggator.toneanalyzer.ArticleToneService
import com.example.newsagreggator.toneanalyzer.SampleArticleToneAnalysisStrategy
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
    ambientLightSensorAvailable: Boolean?,
    modifier: Modifier = Modifier,
    tokViewModel: TokViewModel,
    articlePdfService: ArticlePdfService? = null,
) {
    val uiState by tokViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val speechController = remember(context) { ArticleSpeechController(context) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var pendingPdfArticleId by rememberSaveable {
        mutableStateOf<String?>(null)
    }


    var isExportingPdf by remember { mutableStateOf(false) }

    //launcher for picking a destination of a generated pdf
    val pdfDocumentLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument(PDF_MIME_TYPE)
    ) { destination ->

        //get the article Id on which is clicked
        val articleId = pendingPdfArticleId

        pendingPdfArticleId = null
        if (
            destination == null ||
            articleId == null ||
            articlePdfService == null
        ) {
            return@rememberLauncherForActivityResult
        }

        //getting article object from viewmodel
        val article = tokViewModel.uiState.value.articles.firstOrNull {
            it.id == articleId
        }

        if (article == null) {

            //notifying user throught snackbar if export has failed
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = context.getString(R.string.pdf_export_failed),
                    withDismissAction = true,
                )
            }
            return@rememberLauncherForActivityResult
        }


        //starting pdf export
        isExportingPdf = true
        coroutineScope.launch {

            val result = try {
                //exporting the file
                articlePdfService.export(article, destination)
            } finally {
                isExportingPdf = false
            }

            if (result == PdfExportResult.Success) {
                
                //if the file is succesfully exported, lounching snackbar notification
                val snackbarResult = snackbarHostState.showSnackbar(
                    message = context.getString(R.string.pdf_export_success),
                    actionLabel = context.getString(R.string.open),
                    withDismissAction = true,
                )
                if (
                    snackbarResult == SnackbarResult.ActionPerformed &&
                    !launchPdfViewer(context, destination)
                ) {
                    snackbarHostState.showSnackbar(
                        message = context.getString(
                            R.string.pdf_open_failed
                        ),
                        withDismissAction = true,
                    )
                }
            } else {
                snackbarHostState.showSnackbar(
                    message = context.getString(result.messageResId),
                    withDismissAction = true,
                )
            }
        }
    }


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
    val exportPdf: (NewsCardUiModel) -> Unit = { article ->
        if (
            articlePdfService != null &&
            pendingPdfArticleId == null &&
            !isExportingPdf
        ) {
            pendingPdfArticleId = article.id
            pdfDocumentLauncher.launch(
                articlePdfService.suggestedFilename(article)
            )
        }
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
                    onExportPdf = exportPdf,
                    onShareArticle = shareArticle,
                    modifier = Modifier.padding(innerPadding),
                )
            SecondaryScreen.Digest -> DigestScreen(
                articles = uiState.digestArticles,
                readingTimeMinutes = uiState.digestReadingTimeMinutes,
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
                    toggleDigestSpeech(uiState.digestArticles)
                },
                onExportPdf = exportPdf,
                onShareArticle = shareArticle,
                modifier = Modifier.padding(innerPadding),
            )
            null -> when (uiState.selectedTab) {
            TokTab.Home -> TokHomeScreen(
                articles = uiState.articles,
                searchQuery = uiState.searchQuery,
                normalizedSearchQuery = uiState.normalizedSearchQuery,
                selectedCategory = uiState.selectedCategory,
                followedCategories = uiState.followedCategories,
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
                onExportPdf = exportPdf,
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
                onToggleFollowedCategory =
                    tokViewModel::toggleFollowedCategory,
                modifier = Modifier.padding(innerPadding),
            )
            TokTab.ForYou -> {
                val selectedLocation =
                    uiState.location as? LocationUiState.Selected
                val articles = when (uiState.selectedForYouTab) {
                    ForYouTab.Location -> {
                        if (selectedLocation == null) {
                            emptyList()
                        } else {
                            uiState.articles.filter { article ->
                                selectedLocation.cityId in article.relatedCityIds
                            }
                        }
                    }

                    ForYouTab.FavoriteCategories -> {
                        uiState.articles.filter { article ->
                            article.categoryResId in uiState.followedCategories
                        }
                    }
                }
                ForYouScreen(
                    articles = articles,
                    selectedTab = uiState.selectedForYouTab,
                    followedCategories = uiState.followedCategories,
                    location = uiState.location,
                    savedArticleIds = uiState.savedArticleIds,
                    readArticleIds = uiState.readArticleIds,
                    speakingArticleId = speechController.speakingArticleId,
                    compactLayout = uiState.compactLayout,
                    onToggleSaved = toggleSaved,
                    onReadArticle = readArticle,
                    onToggleSpeech = toggleSpeech,
                    onExportPdf = exportPdf,
                    onShareArticle = shareArticle,
                    onLocationAction = requestGpsLocation,
                    onTabSelected = tokViewModel::selectForYouTab,
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
                onExportPdf = exportPdf,
                onShareArticle = shareArticle,
                modifier = Modifier.padding(innerPadding),
            )
            TokTab.Settings -> SettingsScreen(
                darkTheme = darkTheme,
                automaticThemeEnabled = uiState.automaticThemeEnabled,
                ambientLightSensorAvailable = ambientLightSensorAvailable,
                compactLayout = uiState.compactLayout,
                refreshIntervalMinutes = uiState.refreshIntervalMinutes,
                breakingNewsEnabled = uiState.breakingNewsEnabled,
                onDarkThemeChange = tokViewModel::setDarkTheme,
                onAutomaticThemeChange =
                    tokViewModel::setAutomaticThemeEnabled,
                onCompactLayoutChange = tokViewModel::setCompactLayout,
                onRefreshIntervalChange = tokViewModel::setRefreshInterval,
                onBreakingNewsChange = setBreakingNewsEnabled,
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

private fun launchPdfViewer(
    context: Context,
    destination: Uri,
): Boolean {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(destination, PDF_MIME_TYPE)
        clipData = ClipData.newRawUri(PDF_CLIP_LABEL, destination)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    return try {
        context.startActivity(intent)
        true
    } catch (_: ActivityNotFoundException) {
        false
    } catch (_: SecurityException) {
        false
    }
}

private val PdfExportResult.messageResId: Int
    get() = when (this) {
        PdfExportResult.Success -> R.string.pdf_export_success
        PdfExportResult.InvalidContent -> R.string.pdf_export_invalid_content
        is PdfExportResult.DestinationUnavailable ->
            R.string.pdf_destination_unavailable
        is PdfExportResult.WriteFailed -> R.string.pdf_export_failed
    }

private const val PDF_MIME_TYPE = "application/pdf"
private const val PDF_CLIP_LABEL = "Saved PDF"

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
            ambientLightSensorAvailable = true,
            articlePdfService = null,
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
                dailyDigestStrategy =
                    FollowedCategoriesDailyDigestStrategy(),
                digestReadingTimeStrategy =
                    WordCountDigestReadingTimeStrategy(),
                articleToneService = ArticleToneService(
                    SampleArticleToneAnalysisStrategy()
                ),
            ),
        )
    }
}
