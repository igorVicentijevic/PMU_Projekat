package com.example.newsagreggator.ui.elements.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.newsagreggator.R
import com.example.newsagreggator.ui.elements.composables.LatestSection
import com.example.newsagreggator.ui.elements.composables.NewsArticleCard
import com.example.newsagreggator.ui.elements.composables.TokTopBar
import com.example.newsagreggator.ui.elements.composables.TokCategoryDrawer
import com.example.newsagreggator.ui.elements.composables.TrendingSection
import com.example.newsagreggator.ui.model.sampleNewsArticles
import com.example.newsagreggator.ui.theme.NewsAgreggatorTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun TokHomeScreen(
    savedArticleIds: Set<Int>,
    compactLayout: Boolean,
    onToggleSaved: (Int) -> Unit,
    onCompactLayoutChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf(R.string.category_all) }
    var isRefreshing by rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val resources = LocalContext.current.resources
    val serbianLocale = Locale.forLanguageTag("sr-Latn-RS")
    val normalizedQuery = query.trim().lowercase(serbianLocale)
    val visibleArticles = sampleNewsArticles.filter { article ->
        val matchesCategory =
            selectedCategory == R.string.category_all ||
                selectedCategory == article.categoryResId
        val matchesQuery =
            normalizedQuery.isEmpty() ||
                listOf(article.titleResId, article.summaryResId, article.sourceResId).any {
                    resources.getString(it).lowercase(serbianLocale).contains(normalizedQuery)
                }
        matchesCategory && matchesQuery
    }

    TokCategoryDrawer(
        drawerState = drawerState,
        selectedCategory = selectedCategory,
        onCategorySelected = { category ->
            selectedCategory = category
            coroutineScope.launch { drawerState.close() }
        },
        onClose = { coroutineScope.launch { drawerState.close() } },
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            TokTopBar(
                onMenuClick = { coroutineScope.launch { drawerState.open() } }
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
            Spacer(modifier = Modifier.height(22.dp))
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
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
            TrendingSection()
            Spacer(modifier = Modifier.height(26.dp))
            LatestSection(
                selectedCategory = selectedCategory,
                compactLayout = compactLayout,
                isRefreshing = isRefreshing,
                onCategorySelected = { selectedCategory = it },
                onCompactLayoutClick = { onCompactLayoutChange(!compactLayout) },
                onRefreshClick = {
                    if (!isRefreshing) {
                        coroutineScope.launch {
                            isRefreshing = true
                            delay(700)
                            isRefreshing = false
                        }
                    }
                },
            )
            Spacer(modifier = Modifier.height(14.dp))
            if (visibleArticles.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(
                        if (compactLayout) 10.dp else 18.dp
                    )
                ) {
                    visibleArticles.forEach { article ->
                        NewsArticleCard(
                            article = article,
                            compact = compactLayout,
                            isSaved = article.id in savedArticleIds,
                            onSaveClick = { onToggleSaved(article.id) },
                        )
                    }
                }
            } else {
                Text(
                    text = stringResource(
                        if (normalizedQuery.isEmpty()) {
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
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TokHomeScreenPreview() {
    NewsAgreggatorTheme {
        Surface {
            TokHomeScreen(
                savedArticleIds = emptySet(),
                compactLayout = false,
                onToggleSaved = {},
                onCompactLayoutChange = {},
            )
        }
    }
}
