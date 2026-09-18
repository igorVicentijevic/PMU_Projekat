package com.example.newsagreggator.ui.elements.screens

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.newsagreggator.R
import com.example.newsagreggator.ui.elements.composables.newsCategoryResIds
import com.example.newsagreggator.ui.elements.theme.NewsAgreggatorTheme
import com.example.newsagreggator.ui.elements.theme.TokCoral

@Composable
fun SettingsScreen(
    darkTheme: Boolean,
    compactLayout: Boolean,
    followedCategories: Set<Int>,
    refreshIntervalMinutes: Int,
    breakingNewsEnabled: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    onCompactLayoutChange: (Boolean) -> Unit,
    onRefreshIntervalChange: (Int) -> Unit,
    onBreakingNewsChange: (Boolean) -> Unit,
    onToggleCategory: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 28.dp)
    ) {
        Text(
            text = stringResource(R.string.settings_kicker),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelSmall,
        )
        Text(
            text = stringResource(R.string.nav_settings),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineLarge,
        )
        Text(
            text = stringResource(R.string.settings_subtitle),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(26.dp))
        Text(
            text = stringResource(R.string.settings_appearance),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            tonalElevation = 1.dp,
        ) {
            Column {
                SettingToggleRow(
                    iconResId = R.drawable.ic_moon,
                    titleResId = R.string.settings_dark_theme,
                    descriptionResId = R.string.settings_dark_theme_description,
                    checked = darkTheme,
                    onCheckedChange = onDarkThemeChange,
                    accentColor = MaterialTheme.colorScheme.primary,
                )
                HorizontalDivider(
                    modifier = Modifier.padding(start = 72.dp),
                    color = MaterialTheme.colorScheme.outline,
                )
                SettingToggleRow(
                    iconResId = R.drawable.ic_cards,
                    titleResId = R.string.settings_compact_layout,
                    descriptionResId = R.string.settings_compact_layout_description,
                    checked = compactLayout,
                    onCheckedChange = onCompactLayoutChange,
                    accentColor = TokCoral,
                )
            }
        }
        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = stringResource(R.string.settings_content_notifications),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            tonalElevation = 1.dp,
        ) {
            Column {
                RefreshIntervalRow(
                    selectedIntervalMinutes = refreshIntervalMinutes,
                    onIntervalSelected = onRefreshIntervalChange,
                )
                HorizontalDivider(
                    modifier = Modifier.padding(start = 72.dp),
                    color = MaterialTheme.colorScheme.outline,
                )
                SettingToggleRow(
                    iconResId = R.drawable.ic_bell,
                    titleResId = R.string.settings_breaking_news,
                    descriptionResId = R.string.settings_breaking_news_description,
                    checked = breakingNewsEnabled,
                    onCheckedChange = onBreakingNewsChange,
                    accentColor = TokCoral,
                )
            }
        }
        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = stringResource(R.string.settings_favorite_categories),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = stringResource(R.string.settings_favorite_categories_description),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(12.dp))
        FavoriteCategoryGrid(
            followedCategories = followedCategories,
            onToggleCategory = onToggleCategory,
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

private data class RefreshIntervalOption(
    val minutes: Int,
    @StringRes val labelResId: Int,
)

private val refreshIntervalOptions = listOf(
    RefreshIntervalOption(15, R.string.refresh_interval_15),
    RefreshIntervalOption(30, R.string.refresh_interval_30),
    RefreshIntervalOption(60, R.string.refresh_interval_60),
)

@Composable
private fun RefreshIntervalRow(
    selectedIntervalMinutes: Int,
    onIntervalSelected: (Int) -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val selectedOption = refreshIntervalOptions.firstOrNull {
        it.minutes == selectedIntervalMinutes
    } ?: refreshIntervalOptions.first()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_refresh),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(21.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.settings_auto_refresh),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = stringResource(R.string.settings_auto_refresh_description),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Box {
            Surface(
                onClick = { menuExpanded = true },
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            ) {
                Text(
                    text = "${stringResource(selectedOption.labelResId)} ▾",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
            ) {
                refreshIntervalOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(stringResource(option.labelResId)) },
                        onClick = {
                            onIntervalSelected(option.minutes)
                            menuExpanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoriteCategoryGrid(
    followedCategories: Set<Int>,
    onToggleCategory: (Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        newsCategoryResIds
            .drop(1)
            .chunked(2)
            .forEach { rowCategories ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    rowCategories.forEach { category ->
                        FilterChip(
                            selected = category in followedCategories,
                            onClick = { onToggleCategory(category) },
                            label = {
                                Text(
                                    text = stringResource(category),
                                    fontWeight = FontWeight.Medium,
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor =
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                                selectedLabelColor = MaterialTheme.colorScheme.primary,
                            ),
                        )
                    }
                    if (rowCategories.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
    }
}

@Composable
private fun SettingToggleRow(
    @DrawableRes iconResId: Int,
    @StringRes titleResId: Int,
    @StringRes descriptionResId: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    accentColor: androidx.compose.ui.graphics.Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(accentColor.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(iconResId),
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(21.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(titleResId),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = stringResource(descriptionResId),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Preview(name = "Podešavanja - svetla tema", showBackground = true)
@Composable
private fun SettingsLightPreview() {
    NewsAgreggatorTheme(darkTheme = false) {
        Surface {
            SettingsScreen(
                darkTheme = false,
                compactLayout = false,
                followedCategories = setOf(
                    R.string.category_serbia,
                    R.string.category_technology,
                    R.string.category_world,
                ),
                refreshIntervalMinutes = 15,
                breakingNewsEnabled = true,
                onDarkThemeChange = {},
                onCompactLayoutChange = {},
                onRefreshIntervalChange = {},
                onBreakingNewsChange = {},
                onToggleCategory = {},
            )
        }
    }
}

@Preview(name = "Podešavanja - tamna tema", showBackground = true)
@Composable
private fun SettingsDarkPreview() {
    NewsAgreggatorTheme(darkTheme = true) {
        Surface {
            SettingsScreen(
                darkTheme = true,
                compactLayout = true,
                followedCategories = emptySet(),
                refreshIntervalMinutes = 60,
                breakingNewsEnabled = false,
                onDarkThemeChange = {},
                onCompactLayoutChange = {},
                onRefreshIntervalChange = {},
                onBreakingNewsChange = {},
                onToggleCategory = {},
            )
        }
    }
}
