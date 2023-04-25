package eu.petrfaruzel.collapsibletopbar

import androidx.annotation.FloatRange
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import eu.petrfaruzel.collapsibletopbar.CollapsibleTopAppBarDefaults.DEFAULT_MIN_HEIGHT
import eu.petrfaruzel.collapsibletopbar.CollapsibleTopAppBarDefaults.LABEL_ALPHA_TRANSITION_DELAY
import eu.petrfaruzel.collapsibletopbar.CollapsibleTopAppBarDefaults.TITLE_EXPANDED_VERTICAL_PADDING
import eu.petrfaruzel.collapsibletopbar.CollapsibleTopAppBarDefaults.TITLE_WIDTH_DAMPENING
import eu.petrfaruzel.collapsibletopbar.CollapsibleTopAppBarDefaults.TITLE_WIDTH_SCALE_DELAY
import java.lang.Float.max
import kotlin.math.min
import kotlin.math.pow

/**
 * Simplest form of TopBar, mostly predefined
 */
@Composable
fun CollapsibleTopAppBar(
    modifier: Modifier = Modifier,
    maxHeight: MutableState<Dp>,
    title: String? = null,
    label: String? = null,
    onBack: (() -> Unit) = { },
    actions: (@Composable RowScope.() -> Unit)? = null
) {
    CollapsibleTopAppBar(
        modifier = modifier,
        maxHeight = maxHeight,
        actions = actions,
        navigationIcon = { DefaultTopAppBarBackIcon(onBack) },
        labelContent = {
            DefaultTopAppBarLabel(
                text = label,
                modifier = modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 8.dp),
            )
        }
    ) {
        DefaultCollapsibleTopAppBarTitle(
            text = title,
            fraction = fraction,
            maxHeight = maxHeight,
            modifier = modifier
                .align(Alignment.CenterStart)
                .padding(
                    start = 8.dp + 8.dp * fraction,
                    end = 16.dp * fraction
                )
                .background(Color.White),
        )
    }
}

/**
 * TopBar with custom components and predefined back arrow
 */
@Composable
fun CollapsibleTopAppBar(
    modifier: Modifier = Modifier,
    maxHeight: MutableState<Dp>,
    onBack: (() -> Unit) = { },
    actions: (@Composable RowScope.() -> Unit)? = null,
    labelContent: @Composable CollapsibleTopBarScope.() -> Unit = {},
    content: (@Composable CollapsibleTopBarScope.() -> Unit) = { }
) {
    CollapsibleTopAppBar(
        modifier = modifier,
        maxHeight = maxHeight,
        labelContent = labelContent,
        actions = actions,
        content = content,
        navigationIcon = { DefaultTopAppBarBackIcon(onBack) },
    )
}

/**
 *  Fully customisable TopBar
 */
@Composable
fun CollapsibleTopAppBar(
    modifier: Modifier = Modifier,
    maxHeight: MutableState<Dp>,
    actions: (@Composable RowScope.() -> Unit)? = null,
    navigationIcon: (@Composable () -> Unit)? = null,
    labelContent: @Composable CollapsibleTopBarScope.() -> Unit = {},
    content: (@Composable CollapsibleTopBarScope.() -> Unit) = { }
) {
    CollapsibleTopAppBarInternal(
        maxHeight = remember { maxHeight },
        modifier = modifier,
        scrollOffset = LocalScrollOffset.current.value,
        insets = LocalInsets.current,
        navigationIcon = navigationIcon,
        actions = actions,
        labelContent = labelContent,
        titleContent = content
    )
}

