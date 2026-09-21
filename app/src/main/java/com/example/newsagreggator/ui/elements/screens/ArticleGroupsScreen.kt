package com.example.newsagreggator.ui.elements.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.newsagreggator.R
import com.example.newsagreggator.data.sample.createSampleNewsArticles
import com.example.newsagreggator.ui.elements.theme.NewsAgreggatorTheme
import com.example.newsagreggator.ui.stateholders.ArticleGroupUiModel
import com.example.newsagreggator.ui.stateholders.NewsCardUiModel
import com.example.newsagreggator.ui.stateholders.toNewsCardUiModel
import coil3.compose.AsyncImage

@Composable
fun ArticleGroupsScreen(
    groups: List<ArticleGroupUiModel>,
    onBack: () -> Unit,
    onOpenGroup: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
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
                text = stringResource(R.string.article_groups_kicker),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelSmall,
            )
            Text(
                text = stringResource(R.string.article_groups_title),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineLarge,
            )
            Text(
                text = stringResource(R.string.article_groups_subtitle),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (groups.isEmpty()) {
            item { ArticleGroupsEmptyState() }
        } else {
            items(
                items = groups,
                key = ArticleGroupUiModel::id,
            ) { group ->
                ArticleGroupCard(
                    group = group,
                    onClick = { onOpenGroup(group.id) },
                )
            }
        }

        item { Spacer(modifier = Modifier.height(6.dp)) }
    }
}

@Composable
private fun ArticleGroupCard(
    group: ArticleGroupUiModel,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ArticleImageMosaic(
                articles = group.articles,
                modifier = Modifier.size(92.dp),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = group.title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                )
                group.articles.take(2).forEach { article ->
                    Text(
                        text = article.title,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            Icon(
                painter = painterResource(R.drawable.ic_arrow),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun ArticleImageMosaic(
    articles: List<NewsCardUiModel>,
    modifier: Modifier = Modifier,
) {
    val previewArticles = articles.take(MAX_MOSAIC_IMAGES)

    if (previewArticles.size == 1) {
        MosaicImage(
            article = previewArticles.single(),
            modifier = modifier.clip(RoundedCornerShape(16.dp)),
        )
        return
    }

    Column(
        modifier = modifier.clip(RoundedCornerShape(16.dp)),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        previewArticles.chunked(MOSAIC_COLUMNS).forEach { rowArticles ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                if (rowArticles.size == 1) {
                    MosaicImage(
                        article = rowArticles.single(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                    )
                } else {
                    rowArticles.forEach { article ->
                        MosaicImage(
                            article = article,
                            modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MosaicImage(
    article: NewsCardUiModel,
    modifier: Modifier,
) {
    AsyncImage(
        model = article.imageUrl,
        contentDescription = null,
        placeholder = painterResource(article.placeholderImageResId),
        error = painterResource(article.placeholderImageResId),
        fallback = painterResource(article.placeholderImageResId),
        contentScale = ContentScale.Crop,
        modifier = modifier,
    )
}

@Composable
private fun ArticleGroupsEmptyState() {
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
                painter = painterResource(R.drawable.ic_cards),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(34.dp),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.article_groups_empty_title),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.article_groups_empty_body),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ArticleGroupsScreenPreview() {
    val articles = createSampleNewsArticles().map { it.toNewsCardUiModel() }
    NewsAgreggatorTheme {
        Surface {
            ArticleGroupsScreen(
                groups = listOf(
                    ArticleGroupUiModel(
                        id = "group-1",
                        title = "Srbija i tehnologija",
                        articles = articles.take(2),
                    ),
                    ArticleGroupUiModel(
                        id = "group-2",
                        title = "Tehnologija i svet",
                        articles = articles.drop(1),
                    ),
                ),
                onBack = {},
                onOpenGroup = {},
            )
        }
    }
}

private const val MAX_MOSAIC_IMAGES = 4
private const val MOSAIC_COLUMNS = 2
