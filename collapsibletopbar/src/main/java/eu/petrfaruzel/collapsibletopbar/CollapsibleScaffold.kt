@file:OptIn(ExperimentalMaterial3Api::class)

package eu.petrfaruzel.collapsibletopbar

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import eu.petrfaruzel.collapsibletopbar.CollapsibleTopAppBarDefaults.DEFAULT_MAX_HEIGHT
import kotlin.math.absoluteValue

@Composable
fun CollapsibleScaffold(
    state: ScrollState,
    modifier: Modifier = Modifier,
    maxTopBarHeight: Dp? = null,
    topBar: @Composable (maxHeight: MutableState<Dp>) -> Unit = {},
    content: @Composable (insets: PaddingValues) -> Unit
) {
    CollapsibleScaffoldInternal(
        offsetState = rememberOffsetScrollState(state),
        modifier = modifier,
        maxTopBarHeight = maxTopBarHeight,
        topBar = topBar,
        content = content
    )
}

@Composable
fun CollapsibleScaffold(
    state: LazyListState,
    modifier: Modifier = Modifier,
    maxTopBarHeight: Dp? = null,
    topBar: @Composable (maxHeight: MutableState<Dp>) -> Unit = {},
    content: @Composable (insets: PaddingValues) -> Unit
) {
    CollapsibleScaffoldInternal(
        offsetState = rememberOffsetScrollState(state),
        modifier = modifier,
        maxTopBarHeight = maxTopBarHeight,
        topBar = topBar,
        content = content
    )
}

@Composable
private fun CollapsibleScaffoldInternal(
    offsetState: State<Int>,
    modifier: Modifier = Modifier,
    maxTopBarHeight: Dp?,
    topBar: @Composable (maxTopBarHeight: MutableState<Dp>) -> Unit = {},
    content: @Composable (insets: PaddingValues) -> Unit
) {

    val maxHeight = remember { mutableStateOf(maxTopBarHeight ?: DEFAULT_MAX_HEIGHT) }

    Scaffold(modifier = modifier) { insets ->
        Box {
            content(
                PaddingValues(
                    top = maxHeight.value + 8.dp,
                    bottom = 16.dp
                )
            )
            CompositionLocalProvider(
                LocalScrollOffset provides offsetState,
                LocalInsets provides insets
            ) {
                topBar(maxHeight)
            }
        }
    }
}

@Composable
private fun rememberOffsetScrollState(state: LazyListState): MutableState<Int> {
    val offsetState = rememberSaveable { mutableStateOf(0) }
    LaunchedEffect(key1 = state.layoutInfo.visibleItemsInfo) {
        val fistItem = state.layoutInfo.visibleItemsInfo.firstOrNull { it.index == 0 }
        val offset = fistItem?.offset?.absoluteValue ?: Int.MAX_VALUE
        offsetState.value = offset
    }
    return offsetState
}

@Composable
private fun rememberOffsetScrollState(state: ScrollState): MutableState<Int> {
    val offsetState = rememberSaveable { mutableStateOf(0) }
    LaunchedEffect(key1 = state.value) {
        offsetState.value = state.value
    }
    return offsetState
}