@Composable
private fun CollapsibleTopAppBarInternal(
    scrollOffset: Int,
    insets: PaddingValues,
    modifier: Modifier = Modifier,
    maxHeight: MutableState<Dp>,
    minHeight: Dp = DEFAULT_MIN_HEIGHT,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    labelContent: @Composable CollapsibleTopBarScope.() -> Unit,
    titleContent: @Composable CollapsibleTopBarScope.() -> Unit
) {
    val density = LocalDensity.current
    val navIconSize = remember { mutableStateOf(IntSize.Zero) }
    val navTitleSize = remember { mutableStateOf(IntSize.Zero) }
    val isInitialized = remember { mutableStateOf(false) }

    val navIconWidth = with(density) { navIconSize.value.width.toDp() }
    val navTitleWidth = with(density) { navTitleSize.value.width.toDp() }

    // Height of title body when fully expanded
    val bodyHeight = maxHeight.value - minHeight

    // Fancy math to calculate fraction (0-1)
    val maxOffset = with(density) {
        bodyHeight.roundToPx() - insets.calculateTopPadding().roundToPx()
    }

    val offset = min(scrollOffset, maxOffset)
    val fraction = 1f - kotlin.math.max(0f, offset.toFloat()) / maxOffset
    // Could be just fraction, but we might want to add delay later
    val widthFraction = getFractionWithDelay(
        fraction,
        TITLE_WIDTH_SCALE_DELAY
    )

    BoxWithConstraints(
        modifier = modifier
            .background(Color.White)
            .height(minHeight + bodyHeight * fraction)
    ) {
        val maxWidth = maxWidth

        // Could have used lerp() [linear interpolation], but it seems way slower than direct calculation
        val titleWidth = navTitleWidth +
                (maxWidth * widthFraction.pow(widthFraction + TITLE_WIDTH_DAMPENING)) -
                (navTitleWidth * widthFraction.pow(widthFraction + TITLE_WIDTH_DAMPENING))

        Divider(modifier = Modifier.align(Alignment.BottomCenter))

        // Box has to be above because of background color overlap with navigation icons
        BoxWithConstraints(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .heightIn(min = minHeight)
                .offset(
                    x = navIconWidth - (navIconWidth * fraction),
                    y = ((minHeight - TITLE_EXPANDED_VERTICAL_PADDING / 2) * fraction)
                )
                .width(titleWidth)
                .onGloballyPositioned {
                    if (!isInitialized.value) {
                        isInitialized.value = true
                        maxHeight.value =
                            with(density) { it.size.height.toDp() } + minHeight + TITLE_EXPANDED_VERTICAL_PADDING
                    }
                }
        ) {
            val scope = remember(
                fraction,
                this
            ) {
                CollapsibleTopBarScope(
                    fraction = fraction,
                    scope = this
                )
            }
            titleContent(scope)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(minHeight),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxHeight(1f)
                    .onGloballyPositioned {
                        navIconSize.value = it.size
                    }
            ) {
                if (navigationIcon != null) {
                    navigationIcon()
                } else {
                    DefaultTopAppBarBackIcon()
                }
            }

            val fractionAlphaDelayed = getFractionWithDelay(
                fraction,
                LABEL_ALPHA_TRANSITION_DELAY
            )
            val labelAlpha = fractionAlphaDelayed.pow(fractionAlphaDelayed + 1.5f)
            BoxWithConstraints(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .fillMaxHeight(1f)
                    .weight(
                        1f,
                        fill = true
                    )
                    .onGloballyPositioned {
                        navTitleSize.value = it.size
                    }
                    .alpha(labelAlpha)
            ) {
                val scope = remember(
                    fraction,
                    this
                ) {
                    CollapsibleTopBarScope(
                        fraction = fraction,
                        scope = this
                    )
                }
                labelContent(scope)
            }
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxHeight(1f)
                    .widthIn(
                        0.dp,
                        maxWidth - navIconWidth
                    )
                    .padding(start = 8.dp)
            ) {
                if (actions != null) {
                    actions()
                }
            }
        }
    }
}

private fun getFractionWithDelay(
    @FloatRange(from = 0.0, to = 1.0) fraction: Float,
    @FloatRange(from = 0.0, to = 1.0) delay: Float
): Float {
    return max(0f, fraction - delay + (fraction * delay))
}

@Composable
fun DefaultCollapsibleTopAppBarTitle(text: String?, fraction: Float, maxHeight: MutableState<Dp>, modifier: Modifier = Modifier) {
    Text(
        text = text ?: "",
        modifier = modifier.heightIn(max = maxHeight.value - DEFAULT_MIN_HEIGHT + TITLE_EXPANDED_VERTICAL_PADDING), // TODO Replace constant
        fontSize = lerp(
            16.sp,
            24.sp,
            fraction
        ),
        textAlign = TextAlign.Start,
        overflow = TextOverflow.Ellipsis,
        maxLines = if (fraction < 0.4) 1 else if (fraction < 0.65) 2 else Int.MAX_VALUE,
    )
}

@Composable
fun DefaultTopAppBarBackIcon(
    onBack: (() -> Unit) = { },
) {
    IconButton(
        modifier = Modifier.padding(start = 8.dp),
        onClick = {
            onBack()
        }) {
        Icon(
            Icons.Filled.ArrowBack,
            stringResource(id = R.string.cd_go_back)
        )
    }
}

@Composable
fun DefaultTopAppBarLabel(text: String?, modifier: Modifier = Modifier) {
    Text(
        text = text ?: "",
        modifier = modifier,
        textAlign = TextAlign.Center,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1
    )
}
