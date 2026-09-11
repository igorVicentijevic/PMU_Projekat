package com.example.newsagreggator.ui.elements.composables

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.newsagreggator.R
import com.example.newsagreggator.ui.theme.TokCoral
import com.example.newsagreggator.ui.theme.TokGreen

@Composable
fun TokCategoryDrawer(
    drawerState: DrawerState,
    selectedCategory: Int,
    onCategorySelected: (Int) -> Unit,
    onDigestClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onClose: () -> Unit,
    content: @Composable () -> Unit,
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .width(310.dp)
                    .fillMaxHeight(),
                drawerContainerColor = MaterialTheme.colorScheme.background,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 18.dp, vertical = 16.dp)
                ) {
                    DrawerHeader(onClose)
                    Spacer(modifier = Modifier.height(24.dp))
                    DrawerGreeting()
                    Spacer(modifier = Modifier.height(20.dp))
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = stringResource(R.string.drawer_digest),
                                fontWeight = FontWeight.Bold,
                            )
                        },
                        selected = false,
                        onClick = onDigestClick,
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_spark),
                                contentDescription = null,
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                    )
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = stringResource(R.string.drawer_history),
                                fontWeight = FontWeight.Bold,
                            )
                        },
                        selected = false,
                        onClick = onHistoryClick,
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_history),
                                contentDescription = null,
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = stringResource(R.string.drawer_categories),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 12.dp),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    newsCategoryResIds.forEach { category ->
                        NavigationDrawerItem(
                            label = {
                                Text(
                                    text = stringResource(category),
                                    fontWeight = if (category == selectedCategory) {
                                        FontWeight.Bold
                                    } else {
                                        FontWeight.Medium
                                    },
                                )
                            },
                            selected = category == selectedCategory,
                            onClick = { onCategorySelected(category) },
                            icon = {
                                Box(
                                    modifier = Modifier
                                        .size(9.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (category == selectedCategory) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.outline
                                            }
                                        )
                                )
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor =
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                            ),
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(TokGreen)
                        )
                        Column {
                            Text(
                                text = stringResource(R.string.drawer_offline_title),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Text(
                                text = stringResource(R.string.drawer_offline_subtitle),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
                }
            }
        },
        content = content,
    )
}

@Composable
private fun DrawerHeader(onClose: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(9.dp, 9.dp, 9.dp, 3.dp))
                    .background(TokGreen),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "T",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(
                text = "tok.",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
            )
        }
        IconButton(onClick = onClose) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = stringResource(R.string.close_categories),
            )
        }
    }
}

@Composable
private fun DrawerGreeting() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(TokCoral.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "T",
                color = TokCoral,
                fontWeight = FontWeight.Bold,
            )
        }
        Column {
            Text(
                text = stringResource(R.string.drawer_greeting),
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.drawer_subtitle),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
