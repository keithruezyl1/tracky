package com.tracky.app.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tracky.app.ui.theme.TrackyColors
import com.tracky.app.ui.theme.TrackyTokens
import com.tracky.app.ui.theme.TrackyTypography
import androidx.compose.ui.text.AnnotatedString

/**
 * Tracky Top App Bar
 * 
 * Clean top app bar with consistent styling.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackyTopBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = TrackyTypography.HeadlineMedium,
                color = TrackyColors.TextPrimary
            )
        },
        modifier = modifier,
        navigationIcon = navigationIcon ?: {},
        actions = actions,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = TrackyColors.Background,
            navigationIconContentColor = TrackyColors.TextPrimary,
            titleContentColor = TrackyColors.TextPrimary,
            actionIconContentColor = TrackyColors.TextSecondary
        ),
        windowInsets = WindowInsets.statusBars
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackyTopBar(
    title: AnnotatedString,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = TrackyTypography.HeadlineMedium,
                color = TrackyColors.TextPrimary
            )
        },
        modifier = modifier,
        navigationIcon = navigationIcon ?: {},
        actions = actions,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = TrackyColors.Background,
            navigationIconContentColor = TrackyColors.TextPrimary,
            titleContentColor = TrackyColors.TextPrimary,
            actionIconContentColor = TrackyColors.TextSecondary
        ),
        windowInsets = WindowInsets.statusBars
    )
}

/**
 * Tracky Top Bar with Back Navigation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackyTopBarWithBack(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TrackyTopBar(
        title = title,
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = TrackyColors.TextPrimary
                )
            }
        },
        actions = actions
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackyTopBarWithBack(
    title: AnnotatedString,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TrackyTopBar(
        title = title,
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = TrackyColors.TextPrimary
                )
            }
        },
        actions = actions
    )
}

/**
 * Tracky Top Bar with Close Button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackyTopBarWithClose(
    title: String,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TrackyTopBar(
        title = title,
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onCloseClick) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Close",
                    tint = TrackyColors.TextPrimary
                )
            }
        },
        actions = actions
    )
}
