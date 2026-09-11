package com.example.newsagreggator.ui.elements.composables

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.newsagreggator.R

data class NewsCardUiModel(
    val id: Int,
    @DrawableRes val imageResId: Int,
    @StringRes val categoryResId: Int,
    @StringRes val sourceResId: Int,
    @StringRes val timeResId: Int,
    @StringRes val titleResId: Int,
    @StringRes val summaryResId: Int,
)

@Composable
fun NewsArticleCard(
    article: NewsCardUiModel,
    compact: Boolean,
    isSaved: Boolean,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(if (compact) 18.dp else 24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        if (compact) {
            CompactCardContent(article, isSaved, onSaveClick)
        } else {
            StandardCardContent(article, isSaved, onSaveClick)
        }
    }
}

@Composable
private fun StandardCardContent(
    article: NewsCardUiModel,
    isSaved: Boolean,
    onSaveClick: () -> Unit,
) {
    Column {
        ArticleImage(
            article = article,
            compact = false,
            isSaved = isSaved,
            onSaveClick = onSaveClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(185.dp),
        )
        Column(modifier = Modifier.padding(17.dp)) {
            ArticleMetadata(article)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(article.titleResId),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(article.summaryResId),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
private fun CompactCardContent(
    article: NewsCardUiModel,
    isSaved: Boolean,
    onSaveClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(132.dp)
    ) {
        Row(modifier = Modifier.matchParentSize()) {
            ArticleImage(
                article = article,
                compact = true,
                showBookmark = false,
                isSaved = isSaved,
                onSaveClick = onSaveClick,
                modifier = Modifier
                    .width(112.dp)
                    .height(132.dp),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp, top = 12.dp, end = 42.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                ArticleMetadata(article)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(article.titleResId),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        BookmarkButton(
            compact = true,
            isSaved = isSaved,
            onSaveClick = onSaveClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp),
        )
    }
}

@Composable
private fun BookmarkButton(
    compact: Boolean,
    isSaved: Boolean,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onSaveClick,
        modifier = modifier
            .size(if (compact) 27.dp else 38.dp)
            .clip(RoundedCornerShape(if (compact) 9.dp else 12.dp))
            .background(
                if (isSaved) {
                    Color(0xFFE8F5EE)
                } else {
                    Color(0x99111714)
                }
            ),
    ) {
        Icon(
            painter = painterResource(
                if (isSaved) {
                    R.drawable.ic_bookmark_filled
                } else {
                    R.drawable.ic_bookmark_outline
                }
            ),
            contentDescription = stringResource(
                if (isSaved) {
                    R.string.remove_saved_article
                } else {
                    R.string.save_article
                }
            ),
            tint = if (isSaved) {
                MaterialTheme.colorScheme.primary
            } else {
                Color.White
            },
            modifier = Modifier.size(if (compact) 15.dp else 19.dp),
        )
    }
}

@Composable
private fun ArticleImage(
    article: NewsCardUiModel,
    compact: Boolean,
    showBookmark: Boolean = true,
    isSaved: Boolean,
    onSaveClick: () -> Unit,
    modifier: Modifier,
) {
    Box(modifier = modifier) {
        Image(
            painter = painterResource(article.imageResId),
            contentDescription = stringResource(article.titleResId),
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize(),
        )
        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(if (compact) 8.dp else 12.dp),
            shape = RoundedCornerShape(if (compact) 7.dp else 9.dp),
            color = Color(0xA6111714),
        ) {
            Text(
                text = stringResource(article.categoryResId).uppercase(),
                modifier = Modifier.padding(
                    horizontal = if (compact) 6.dp else 8.dp,
                    vertical = if (compact) 4.dp else 5.dp,
                ),
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
            )
        }
        if (showBookmark) {
            BookmarkButton(
                compact = compact,
                isSaved = isSaved,
                onSaveClick = onSaveClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp),
            )
        }
    }
}

@Composable
private fun ArticleMetadata(article: NewsCardUiModel) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(article.sourceResId),
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelSmall,
        )
        Text(
            text = "•",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall,
        )
        Text(
            text = stringResource(article.timeResId),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}
