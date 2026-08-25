@file:Suppress("OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.Person
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.iambhvsh.outflow.BuildConfig
import `in`.iambhvsh.outflow.R
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

private val Badge = 64.dp


private val Lead = 16.dp

/** The launcher foreground is drawn with the adaptive icon's safe margin, which its mask crops away. */
private const val Crop = 1.5f

/** What the app is, who wrote it, and where the source and the licence are. */
@Composable
fun About(
    padding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val uri = LocalUriHandler.current
    var license by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OutflowColors.group)
            .padding(horizontal = 16.dp),
        contentPadding = padding,
        verticalArrangement = Arrangement.spacedBy(OutflowShapes.Gap)
    ) {
        item(key = "top") { Spacer(Modifier.height(20.dp)) }

        item(key = "app") {
            AppCard(index = 0, count = 2) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(Badge)
                            .clip(MaterialShapes.Cookie12Sided.toShape())
                            .background(Color.White)
                    ) {
                        Image(
                            painter = painterResource(R.mipmap.ic_launcher_foreground),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .scale(Crop)
                        )
                    }
                    Spacer(Modifier.width(Lead))
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "Outflow",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    FilledTonalIconButton(
                        onClick = { uri.openUri(Repository) },
                        shapes = IconButtonDefaults.shapes()
                    ) {
                        Icon(painterResource(R.drawable.github), contentDescription = "GitHub")
                    }
                }
            }
        }

        item(key = "developer") {
            AppCard(index = 1, count = 2) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(Badge)
                            .clip(MaterialShapes.Square.toShape())
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(Modifier.width(Lead))
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "Bhavesh Patil",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Developer",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    FilledTonalIconButton(
                        onClick = { uri.openUri(Profile) },
                        shapes = IconButtonDefaults.shapes()
                    ) {
                        Icon(painterResource(R.drawable.github), contentDescription = "iambhvsh on GitHub")
                    }
                }
            }
        }

        item(key = "project-heading") { AppHeading("Project") }
        item(key = "source") {
            AppItem(
                title = "Source code",
                index = 0,
                count = 3,
                description = "github.com/iambhvsh/outflow",
                icon = Icons.Rounded.Code,
                onClick = { uri.openUri(Repository) }
            )
        }
        item(key = "issues") {
            AppItem(
                title = "Report an issue",
                index = 1,
                count = 3,
                description = "Bugs and ideas go on the tracker",
                icon = Icons.Rounded.BugReport,
                onClick = { uri.openUri(Issues) }
            )
        }
        item(key = "license") {
            AppItem(
                title = "License",
                index = 2,
                count = 3,
                description = "GNU General Public License v3.0",
                icon = Icons.Rounded.Gavel,
                onClick = { license = true }
            )
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
