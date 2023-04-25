package eu.petrfaruzel.collapsibletopbar

import androidx.compose.foundation.layout.BoxWithConstraintsScope

class CollapsibleTopBarScope(
    val fraction: Float,
    scope: BoxWithConstraintsScope
) : BoxWithConstraintsScope by scope