package com.example.newsagreggator.ui.elements.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.newsagreggator.R
import com.example.newsagreggator.ui.elements.composables.LatestSection
import com.example.newsagreggator.ui.elements.composables.NewsArticleCard
import com.example.newsagreggator.ui.elements.composables.TokTopBar
import com.example.newsagreggator.ui.elements.composables.TokCategoryDrawer
import com.example.newsagreggator.ui.elements.composables.TrendingSection
import com.example.newsagreggator.ui.stateholders.NewsCardUiModel
import com.example.newsagreggator.data.sample.createSampleNewsArticles
import com.example.newsagreggator.ui.stateholders.toNewsCardUiModel
import com.example.newsagreggator.ui.elements.theme.NewsAgreggatorTheme
import kotlinx.coroutines.launch

@Composable
fun TokHomeScreen(
    articles: List<NewsCardUiModel>,
    searchQuery: String,
    normalizedSearchQuery: String,
    selectedCategory: Int,
    followedCategories: Set<Int>,
    savedArticleIds: Set<String>,
    readArticleIds: Set<String>,
    speakingArticleId: String?,
    compactLayout: Boolean,
    isRefreshing: Boolean,
    lastSuccessfulRefreshEpochMillis: Long?,
    isOffline: Boolean,
    onToggleSaved: (String) -> Unit,
    onReadArticle: (NewsCardUiModel) -> Unit,
    onToggleSpeech: (NewsCardUiModel) -> Unit,
    onExportPdf: (NewsCardUiModel) -> Unit,
    onShareArticle: (NewsCardUiModel) -> Unit,
    onOpenHistory: () -> Unit,
    onOpenDigest: () -> Unit,
    onRefreshArticles: () -> Unit,
    onCompactLayoutChange: (Boolean) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelected: (Int) -> Unit,
    onToggleFollowedCategory: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val visibleArticles = articles.filter { article ->
        val matchesCategory =
            selectedCategory == R.string.category_all ||
                selectedCategory == article.categoryResId
        val matchesQuery =
            normalizedSearchQuery.isEmpty() ||
                article.normalizedText.contains(normalizedSearchQuery)
        matchesCategory && matchesQuery
    }

    TokCategoryDrawer(
        drawerState = drawerState,
        followedCategories = followedCategories,
        isOffline = isOffline,
        onToggleCategory = onToggleFollowedCategory,
        onDigestClick = {
            coroutineScope.launch {
                drawerState.close()
                onOpenDigest()
            }
        },
        onHistoryClick = {
            coroutineScope.launch {
                drawerState.close()
                onOpenHistory()
            }
        },
        onClose = { coroutineScope.launch { drawerState.close() } },
    ) {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = 20.dp,
                vertical = 12.dp,
            ),
        ) {
            item {
                TokTopBar(
                    onMenuClick = {
                        coroutineScope.launch { drawerState.open() }
                    }
                )
                Spacer(modifier = Modifier.height(34.dp))
                Text(
                    text = stringResource(R.string.home_eyebrow),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelSmall,
                )
                Text(
                    text = stringResource(R.string.home_greeting),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineLarge,
                )
                Text(
                    text = stringResource(R.string.home_subtitle),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
                if (isOffline) {
                    Spacer(modifier = Modifier.height(16.dp))
                    OfflineModeCard()
                }
                Spacer(modifier = Modifier.height(22.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.search_news),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_search),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(17.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    ),
                )
                Spacer(modifier = Modifier.height(26.dp))
                TrendingSection(
                    articles = articles,
                    onDigestClick = onOpenDigest,
                )
                Spacer(modifier = Modifier.height(26.dp))
                LatestSection(
                    selectedCategory = selectedCategory,
                    compactLayout = compactLayout,
                    isRefreshing = isRefreshing,
                    lastSuccessfulRefreshEpochMillis =
                        lastSuccessfulRefreshEpochMillis,
                    isOffline = isOffline,
                    onCategorySelected = onCategorySelected,
                    onCompactLayoutClick = {
                        onCompactLayoutChange(!compactLayout)
                    },
                    onRefreshClick = {
                        if (!isRefreshing) {
                            onRefreshArticles()
                        }
                    },
                )
                Spacer(modifier = Modifier.height(14.dp))
                if (visibleArticles.isEmpty()) {
                    Text(
                        text = stringResource(
                            if (normalizedSearchQuery.isEmpty()) {
                                R.string.no_category_news
                            } else {
                                R.string.no_search_results
                            }
                        ),
                        modifier = Modifier.padding(vertical = 28.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            items(
                items = visibleArticles,
                key = NewsCardUiModel::url,
            ) { article ->
                NewsArticleCard(
                    article = article,
                    compact = compactLayout,
                    isSaved = article.id in savedArticleIds,
                    isRead = article.id in readArticleIds,
                    isSpeaking = article.id == speakingArticleId,
                    onSaveClick = { onToggleSaved(article.id) },
                    onReadClick = { onReadArticle(article) },
                    onSpeechClick = { onToggleSpeech(article) },
                    onPdfClick = { onExportPdf(article) },
                    onShareClick = { onShareArticle(article) },
                    modifier = Modifier.padding(
                        bottom = if (compactLayout) 10.dp else 18.dp
                    ),
                )
            }
            item {
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

@Composable
private fun OfflineModeCard(
    modifier: Modifier = Modifier,
) {
    val offlineOrange = Color(0xFFD08B35)
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = offlineOrange.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, offlineOrange.copy(alpha = 0.35f)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(offlineOrange)
            )
            Column {
                Text(
                    text = stringResource(R.string.offline_home_title),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = stringResource(R.string.offline_home_body),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TokHomeScreenPreview() {
    NewsAgreggatorTheme {
        Surface {
            TokHomeScreen(
                articles = createSampleNewsArticles().map {
                    it.toNewsCardUiModel()
                },
                searchQuery = "",
                normalizedSearchQuery = "",
                selectedCategory = R.string.category_all,
                followedCategories = setOf(
                    R.string.category_serbia,
                    R.string.category_technology,
                ),
                savedArticleIds = emptySet(),
                readArticleIds = emptySet(),
                speakingArticleId = null,
                compactLayout = false,
                isRefreshing = false,
                lastSuccessfulRefreshEpochMillis = null,
                isOffline = false,
                onToggleSaved = {},
                onReadArticle = {},
                onToggleSpeech = {},
                onExportPdf = {},
                onShareArticle = {},
                onOpenHistory = {},
                onOpenDigest = {},
                onRefreshArticles = {},
                onCompactLayoutChange = {},
                onSearchQueryChange = {},
                onCategorySelected = {},
                onToggleFollowedCategory = {},
            )
        }
    }
}
