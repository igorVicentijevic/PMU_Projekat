package com.example.newsagreggator.ui.elements

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.newsagreggator.R
import com.example.newsagreggator.ui.elements.screens.ForYouScreen
import com.example.newsagreggator.ui.elements.screens.SavedScreen
import com.example.newsagreggator.ui.elements.screens.SettingsScreen
import com.example.newsagreggator.ui.elements.screens.TokHomeScreen
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

@Composable
fun TokApp(
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTab by rememberSaveable { mutableStateOf(TokTab.Home) }
    var compactLayout by rememberSaveable { mutableStateOf(false) }
    var refreshIntervalMinutes by rememberSaveable { mutableStateOf(15) }
    var breakingNewsEnabled by rememberSaveable { mutableStateOf(true) }
    var savedArticleIds by remember { mutableStateOf(emptySet<Int>()) }
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
                        onClick = { selectedTab = tab },
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
        when (selectedTab) {
            TokTab.Home -> TokHomeScreen(
                savedArticleIds = savedArticleIds,
                compactLayout = compactLayout,
                onToggleSaved = toggleSaved,
                onCompactLayoutChange = { compactLayout = it },
                modifier = Modifier.padding(innerPadding),
            )
            TokTab.ForYou -> ForYouScreen(
                articles = sampleNewsArticles.filter {
                    it.categoryResId in followedCategories
                },
                followedCategories = followedCategories,
                savedArticleIds = savedArticleIds,
                compactLayout = compactLayout,
                onToggleSaved = toggleSaved,
                modifier = Modifier.padding(innerPadding),
            )
            TokTab.Saved -> SavedScreen(
                articles = sampleNewsArticles.filter { it.id in savedArticleIds },
                compactLayout = compactLayout,
                onRemoveSaved = toggleSaved,
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
