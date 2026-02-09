package com.tracky.app.ui.screens.features

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Scale
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.tracky.app.ui.components.TrackyBodySmall
import com.tracky.app.ui.components.TrackyBodyText
import com.tracky.app.ui.components.TrackyCard
import com.tracky.app.ui.components.TrackySectionTitle
import com.tracky.app.ui.components.TrackyTopBarWithBack
import com.tracky.app.ui.theme.TrackyColors
import com.tracky.app.ui.theme.TrackyTokens
import com.tracky.app.ui.theme.TrackyTypography

@Composable
fun FeaturesScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TrackyTopBarWithBack(
                title = "Welcome to Tracky",
                onBackClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(TrackyColors.Background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = TrackyTokens.Spacing.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.L)
        ) {
            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.XS))

            // Features List
            FeatureSection(
                title = "Effortless Logging",
                icon = Icons.Outlined.Restaurant,
                features = listOf(
                    "Chat-Style Logging" to "Just type what you ate like you're texting a friend, like \"Ate 2 eggs and toast\"!",
                    "Visual Recognition" to "Snap a photo of your meal. Tracky identifies the food and estimates macros for you.",
                    "Auto-Detection" to "Tracky knows if you're logging a workout or a meal automatically.",
                    "Smart Drafts" to "Review every entry before you log it. You are always in control.",
                    "Self-Learning" to "Tracky's database learns from your edits. Prioritizes your personal history and overrides."
                )
            )

            FeatureSection(
                title = "Smart Dashboard",
                icon = Icons.Outlined.AutoAwesome,
                features = listOf(
                    "6-Day Strip" to "Swipe to see your past week's consistency at a glance.",
                    "Live Rings" to "Visual progress rings for Calories, Protein, Carbs, and Fat.",
                    "Quick Goals" to "Tap your daily summary to adjust your goals on the fly."
                )
            )

            FeatureSection(
                title = "Deep Insights",
                icon = Icons.Outlined.History,
                features = listOf(
                    "Re-Analyze" to "Tap any chat message to edit the text and completely re-process the entry.",
                    "Swipe History" to "Browse your entire timeline. Swipe left to delete entries.",
                    "Full Breakdown" to "Tap any entry card to see your entry's details."
                )
            )

            FeatureSection(
                title = "Weight Management",
                icon = Icons.Outlined.Scale,
                features = listOf(
                    "Interactive Charts" to "Visualize weight trends over Daily, Weekly, Monthly, or All Time ranges.",
                    "Dynamic Goals" to "See exactly how much you have left to lose (or gain) to hit your target."
                )
            )



            // Spite Message
            TrackyCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.M)
                ) {
                    Text(
                        text = "Why Tracky Exists",
                        style = TrackyTypography.HeadlineMedium,
                        color = TrackyColors.TextPrimary
                    )
                    
                    Text(
                        text = "This app was developed out of spite.",
                        style = TrackyTypography.BodyLarge,
                        color = TrackyColors.TextSecondary
                    )

                    Text(
                        text = buildAnnotatedString {
                            append("The developer was asked to pay 800 pesos for a calorie tracking app. He said \"")
                            withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                                append("nuh uh")
                            }
                            append("\" and created Tracky instead.")
                        },
                        style = TrackyTypography.BodyLarge,
                        color = TrackyColors.TextSecondary
                    )
                    
                    Text(
                        text = "The developer will most likely never reach his weight goals, but atleast he has Tracky for his portfolio.",
                        style = TrackyTypography.BodyLarge,
                        color = TrackyColors.TextSecondary
                    )

                    Text(
                        text = "Thank you for using Tracky! :)",
                        style = TrackyTypography.BodyLarge,
                        color = TrackyColors.BrandPrimary,
                        modifier = Modifier.padding(top = TrackyTokens.Spacing.S),
                        textAlign = TextAlign.Center
                    )

                    // Developer Socials
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.XS)
                    ) {
                        Text(
                            text = "Follow me on Facebook and LinkedIn!",
                            style = TrackyTypography.BodyLarge,
                            color = TrackyColors.TextPrimary
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.S)) {
                            val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
                            val iconTint = if (androidx.compose.foundation.isSystemInDarkTheme()) androidx.compose.ui.graphics.Color.White else androidx.compose.ui.graphics.Color.Black
                            
                            androidx.compose.material3.IconButton(onClick = { uriHandler.openUri("https://www.facebook.com/sikeithni/") }) {
                                Icon(
                                    painter = androidx.compose.ui.res.painterResource(id = com.tracky.app.R.drawable.ic_facebook),
                                    contentDescription = "Facebook",
                                    tint = iconTint,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            androidx.compose.material3.IconButton(onClick = { uriHandler.openUri("https://www.linkedin.com/in/keith-tagarao/") }) {
                                Icon(
                                    painter = androidx.compose.ui.res.painterResource(id = com.tracky.app.R.drawable.ic_linkedin),
                                    contentDescription = "LinkedIn",
                                    tint = iconTint,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.XL))
        }
    }
}

@Composable
private fun FeatureSection(
    title: String,
    icon: ImageVector,
    features: List<Pair<String, String>>
) {
    Column(verticalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.M)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.S)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TrackyColors.BrandPrimary,
                modifier = Modifier.size(24.dp)
            )
            TrackySectionTitle(text = title)
        }

        TrackyCard {
            Column(verticalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.L)) {
                features.forEach { (name, description) ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = name,
                            style = TrackyTypography.BodyLarge,
                            color = TrackyColors.TextPrimary
                        )
                        TrackyBodySmall(
                            text = description,
                            color = TrackyColors.TextSecondary
                        )
                    }
                }
            }
        }
    }
}