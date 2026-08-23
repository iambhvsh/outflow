@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE", "OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")
package `in`.iambhvsh.outflow.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.layout.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.iambhvsh.outflow.R
import `in`.iambhvsh.outflow.data.TransactionEntity
import `in`.iambhvsh.outflow.data.TransactionType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

import androidx.compose.animation.core.animateDpAsState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
class MainActivity : ComponentActivity() {
    private val viewModel: OutflowViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)
        setContent {
            val pureWhite = Color(0xFFFFFFFF)
            val pitchBlack = Color(0xFF000000)
            val darkGray = Color(0xFF1E1E1E)
            val mediumGray = Color(0xFF757575)

            val colorScheme = darkColorScheme(
                primary = pureWhite,
                onPrimary = pitchBlack,
                primaryContainer = darkGray,
                onPrimaryContainer = pureWhite,
                secondary = mediumGray,
                onSecondary = pitchBlack,
                secondaryContainer = mediumGray,
                onSecondaryContainer = pureWhite,
                background = pitchBlack,
                surface = pitchBlack,
                surfaceVariant = darkGray,
                onSurfaceVariant = pureWhite,
                errorContainer = Color(0xFF550000),
                onErrorContainer = Color(0xFFFF5555)
            )

            val googleSansFlex = FontFamily(
                Font(
                    R.font.google_sans_flex,
                    FontWeight.Normal,
                    variationSettings = FontVariation.Settings(FontVariation.weight(400))
                ),
                Font(
                    R.font.google_sans_flex,
                    FontWeight.Bold,
                    variationSettings = FontVariation.Settings(
                        FontVariation.weight(600),
                        FontVariation.Setting("ROND", 100f)
                    )
                ),
                Font(
                    R.font.google_sans_flex,
                    FontWeight.Black,
                    variationSettings = FontVariation.Settings(
                        FontVariation.weight(900),
                        FontVariation.width(112.5f),
                        FontVariation.Setting("ROND", 35f)
                    )
                )
            )

            val feat = "ss02, dlig"
            val base = MaterialTheme.typography
            val typography = base.copy(
                displayLarge = base.displayLarge.copy(
                    fontFamily = googleSansFlex,
                    fontFeatureSettings = feat,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-2.0).sp,
                    lineHeight = 64.sp
                ),
                displayMedium = base.displayMedium.copy(fontFamily = googleSansFlex, fontFeatureSettings = feat),
                displaySmall = base.displaySmall.copy(fontFamily = googleSansFlex, fontFeatureSettings = feat),
                headlineLarge = base.headlineLarge.copy(fontFamily = googleSansFlex, fontFeatureSettings = feat),
                headlineMedium = base.headlineMedium.copy(
                    fontFamily = googleSansFlex,
                    fontFeatureSettings = feat,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1.0).sp
                ),
                headlineSmall = base.headlineSmall.copy(fontFamily = googleSansFlex, fontFeatureSettings = feat),
                titleLarge = base.titleLarge.copy(
                    fontFamily = googleSansFlex,
                    fontFeatureSettings = feat,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                ),
                titleMedium = base.titleMedium.copy(fontFamily = googleSansFlex, fontFeatureSettings = feat),
                titleSmall = base.titleSmall.copy(fontFamily = googleSansFlex, fontFeatureSettings = feat),
                bodyLarge = base.bodyLarge.copy(fontFamily = googleSansFlex, fontFeatureSettings = feat),
                bodyMedium = base.bodyMedium.copy(fontFamily = googleSansFlex, fontFeatureSettings = feat),
                bodySmall = base.bodySmall.copy(fontFamily = googleSansFlex, fontFeatureSettings = feat),
                labelLarge = base.labelLarge.copy(fontFamily = googleSansFlex, fontFeatureSettings = feat),
                labelMedium = base.labelMedium.copy(fontFamily = googleSansFlex, fontFeatureSettings = feat),
                labelSmall = base.labelSmall.copy(fontFamily = googleSansFlex, fontFeatureSettings = feat)
            )

            MaterialExpressiveTheme(
                colorScheme = colorScheme,
                typography = typography,
                motionScheme = MotionScheme.expressive(),
                shapes = MaterialTheme.shapes.copy(
                    extraLarge = RoundedCornerShape(42.dp),
                    large = RoundedCornerShape(32.dp)
                )
            ) {
                OutflowApp(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OutflowApp(viewModel: OutflowViewModel) {
    val transactions by viewModel.transactions.collectAsState()
    val summary by viewModel.summary.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val isScrollingUp = listState.firstVisibleItemScrollOffset > 0 || listState.firstVisibleItemIndex > 0

    Scaffold(
        floatingActionButton = {
            val fabInteractionSource = remember { MutableInteractionSource() }
            val isFabPressed by fabInteractionSource.collectIsPressedAsState()
            val isFabHovered by fabInteractionSource.collectIsHoveredAsState()
            val fabCorner by animateDpAsState(
                targetValue = when {
                    isFabPressed -> 12.dp
                    isFabHovered -> 40.dp
                    else -> 32.dp
                },
                animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
                label = "FabCorner"
            )
            ExtendedFloatingActionButton(
                text = { Text("New Entry", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                onClick = { showBottomSheet = true },
                expanded = !isScrollingUp,
                containerColor = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(fabCorner),
                interactionSource = fabInteractionSource
            )
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Outflow", style = MaterialTheme.typography.headlineMedium) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = MaterialTheme.shapes.extraLarge,
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.animateContentSize(
                            animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
                        )
                    ) {
                        Text(
                            text = "Net Balance",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = money(summary.net),
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Spacer(modifier = Modifier.height(28.dp))
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f)
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            SummaryColumn(
                                label = "In",
                                value = signed(summary.inflow, positive = true),
                                valueColor = Color(0xFF4CAF50),
                                modifier = Modifier.weight(1f)
                            )
                            VerticalDivider(
                                modifier = Modifier.height(40.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f)
                            )
                            SummaryColumn(
                                label = "Out",
                                value = signed(summary.outflow, positive = false),
                                valueColor = Color(0xFFF44336),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items = transactions, key = { it.id }) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onDelete = { viewModel.delete(transaction) }
                    )
                }
            }
        }
    }

    AnimatedVisibility(
        visible = showBottomSheet,
        enter = androidx.compose.animation.fadeIn(),
        exit = androidx.compose.animation.fadeOut()
    ) {
        androidx.activity.compose.BackHandler { showBottomSheet = false }
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { showBottomSheet = false }
                    )
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .debouncedImePadding()
                    .animateEnterExit(
                        enter = androidx.compose.animation.slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)
                        ),
                        exit = androidx.compose.animation.slideOutVertically(
                            targetOffsetY = { it },
                            animationSpec = tween(200)
                        )
                    )
            ) {
                Surface(
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AddTransactionSheetContent(
                        onConfirm = { title, amount, category, type ->
                            viewModel.insert(title, amount, category, type)
                            showBottomSheet = false
                        },
                        onCancel = {
                            showBottomSheet = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TransactionItem(transaction: TransactionEntity, onDelete: () -> Unit) {
    var isDeleted by remember { mutableStateOf(false) }

    val categoryColors = listOf(
        Color(0xFFFFFFFF), Color(0xFFCCCCCC), Color(0xFF999999), Color(0xFF666666), Color(0xFF333333)
    )
    val categoryColor = categoryColors[transaction.category.hashCode().mod(categoryColors.size)]

    val isInflow = transaction.type == TransactionType.INFLOW

    val dismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
            isDeleted = true
            delay(300)
            onDelete()
        }
    }

    val cardInteractionSource = remember { MutableInteractionSource() }
    val isCardPressed by cardInteractionSource.collectIsPressedAsState()
    val isCardHovered by cardInteractionSource.collectIsHoveredAsState()
    val cardCorner by animateDpAsState(
        targetValue = when {
            isCardPressed -> 16.dp
            isCardHovered -> 40.dp
            else -> 32.dp
        },
        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
        label = "CardCorner"
    )
    val cardShape = RoundedCornerShape(cardCorner)

    AnimatedVisibility(
        visible = !isDeleted,
        exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(animationSpec = tween(300))
    ) {
        SwipeToDismissBox(
            state = dismissState,
            backgroundContent = {
                val color = MaterialTheme.colorScheme.errorContainer
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(cardShape)
                        .background(color, cardShape)
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            },
            content = {
                Card(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(),
                    shape = cardShape,
                    interactionSource = cardInteractionSource,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(categoryColor, RoundedCornerShape(50))
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = transaction.title,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isInflow) "Inflow · ${transaction.category}" else transaction.category,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }
                        Text(
                            text = signed(transaction.amount, positive = isInflow),
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                            color = if (isInflow) Color(0xFF4CAF50) else Color(0xFFF44336)
                        )
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AddTransactionSheetContent(
    onConfirm: (String, Double, String, TransactionType) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(TransactionType.OUTFLOW) }

    val submit: () -> Unit = {
        val amountVal = amount.toDoubleOrNull() ?: 0.0
        if (title.isNotBlank() && category.isNotBlank() && amountVal > 0) {
            onConfirm(title, amountVal, category, type)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = if (type == TransactionType.INFLOW) "New Inflow" else "New Outflow",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black)
        )

        TypeToggle(selected = type, onSelect = { type = it })

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = androidx.compose.ui.text.input.ImeAction.Next
            ),
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = androidx.compose.ui.text.input.ImeAction.Next
            ),
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = androidx.compose.ui.text.input.ImeAction.Done
            ),
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                onDone = { submit() }
            ),
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val saveInteractionSource = remember { MutableInteractionSource() }
            val isSavePressed by saveInteractionSource.collectIsPressedAsState()
            val isSaveHovered by saveInteractionSource.collectIsHoveredAsState()
            val saveCorner by animateDpAsState(
                targetValue = when {
                    isSavePressed -> 12.dp
                    isSaveHovered -> 40.dp
                    else -> 32.dp
                },
                animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
                label = "SaveCorner"
            )
            Button(
                onClick = submit,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(saveCorner),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                contentPadding = PaddingValues(vertical = 18.dp),
                interactionSource = saveInteractionSource
            ) {
                Text("Save", fontWeight = FontWeight.Bold)
            }

            val cancelInteractionSource = remember { MutableInteractionSource() }
            val isCancelPressed by cancelInteractionSource.collectIsPressedAsState()
            val isCancelHovered by cancelInteractionSource.collectIsHoveredAsState()
            val cancelCorner by animateDpAsState(
                targetValue = when {
                    isCancelPressed -> 12.dp
                    isCancelHovered -> 40.dp
                    else -> 32.dp
                },
                animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
                label = "CancelCorner"
            )
            TextButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(cancelCorner),
                contentPadding = PaddingValues(vertical = 14.dp),
                interactionSource = cancelInteractionSource
            ) {
                Text("Cancel", fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun money(value: Double): String {
    val magnitude = String.format("%.2f", abs(value))
    return if (value <= -0.005) "−$$magnitude" else "$$magnitude"
}

private fun signed(magnitude: Double, positive: Boolean): String =
    (if (positive) "+" else "−") + money(abs(magnitude))

@Composable
private fun SummaryColumn(label: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = valueColor,
            maxLines = 1
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TypeToggle(selected: TransactionType, onSelect: (TransactionType) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        TypeToggleOption(
            label = "Outflow",
            isSelected = selected == TransactionType.OUTFLOW,
            onClick = { onSelect(TransactionType.OUTFLOW) },
            modifier = Modifier.weight(1f)
        )
        TypeToggleOption(
            label = "Inflow",
            isSelected = selected == TransactionType.INFLOW,
            onClick = { onSelect(TransactionType.INFLOW) },
            modifier = Modifier.weight(1f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TypeToggleOption(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()

    val corner by animateDpAsState(
        targetValue = when {
            isPressed -> 10.dp
            isHovered -> 28.dp
            else -> 26.dp
        },
        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
        label = "ToggleCorner"
    )
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
        label = "ToggleContainer"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        },
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
        label = "ToggleContent"
    )

    Surface(
        onClick = onClick,
        modifier = modifier.semantics { selected = isSelected },
        shape = RoundedCornerShape(corner),
        color = containerColor,
        contentColor = contentColor,
        interactionSource = interactionSource
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )
    }
}

@Composable
fun Modifier.debouncedImePadding(): Modifier {
    val density = LocalDensity.current
    val imeBottom = WindowInsets.ime.getBottom(density)
    var debouncedBottom by remember { mutableIntStateOf(imeBottom) }

    LaunchedEffect(imeBottom) {
        if (imeBottom >= debouncedBottom) {
            debouncedBottom = imeBottom
        } else {
            delay(150)
            debouncedBottom = imeBottom
        }
    }

    val animatedPadding by animateDpAsState(
        targetValue = with(density) { debouncedBottom.toDp() },
        animationSpec = tween(durationMillis = 200, easing = LinearOutSlowInEasing),
        label = "debouncedImePadding"
    )

    return this.padding(bottom = animatedPadding)
}
