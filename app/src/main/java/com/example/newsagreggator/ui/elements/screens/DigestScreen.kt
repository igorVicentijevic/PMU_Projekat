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
import com.example.newsagreggator.ui.theme.TokCoral

@Composable
fun DigestScreen(
    articles: List<NewsCardUiModel>,
    savedArticleIds: Set<Int>,
    readArticleIds: Set<Int>,
    speakingArticleId: Int?,
    compactLayout: Boolean,
    onBack: () -> Unit,
    onToggleSaved: (Int) -> Unit,
    onReadArticle: (NewsCardUiModel) -> Unit,
    onToggleSpeech: (NewsCardUiModel) -> Unit,
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
        Text(
            text = stringResource(R.string.digest_kicker),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelSmall,
        )
        Text(
            text = stringResource(R.string.digest_title),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineLarge,
        )
        Text(
            text = stringResource(R.string.digest_subtitle),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(20.dp))
        DigestHero(articleCount = articles.size)
        Spacer(modifier = Modifier.height(22.dp))
        Text(
            text = stringResource(R.string.digest_selection),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (articles.isEmpty()) {
            DigestEmptyState()
        } else {
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
                        isRead = article.id in readArticleIds,
                        isSpeaking = article.id == speakingArticleId,
                        onSaveClick = { onToggleSaved(article.id) },
                        onReadClick = { onReadArticle(article) },
                        onSpeechClick = { onToggleSpeech(article) },
                        onShareClick = { onShareArticle(article) },
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DigestHero(articleCount: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = articleCount.toString(),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = stringResource(R.string.digest_story_count, articleCount)
                            .substringAfter(' '),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.digest_duration),
                    color = TokCoral,
                    style = MaterialTheme.typography.labelSmall,
                )
                Text(
                    text = stringResource(R.string.digest_flow_title),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = stringResource(R.string.digest_flow_body),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun DigestEmptyState() {
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
                modifier = Modifier.size(34.dp),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.digest_empty_title),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.digest_empty_body),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Preview(name = "Dnevni pregled - sadržaj", showBackground = true)
@Composable
private fun DigestContentPreview() {
    NewsAgreggatorTheme {
        Surface {
            DigestScreen(
                articles = sampleNewsArticles,
                savedArticleIds = setOf(1),
                readArticleIds = setOf(2),
                speakingArticleId = 1,
                compactLayout = true,
                onBack = {},
                onToggleSaved = {},
                onReadArticle = {},
                onToggleSpeech = {},
                onShareArticle = {},
            )
        }
    }
}

@Preview(name = "Dnevni pregled - prazno", showBackground = true)
@Composable
private fun DigestEmptyPreview() {
    NewsAgreggatorTheme {
        Surface {
            DigestScreen(
                articles = emptyList(),
                savedArticleIds = emptySet(),
                readArticleIds = emptySet(),
                speakingArticleId = null,
                compactLayout = false,
                onBack = {},
                onToggleSaved = {},
                onReadArticle = {},
                onToggleSpeech = {},
                onShareArticle = {},
            )
        }
    }
}
