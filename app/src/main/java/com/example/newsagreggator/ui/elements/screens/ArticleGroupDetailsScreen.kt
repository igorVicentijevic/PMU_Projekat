package com.example.newsagreggator.ui.elements.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.newsagreggator.R
import com.example.newsagreggator.ui.elements.composables.NewsArticleCard
import com.example.newsagreggator.ui.stateholders.ArticleGroupUiModel
import com.example.newsagreggator.ui.stateholders.NewsCardUiModel

@Composable
fun ArticleGroupDetailsScreen(
    group: ArticleGroupUiModel,
    savedArticleIds: Set<String>,
    readArticleIds: Set<String>,
    speakingArticleId: String?,
    compactLayout: Boolean,
    onBack: () -> Unit,
    onToggleSaved: (String) -> Unit,
    onReadArticle: (NewsCardUiModel) -> Unit,
    onToggleSpeech: (NewsCardUiModel) -> Unit,
    onExportPdf: (NewsCardUiModel) -> Unit,
    onShareArticle: (NewsCardUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
    ) {
        item {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = stringResource(R.string.navigate_back),
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.article_group_details_kicker),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelSmall,
            )
            Text(
                text = group.title,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineLarge,
            )
            Text(
                text = pluralStringResource(
                    R.plurals.article_group_details_subtitle,
                    group.articles.size,
                    group.articles.size,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        items(
            items = group.articles,
            key = NewsCardUiModel::id,
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

        item { Spacer(modifier = Modifier.height(6.dp)) }
    }
}
