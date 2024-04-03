package com.mgws.adownkyi.ui.compose

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
abstract class SwipeMenuBoxScope {
    protected abstract val width: Dp
    protected abstract val height: Dp

    protected abstract val block: (DraggableAnchors<Int>) -> Unit

    @Composable
    fun SwipeMenu(content: @Composable () -> Unit) {

        Layout(modifier = Modifier
            .offset(x = width)
            .height(height)
            .wrapContentWidth(), content = {
            content()
        }) { measurables, constraints ->
            val placeables = measurables.map { measurable ->
                measurable.measure(constraints)
            }
            val widths = placeables.sumOf { it.width }

            block(DraggableAnchors {
                0 at 0f
                1 at -widths.toFloat()
            })
            layout(constraints.maxWidth, constraints.maxHeight) {
                var x = 0
                placeables.forEach { placeable ->
                    placeable.placeRelative(x = x, y = 0)
                    x += placeable.width
                }
            }
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwipeMenuBox(
    modifier: Modifier = Modifier,
    disable: Boolean,
    content: @Composable SwipeMenuBoxScope.() -> Unit,
) {

    val density = LocalDensity.current
    val positionalThreshold = { distance: Float -> distance }
    val velocityThreshold = { with(density) { -10.dp.toPx() } }
    val animationSpec = tween<Float>()

    val state = rememberSaveable(
        saver = AnchoredDraggableState.Saver(animationSpec, positionalThreshold, velocityThreshold)
    ) {
        AnchoredDraggableState(
            0,
            anchors = DraggableAnchors {
                0 at 0f
            },
            positionalThreshold = positionalThreshold,
            velocityThreshold = velocityThreshold,
            animationSpec = animationSpec,
        )
    }

    BoxWithConstraints(
        modifier = if (disable) Modifier
        // 滑动
        else modifier
            .anchoredDraggable(state = state, orientation = Orientation.Horizontal)
            .offset {
                IntOffset(
                    x = state
                        .requireOffset()
                        .roundToInt(),
                    y = 0
                )
            },
    ) {

        val scope = object : SwipeMenuBoxScope() {
            override val width: Dp = maxWidth
            override val height: Dp = maxHeight
            override val block: (DraggableAnchors<Int>) -> Unit =
                { state.updateAnchors(it) }
        }
        scope.content()
    }
}