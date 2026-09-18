package com.example.newsagreggator.ui.elements.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.newsagreggator.R
import com.example.newsagreggator.data.sample.createSampleNewsArticles
import com.example.newsagreggator.ui.elements.composables.NewsArticleCard
import com.example.newsagreggator.ui.stateholders.NewsCardUiModel
import com.example.newsagreggator.ui.stateholders.toNewsCardUiModel
import com.example.newsagreggator.ui.stateholders.LocationUiState
import com.example.newsagreggator.ui.elements.theme.NewsAgreggatorTheme

@Composable
fun ForYouScreen(
    articles: List<NewsCardUiModel>,
    followedCategories: Set<Int>,
    location: LocationUiState,
    savedArticleIds: Set<String>,
    readArticleIds: Set<String>,
    speakingArticleId: String?,
    compactLayout: Boolean,
    onToggleSaved: (String) -> Unit,
    onReadArticle: (NewsCardUiModel) -> Unit,
    onToggleSpeech: (NewsCardUiModel) -> Unit,
    onShareArticle: (NewsCardUiModel) -> Unit,
    onLocationAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 28.dp),
    ) {
        item {
            Text(
                text = stringResource(R.string.for_you_kicker),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelSmall,
            )
            Text(
                text = stringResource(R.string.nav_for_you),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineLarge,
            )
            Text(
                text = if (location is LocationUiState.Selected) {
                    stringResource(
                        R.string.for_you_local_subtitle,
                        location.cityName,
                    )
                } else {
                    stringResource(R.string.for_you_subtitle)
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(18.dp))
            LocalNewsLocationCard(
                location = location,
                onLocationAction = onLocationAction,
            )
            Spacer(modifier = Modifier.height(18.dp))
            if (followedCategories.isNotEmpty()) {
                FollowedCategoryChips(followedCategories)
                Spacer(modifier = Modifier.height(18.dp))
            }
        }
        if (articles.isEmpty()) {
            item { ForYouEmptyState(location) }
        } else {
            items(
                items = articles,
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
                    onShareClick = { onShareArticle(article) },
                    modifier = Modifier.padding(
                        bottom = if (compactLayout) 10.dp else 18.dp
                    ),
                )
            }
        }
        item { Spacer(modifier = Modifier.height(6.dp)) }
    }
}

@Composable
private fun LocalNewsLocationCard(
    location: LocationUiState,
    onLocationAction: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
        ) {
            Text(
                text = when (location) {
                    is LocationUiState.Selected -> stringResource(
                        R.string.for_you_location_selected_title,
                        location.cityName,
                    )
                    else -> stringResource(R.string.for_you_location_title)
                },
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = when (location) {
                    LocationUiState.NotConfigured,
                    LocationUiState.PermissionRequired,
                    -> stringResource(R.string.for_you_location_body)

                    LocationUiState.Detecting ->
                        stringResource(R.string.for_you_location_detecting)

                    LocationUiState.PermissionDenied ->
                        stringResource(R.string.for_you_location_permission_denied)

                    LocationUiState.LocationServicesDisabled ->
                        stringResource(R.string.for_you_location_disabled)

                    LocationUiState.LocationUnavailable ->
                        stringResource(R.string.for_you_location_unavailable)

                    LocationUiState.OutsideSupportedArea ->
                        stringResource(R.string.for_you_location_outside_serbia)

                    is LocationUiState.Selected -> stringResource(
                        R.string.for_you_location_selected_body
                    )
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
            when (location) {
                LocationUiState.Detecting -> {
                    Spacer(modifier = Modifier.height(14.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 3.dp,
                    )
                }
                is LocationUiState.Selected -> Unit
                else -> {
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(onClick = onLocationAction) {
                        Text(
                            text = stringResource(
                                when (location) {
                                    LocationUiState.LocationServicesDisabled ->
                                        R.string.for_you_location_retry
                                    else -> R.string.for_you_location_use_gps
                                }
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FollowedCategoryChips(categories: Set<Int>) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories.toList()) { category ->
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            ) {
                Text(
                    text = stringResource(category),
                    modifier = Modifier.padding(horizontal = 13.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun ForYouEmptyState(location: LocationUiState) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_spark),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (location is LocationUiState.Selected) {
                    stringResource(
                        R.string.for_you_local_empty_title,
                        location.cityName,
                    )
                } else {
                    stringResource(R.string.for_you_empty_title)
                },
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(
                    if (location is LocationUiState.Selected) {
                        R.string.for_you_local_empty_body
                    } else {
                        R.string.for_you_empty_body
                    }
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Preview(name = "Za vas - sadržaj", showBackground = true)
@Composable
private fun ForYouScreenContentPreview() {
    NewsAgreggatorTheme {
        Surface {
            ForYouScreen(
                articles = createSampleNewsArticles().map {
                    it.toNewsCardUiModel()
                },
                followedCategories = setOf(
                    R.string.category_serbia,
                    R.string.category_technology,
                    R.string.category_world,
                ),
                location = LocationUiState.Selected(
                    cityId = "belgrade",
                    cityName = "Beograd",
                ),
                savedArticleIds = setOf("1"),
                readArticleIds = setOf("2"),
                speakingArticleId = "1",
                compactLayout = true,
                onToggleSaved = {},
                onReadArticle = {},
                onToggleSpeech = {},
                onShareArticle = {},
                onLocationAction = {},
            )
        }
    }
}

@Preview(name = "Za vas - prazno", showBackground = true)
@Composable
private fun ForYouScreenEmptyPreview() {
    NewsAgreggatorTheme {
        Surface {
            ForYouScreen(
                articles = emptyList(),
                followedCategories = emptySet(),
                location = LocationUiState.NotConfigured,
                savedArticleIds = emptySet(),
                readArticleIds = emptySet(),
                speakingArticleId = null,
                compactLayout = false,
                onToggleSaved = {},
                onReadArticle = {},
                onToggleSpeech = {},
                onShareArticle = {},
                onLocationAction = {},
            )
        }
    }
}
