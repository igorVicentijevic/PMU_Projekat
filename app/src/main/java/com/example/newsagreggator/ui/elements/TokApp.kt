package com.example.newsagreggator.ui.elements

import android.content.Context
import android.content.Intent
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.newsagreggator.R
import com.example.newsagreggator.ui.elements.screens.ForYouScreen
import com.example.newsagreggator.ui.elements.screens.DigestScreen
import com.example.newsagreggator.ui.elements.screens.HistoryScreen
import com.example.newsagreggator.ui.elements.screens.SavedScreen
import com.example.newsagreggator.ui.elements.screens.SettingsScreen
import com.example.newsagreggator.ui.elements.screens.TokHomeScreen
import com.example.newsagreggator.ui.model.NewsCardUiModel
import com.example.newsagreggator.ui.model.sampleNewsArticles

private enum class TokTab(
    @StringRes val labelResId: Int,
    @DrawableRes val iconResId: Int,
) {
    Home(R.string.nav_home, R.drawable.ic_home),
    ForYou(R.string.nav_for_you, R.drawable.ic_spark),
    Saved(R.string.nav_saved, R.drawable.ic_bookmark_outline),
    Settings(R.string.nav_settings, R.drawable.ic_settings),
}

private enum class SecondaryScreen {
    History,
    Digest,
}

@Composable
fun TokApp(
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTab by rememberSaveable { mutableStateOf(TokTab.Home) }
    var secondaryScreen by rememberSaveable { mutableStateOf<SecondaryScreen?>(null) }
    var compactLayout by rememberSaveable { mutableStateOf(false) }
    var refreshIntervalMinutes by rememberSaveable { mutableStateOf(15) }
    var breakingNewsEnabled by rememberSaveable { mutableStateOf(true) }
    var savedArticleIds by remember { mutableStateOf(emptySet<Int>()) }
    var readArticleIds by remember { mutableStateOf(emptySet<Int>()) }
    val context = LocalContext.current
    var followedCategories by remember {
        mutableStateOf(
            setOf(
            R.string.category_serbia,
            R.string.category_technology,
            R.string.category_world,
            )
        )
    }
    val toggleSaved: (Int) -> Unit = { articleId ->
        savedArticleIds = if (articleId in savedArticleIds) {
            savedArticleIds - articleId
        } else {
            savedArticleIds + articleId
        }
    }
    val readArticle: (NewsCardUiModel) -> Unit = { article ->
        readArticleIds = readArticleIds + article.id
    }
    val shareArticle: (NewsCardUiModel) -> Unit = { article ->
        launchShareChooser(context, article)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
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
                            secondaryScreen = null
                        },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (tab == TokTab.Saved && savedArticleIds.isNotEmpty()) {
                                        Badge {
                                            Text(savedArticleIds.size.toString())
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
        when (secondaryScreen) {
            SecondaryScreen.History -> HistoryScreen(
                    articles = sampleNewsArticles.filter { it.id in readArticleIds },
                    savedArticleIds = savedArticleIds,
                    compactLayout = compactLayout,
                    onBack = { secondaryScreen = null },
                    onClearHistory = { readArticleIds = emptySet() },
                    onToggleSaved = toggleSaved,
                    onShareArticle = shareArticle,
                    modifier = Modifier.padding(innerPadding),
                )
            SecondaryScreen.Digest -> DigestScreen(
                articles = sampleNewsArticles
                    .filter { it.categoryResId in followedCategories }
                    .take(5),
                savedArticleIds = savedArticleIds,
                readArticleIds = readArticleIds,
                compactLayout = compactLayout,
                onBack = { secondaryScreen = null },
                onToggleSaved = toggleSaved,
                onReadArticle = readArticle,
                onShareArticle = shareArticle,
                modifier = Modifier.padding(innerPadding),
            )
            null -> when (selectedTab) {
            TokTab.Home -> TokHomeScreen(
                savedArticleIds = savedArticleIds,
                readArticleIds = readArticleIds,
                compactLayout = compactLayout,
                onToggleSaved = toggleSaved,
                onReadArticle = readArticle,
                onShareArticle = shareArticle,
                onOpenHistory = { secondaryScreen = SecondaryScreen.History },
                onOpenDigest = { secondaryScreen = SecondaryScreen.Digest },
                onCompactLayoutChange = { compactLayout = it },
                modifier = Modifier.padding(innerPadding),
            )
            TokTab.ForYou -> ForYouScreen(
                articles = sampleNewsArticles.filter {
                    it.categoryResId in followedCategories
                },
                followedCategories = followedCategories,
                savedArticleIds = savedArticleIds,
                readArticleIds = readArticleIds,
                compactLayout = compactLayout,
                onToggleSaved = toggleSaved,
                onReadArticle = readArticle,
                onShareArticle = shareArticle,
                modifier = Modifier.padding(innerPadding),
            )
            TokTab.Saved -> SavedScreen(
                articles = sampleNewsArticles.filter { it.id in savedArticleIds },
                readArticleIds = readArticleIds,
                compactLayout = compactLayout,
                onRemoveSaved = toggleSaved,
                onReadArticle = readArticle,
                onShareArticle = shareArticle,
                modifier = Modifier.padding(innerPadding),
            )
            TokTab.Settings -> SettingsScreen(
                darkTheme = darkTheme,
                compactLayout = compactLayout,
                followedCategories = followedCategories,
                refreshIntervalMinutes = refreshIntervalMinutes,
                breakingNewsEnabled = breakingNewsEnabled,
                onDarkThemeChange = onDarkThemeChange,
                onCompactLayoutChange = { compactLayout = it },
                onRefreshIntervalChange = { refreshIntervalMinutes = it },
                onBreakingNewsChange = { breakingNewsEnabled = it },
                onToggleCategory = { category ->
                    followedCategories = if (category in followedCategories) {
                        followedCategories - category
                    } else {
                        followedCategories + category
                    }
                },
                modifier = Modifier.padding(innerPadding),
            )
            }
        }
    }
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
