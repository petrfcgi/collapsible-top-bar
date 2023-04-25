package eu.petrfaruzel.collapsibletopbar

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.dp


internal object CollapsibleTopAppBarDefaults {
    // Replicating the value in androidx.compose.material.AppBar.AppBarHeight which is private
    val DEFAULT_MIN_HEIGHT = 64.dp
    val DEFAULT_MAX_HEIGHT = 180.dp

    /**
     * Log intensity of title width scaling
     * Function looks like this [__/]
     * The greater value, the steeper the curve and greater the delay
     * (Range of 1-n please)
     */
    const val TITLE_WIDTH_DAMPENING = 5f
    const val LABEL_ALPHA_TRANSITION_DELAY = 0.35f // Delay for displaying label
    const val TITLE_WIDTH_SCALE_DELAY = 0.0f // Delay before title width starts scaling horizontally

    val TITLE_EXPANDED_VERTICAL_PADDING = 8.dp
}

internal val LocalScrollOffset = compositionLocalOf<State<Int>> {
    mutableStateOf(Int.MAX_VALUE)
}
internal val LocalInsets = compositionLocalOf {
    PaddingValues(0.dp)
}
