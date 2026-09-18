package com.example.newsagreggator.ui.elements.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.newsagreggator.R
import com.example.newsagreggator.data.sample.createSampleNewsArticles
import com.example.newsagreggator.ui.stateholders.NewsCardUiModel
import com.example.newsagreggator.ui.stateholders.toNewsCardUiModel
import com.example.newsagreggator.ui.elements.theme.NewsAgreggatorTheme
import coil3.compose.AsyncImage

@Composable
fun NewsArticleCard(
    article: NewsCardUiModel,
    compact: Boolean,
    isSaved: Boolean,
    isRead: Boolean,
    isSpeaking: Boolean,
    onSaveClick: () -> Unit,
    onReadClick: () -> Unit,
    onSpeechClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(if (compact) 18.dp else 24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isRead) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            },
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        if (compact) {
            CompactCardContent(
                article = article,
                isSaved = isSaved,
                isSpeaking = isSpeaking,
                onSaveClick = onSaveClick,
                onReadClick = onReadClick,
                onSpeechClick = onSpeechClick,
                onShareClick = onShareClick,
            )
        } else {
            StandardCardContent(
                article = article,
                isSaved = isSaved,
                isSpeaking = isSpeaking,
                onSaveClick = onSaveClick,
                onReadClick = onReadClick,
                onSpeechClick = onSpeechClick,
                onShareClick = onShareClick,
            )
        }
    }
}

@Composable
private fun StandardCardContent(
    article: NewsCardUiModel,
    isSaved: Boolean,
    isSpeaking: Boolean,
    onSaveClick: () -> Unit,
    onReadClick: () -> Unit,
    onSpeechClick: () -> Unit,
    onShareClick: () -> Unit,
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
                text = article.title,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = article.summary,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            ArticleActions(
                compact = false,
                isSpeaking = isSpeaking,
                onReadClick = onReadClick,
                onSpeechClick = onSpeechClick,
                onShareClick = onShareClick,
            )
        }
    }
}

@Composable
private fun CompactCardContent(
    article: NewsCardUiModel,
    isSaved: Boolean,
    isSpeaking: Boolean,
    onSaveClick: () -> Unit,
    onReadClick: () -> Unit,
    onSpeechClick: () -> Unit,
    onShareClick: () -> Unit,
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
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(start = 12.dp, end = 8.dp),
            ) {
                Column(
                    modifier = Modifier.padding(top = 12.dp, end = 48.dp),
                ) {
                    ArticleMetadata(article)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = article.title,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                ArticleActions(
                    compact = true,
                    isSpeaking = isSpeaking,
                    onReadClick = onReadClick,
                    onSpeechClick = onSpeechClick,
                    onShareClick = onShareClick,
                    modifier = Modifier.align(Alignment.BottomStart),
                )
            }
        }
        BookmarkButton(
            compact = true,
            isSaved = isSaved,
            onSaveClick = onSaveClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 8.dp),
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
            .clip(
                if (compact) {
                    CircleShape
                } else {
                    RoundedCornerShape(12.dp)
                }
            )
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
        AsyncImage(
            model = article.imageUrl,
            contentDescription = article.title,
            placeholder = painterResource(article.placeholderImageResId),
            error = painterResource(article.placeholderImageResId),
            fallback = painterResource(article.placeholderImageResId),
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
private fun ArticleActions(
    compact: Boolean,
    isSpeaking: Boolean,
    onReadClick: () -> Unit,
    onSpeechClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(
            onClick = onReadClick,
            contentPadding = PaddingValues(
                horizontal = 0.dp,
                vertical = if (compact) 0.dp else 6.dp,
            ),
        ) {
            Text(
                text = stringResource(R.string.read_article),
                fontWeight = FontWeight.Bold,
                style = if (compact) {
                    MaterialTheme.typography.labelSmall
                } else {
                    MaterialTheme.typography.bodyMedium
                },
            )
            Spacer(modifier = Modifier.width(5.dp))
            Icon(
                painter = painterResource(R.drawable.ic_arrow),
                contentDescription = null,
                modifier = Modifier.size(if (compact) 13.dp else 17.dp),
            )
        }
        Row {
            IconButton(
                onClick = onSpeechClick,
                modifier = Modifier
                    .size(if (compact) 28.dp else 36.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSpeaking) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            Color.Transparent
                        }
                    ),
            ) {
                Icon(
                    painter = painterResource(
                        if (isSpeaking) R.drawable.ic_stop else R.drawable.ic_volume
                    ),
                    contentDescription = stringResource(
                        if (isSpeaking) R.string.stop_speaking else R.string.speak_article
                    ),
                    tint = if (isSpeaking) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.size(if (compact) 15.dp else 18.dp),
                )
            }
            IconButton(
                onClick = onShareClick,
                modifier = Modifier.size(if (compact) 28.dp else 36.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_share),
                    contentDescription = stringResource(R.string.share_article),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(if (compact) 15.dp else 18.dp),
                )
            }
        }
    }
}

@Composable
private fun ArticleMetadata(
    article: NewsCardUiModel,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = article.source,
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
            text = article.time,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Preview(name = "Kartica - standardna", showBackground = true)
@Composable
private fun StandardNewsCardPreview() {
    NewsAgreggatorTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            NewsArticleCard(
                article = createSampleNewsArticles().first().toNewsCardUiModel(),
                compact = false,
                isSaved = false,
                isRead = false,
                isSpeaking = false,
                onSaveClick = {},
                onReadClick = {},
                onSpeechClick = {},
                onShareClick = {},
            )
        }
    }
}

@Preview(name = "Kartica - kompaktna", showBackground = true)
@Composable
private fun CompactNewsCardPreview() {
    NewsAgreggatorTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            NewsArticleCard(
                article = createSampleNewsArticles().first().toNewsCardUiModel(),
                compact = true,
                isSaved = true,
                isRead = true,
                isSpeaking = true,
                onSaveClick = {},
                onReadClick = {},
                onSpeechClick = {},
                onShareClick = {},
            )
        }
    }
}
