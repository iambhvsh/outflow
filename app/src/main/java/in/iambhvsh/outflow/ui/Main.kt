@file:Suppress("OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")

package `in`.iambhvsh.outflow.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import `in`.iambhvsh.outflow.data.TransactionEntity
import `in`.iambhvsh.outflow.ui.components.AppNavigation
import `in`.iambhvsh.outflow.ui.components.AppToolbar
import `in`.iambhvsh.outflow.ui.screens.About
import `in`.iambhvsh.outflow.ui.screens.Home
import `in`.iambhvsh.outflow.ui.screens.Insight
import `in`.iambhvsh.outflow.ui.screens.Settings
import `in`.iambhvsh.outflow.ui.screens.TransactionSheet
import `in`.iambhvsh.outflow.ui.theme.OutflowColors
import `in`.iambhvsh.outflow.ui.theme.OutflowTheme
import `in`.iambhvsh.outflow.ui.utils.currency
import `in`.iambhvsh.outflow.ui.utils.screenPadding

class Main : ComponentActivity() {

    private val model: AppModel by viewModels()

    /**
     * Nothing is drawn until the stored settings have been read, so the first frame is already in the
     * chosen theme rather than re-theming itself a moment after launch.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        val launch = installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)


        val options = model.options
        launch.setKeepOnScreenCondition { options.value == null }

        setContent {
            val saved by options.collectAsStateWithLifecycle()
            val settings = saved ?: return@setContent
            SideEffect {
                currency = settings.currency
            }
            OutflowTheme(
                themeMode = settings.themeMode,
                themeColor = settings.themeColor,
                isDynamicColor = settings.isDynamicColor
            ) {
                App(model)
            }
        }
    }
}

/**
 * The whole app under one scaffold: a top bar that scrolls away, a floating toolbar fixed above the
 * navigation bar, and whichever destination is current in between. Only one [Scaffold] — a nested
 * second would apply the status bar inset twice.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun App(model: AppModel) {
    var current by remember { mutableStateOf(Screen.HOME) }
    var about by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<TransactionEntity?>(null) }
    var sheetOpen by remember { mutableStateOf(false) }

    val deleted by model.deleted.collectAsStateWithLifecycle()

    val topBarScroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val snackbar = remember { SnackbarHostState() }

    val direction = LocalLayoutDirection.current
    val systemBars = WindowInsets.systemBars.asPaddingValues()
    val cutout = WindowInsets.displayCutout.asPaddingValues()

    BackHandler(enabled = about || current != Screen.HOME) {
        if (about) about = false else current = Screen.HOME
    }


    LaunchedEffect(current, about) {
        topBarScroll.state.heightOffset = 0f
        topBarScroll.state.contentOffset = 0f
    }

    LaunchedEffect(deleted) {
        val transaction = deleted ?: return@LaunchedEffect
        val result = snackbar.showSnackbar(
            message = "Deleted ${transaction.title.ifBlank { "flow" }}",
            actionLabel = "Undo",
            withDismissAction = true
        )
        if (result == SnackbarResult.ActionPerformed) model.restore() else model.forget()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(topBarScroll.nestedScrollConnection),
        topBar = {
            AppToolbar(
                title = if (about) {
                    "About"
                } else {
                    when (current) {
                        Screen.HOME -> "Outflow"
                        Screen.INSIGHT -> "Insights"
                        Screen.SETTINGS -> "Settings"
                    }
                },
                scrollBehavior = topBarScroll,
                onBack = if (about) ({ about = false }) else null
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = cutout.calculateStartPadding(direction),
                        end = cutout.calculateEndPadding(direction)
                    ),
                contentAlignment = Alignment.Center
            ) {
                AppNavigation(
                    current = current,
                    onSelect = {
                        about = false
                        current = it
                    },
                    onAdd = {
                        editing = null
                        sheetOpen = true
                    },
                    modifier = Modifier.padding(
                        top = FloatingToolbarDefaults.ScreenOffset,
                        bottom = systemBars.calculateBottomPadding() +
                            FloatingToolbarDefaults.ScreenOffset
                    )
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = OutflowColors.group
    ) { padding ->
        val content = screenPadding(padding)
        AnimatedContent(
            targetState = current,
            transitionSpec = { slide(targetState.ordinal > initialState.ordinal) },
            label = "Destination"
        ) { screen ->
            when (screen) {
                Screen.HOME -> Home(
                    model = model,
                    padding = content,
                    onEdit = {
                        editing = it
                        sheetOpen = true
                    }
                )

                Screen.INSIGHT -> Insight(model = model, padding = content)
                Screen.SETTINGS -> AnimatedContent(
                    targetState = about,
                    transitionSpec = { slide(targetState) },
                    label = "About"
                ) { open ->
                    if (open) {
                        About(padding = content)
                    } else {
                        Settings(
                            model = model,
                            padding = content,
                            onAbout = { about = true }
                        )
                    }
                }
            }
        }
    }

    if (sheetOpen) {
        TransactionSheet(
            existing = editing,
            onSave = model::save,
            onDismiss = { sheetOpen = false }
        )
    }
}

/** Destinations slide in from the side they sit on in the toolbar, so the move reads as lateral. */
private fun slide(forward: Boolean) = (
    slideInHorizontally { width -> if (forward) width / 6 else -width / 6 } + fadeIn()
    ) togetherWith (
    slideOutHorizontally { width -> if (forward) -width / 6 else width / 6 } + fadeOut()
    )
