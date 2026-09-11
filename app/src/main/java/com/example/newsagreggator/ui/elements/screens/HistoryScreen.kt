package com.example.newsagreggator.ui.elements.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.newsagreggator.R
import com.example.newsagreggator.ui.elements.composables.NewsArticleCard
import com.example.newsagreggator.ui.model.NewsCardUiModel
import com.example.newsagreggator.ui.model.sampleNewsArticles
import com.example.newsagreggator.ui.theme.NewsAgreggatorTheme

@Composable
fun HistoryScreen(
    articles: List<NewsCardUiModel>,
    savedArticleIds: Set<Int>,
    compactLayout: Boolean,
    onBack: () -> Unit,
    onClearHistory: () -> Unit,
    onToggleSaved: (Int) -> Unit,
    onShareArticle: (NewsCardUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        IconButton(onClick = onBack) {
            Icon(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = stringResource(R.string.navigate_back),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.history_kicker),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelSmall,
                )
                Text(
                    text = stringResource(R.string.history_title),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineLarge,
                )
                Text(
                    text = stringResource(R.string.history_subtitle),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = articles.size.toString(),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (articles.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = onClearHistory) {
                    Icon(
                        painter = painterResource(R.drawable.ic_trash),
                        contentDescription = null,
                        modifier = Modifier.size(17.dp),
                    )
                    Spacer(modifier = Modifier.size(5.dp))
                    Text(
                        text = stringResource(R.string.history_clear),
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    if (compactLayout) 10.dp else 18.dp
                )
            ) {
                articles.forEach { article ->
                    NewsArticleCard(
                        article = article,
                        compact = compactLayout,
                        isSaved = article.id in savedArticleIds,
                        isRead = true,
                        onSaveClick = { onToggleSaved(article.id) },
                        onReadClick = {},
                        onShareClick = { onShareArticle(article) },
                    )
                }
            }
        } else {
            HistoryEmptyState()
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun HistoryEmptyState() {
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
                painter = painterResource(R.drawable.ic_history),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(34.dp),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.history_empty_title),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.history_empty_body),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Preview(name = "Istorija - sadržaj", showBackground = true)
@Composable
private fun HistoryContentPreview() {
    NewsAgreggatorTheme {
        Surface {
            HistoryScreen(
                articles = sampleNewsArticles.take(2),
                savedArticleIds = setOf(1),
                compactLayout = true,
                onBack = {},
                onClearHistory = {},
                onToggleSaved = {},
                onShareArticle = {},
            )
        }
    }
}

@Preview(name = "Istorija - prazno", showBackground = true)
@Composable
private fun HistoryEmptyPreview() {
    NewsAgreggatorTheme {
        Surface {
            HistoryScreen(
                articles = emptyList(),
                savedArticleIds = emptySet(),
                compactLayout = false,
                onBack = {},
                onClearHistory = {},
                onToggleSaved = {},
                onShareArticle = {},
            )
        }
    }
}
