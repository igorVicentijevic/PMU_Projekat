package com.example.newsagreggator.ui.elements

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.newsagreggator.R
import com.example.newsagreggator.ui.elements.screens.PlaceholderScreen
import com.example.newsagreggator.ui.elements.screens.TokHomeScreen

private enum class TokTab(
    @StringRes val labelResId: Int,
    @DrawableRes val iconResId: Int,
) {
    Home(R.string.nav_home, R.drawable.ic_home),
    ForYou(R.string.nav_for_you, R.drawable.ic_spark),
    Saved(R.string.nav_saved, R.drawable.ic_bookmark_outline),
    Settings(R.string.nav_settings, R.drawable.ic_settings),
}

@Composable
fun TokApp(modifier: Modifier = Modifier) {
    var selectedTab by rememberSaveable { mutableStateOf(TokTab.Home) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = androidx.compose.ui.unit.Dp.Unspecified,
            ) {
                TokTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = tab == selectedTab,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                painter = painterResource(tab.iconResId),
                                contentDescription = null,
                            )
                        },
                        label = { Text(stringResource(tab.labelResId)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor =
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        ),
                    )
                }
            }
        },
    ) { innerPadding ->
        when (selectedTab) {
            TokTab.Home -> TokHomeScreen(modifier = Modifier.padding(innerPadding))
            TokTab.ForYou -> PlaceholderScreen(
                kickerResId = R.string.for_you_kicker,
                titleResId = R.string.nav_for_you,
                bodyResId = R.string.for_you_placeholder,
                modifier = Modifier.padding(innerPadding),
            )
            TokTab.Saved -> PlaceholderScreen(
                kickerResId = R.string.saved_kicker,
                titleResId = R.string.nav_saved,
                bodyResId = R.string.saved_placeholder,
                modifier = Modifier.padding(innerPadding),
            )
            TokTab.Settings -> PlaceholderScreen(
                kickerResId = R.string.settings_kicker,
                titleResId = R.string.nav_settings,
                bodyResId = R.string.settings_placeholder,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}
