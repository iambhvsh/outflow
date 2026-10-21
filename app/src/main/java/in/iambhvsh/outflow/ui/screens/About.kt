@file:Suppress("FunctionName", "OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")

package `in`.iambhvsh.outflow.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Engineering
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.iambhvsh.outflow.BuildConfig
import `in`.iambhvsh.outflow.ui.components.AppCard
import `in`.iambhvsh.outflow.ui.components.AppHeading
import `in`.iambhvsh.outflow.ui.components.AppItem
import `in`.iambhvsh.outflow.ui.theme.HeroFont
import `in`.iambhvsh.outflow.ui.theme.OutflowColors
import `in`.iambhvsh.outflow.ui.theme.OutflowShapes

private const val Repository = "https://github.com/iambhvsh/outflow"
private const val Issues = "$Repository/issues"
private const val Profile = "https://github.com/iambhvsh"
private const val Licenses = "https://www.gnu.org/licenses/"

/** The launcher foreground is drawn with the adaptive icon's safe margin, which its mask crops away. */
private const val Crop = 1.45f

/** Clean, professional About screen following Apple tone and design principles. */
@Composable
fun About(
    padding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uri = LocalUriHandler.current
    var license by remember { mutableStateOf(false) }
    val cleanVersion = BuildConfig.VERSION_NAME.substringBefore("-")

    val launcherIconRes = remember(context) {
        val id = context.resources.getIdentifier("ic_launcher_foreground", "mipmap", context.packageName)
        if (id != 0) id else context.applicationInfo.icon
    }
    val githubIconRes = remember(context) {
        context.resources.getIdentifier("github", "drawable", context.packageName)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OutflowColors.group)
            .padding(horizontal = 16.dp),
        contentPadding = padding,
        verticalArrangement = Arrangement.spacedBy(OutflowShapes.Gap)
    ) {
        item(key = "top") { Spacer(Modifier.height(12.dp)) }

        // Hero Card
        item(key = "hero") {
            AppCard(index = 0, count = 1) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(MaterialShapes.Cookie12Sided.toShape())
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(launcherIconRes),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .scale(Crop)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "Outflow",
                            fontFamily = HeroFont,
                            fontSize = 28.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                    .padding(horizontal = 9.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "v$cleanVersion",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                    .padding(horizontal = 9.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "Vibe Coded",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    FilledTonalIconButton(
                        onClick = { uri.openUri(Repository) },
                        shapes = IconButtonDefaults.shapes()
                    ) {
                        Icon(painterResource(githubIconRes), contentDescription = "GitHub")
                    }
                }
                Text(
                    text = "A simple, local-first finance companion designed with Material 3 Expressive.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // The Story
        item(key = "story-heading") { AppHeading("The Story") }
        item(key = "story") {
            AppCard(index = 0, count = 1) {
                Text(
                    text = "Most expense trackers are cluttered with ads, complex menus, or paywalls. Outflow is designed to be the opposite: fast, private, and intentionally simple.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "The journey started in 2024 with Xpense, a prototype built with React and Capacitor. While functional, it lacked the tactile responsiveness that only a native app can deliver.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Outflow is the native reimagining. Vibe coded in Kotlin and Jetpack Compose, built alongside Google Antigravity AI Agents, and refined using the Reverse Engineer Anything (REA) skill. Built around Google's Material 3 Expressive guidelines with fluid spring physics, organic shapes, and native 120Hz performance.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Zero accounts. Zero cloud servers. Your financial data stays on your device, always.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Craft
        item(key = "craft-heading") { AppHeading("Craft") }
        item(key = "tech-android") {
            AppItem(
                title = "Kotlin & Jetpack Compose",
                index = 0,
                count = 4,
                icon = Icons.Rounded.Android,
                description = "Pure native Android with 120Hz spring physics"
            )
        }
        item(key = "tech-m3") {
            AppItem(
                title = "Material 3 Expressive",
                index = 1,
                count = 4,
                icon = Icons.Rounded.Palette,
                description = "Adaptive shapes, fluid motion, and dynamic color"
            )
        }
        item(key = "tech-ai") {
            AppItem(
                title = "Antigravity AI & REA",
                index = 2,
                count = 4,
                icon = Icons.Rounded.AutoAwesome,
                description = "Autonomous AI pair-programming and design system forensics"
            )
        }
        item(key = "tech-privacy") {
            AppItem(
                title = "Local-First Architecture",
                index = 3,
                count = 4,
                icon = Icons.Rounded.Security,
                description = "100% on-device privacy powered by Room"
            )
        }

        // Project & Creator
        item(key = "project-heading") { AppHeading("Project") }
        item(key = "developer") {
            AppItem(
                title = "Bhavesh Patil",
                index = 0,
                count = 4,
                icon = Icons.Rounded.Person,
                description = "Creator and developer",
                onClick = { uri.openUri(Profile) }
            )
        }
        item(key = "source") {
            AppItem(
                title = "Source code",
                index = 1,
                count = 4,
                description = "github.com/iambhvsh/outflow",
                icon = Icons.Rounded.Code,
                onClick = { uri.openUri(Repository) }
            )
        }
        item(key = "issues") {
            AppItem(
                title = "Report an issue",
                index = 2,
                count = 4,
                description = "Feedback, suggestions, and bug reports",
                icon = Icons.Rounded.BugReport,
                onClick = { uri.openUri(Issues) }
            )
        }
        item(key = "license") {
            AppItem(
                title = "License",
                index = 3,
                count = 4,
                description = "GNU General Public License v3.0",
                icon = Icons.Rounded.Gavel,
                onClick = { license = true }
            )
        }

        item(key = "bottom-space") {
            Spacer(Modifier.height(100.dp))
        }
    }

    if (license) LicenseSheet(onDismiss = { license = false })
}

/** The notice the GPL asks a program to carry, with the full text a tap away. */
@Composable
private fun LicenseSheet(onDismiss: () -> Unit) {
    val accent = MaterialTheme.colorScheme.primary
    val paragraphs = remember(accent) { notice(accent) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = OutflowColors.sheet
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(text = "License", fontFamily = HeroFont, fontSize = 28.sp)
            }
            items(paragraphs) { paragraph ->
                Text(
                    text = paragraph,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun notice(accent: Color): List<AnnotatedString> = listOf(
    AnnotatedString("Copyright © 2026 Bhavesh Patil"),
    AnnotatedString(
        "Outflow is free software: you can redistribute it and modify it under the terms of the GNU " +
            "General Public License as published by the Free Software Foundation, either version 3 of " +
            "the License, or (at your option) any later version."
    ),
    AnnotatedString(
        "It is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even " +
            "the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU " +
            "General Public License for more details."
    ),
    buildAnnotatedString {
        append("You should have received a copy of the GNU General Public License along with Outflow. If not, see ")
        withLink(
            LinkAnnotation.Url(
                url = Licenses,
                styles = TextLinkStyles(
                    SpanStyle(color = accent, textDecoration = TextDecoration.Underline)
                )
            )
        ) {
            append("gnu.org/licenses")
        }
        append(".")
    }
)
