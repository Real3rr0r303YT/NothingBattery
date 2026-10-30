package io.github.real3rr0r303yt.nothingbattery.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import io.github.real3rr0r303yt.nothingbattery.ui.theme.NothingColors
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Circular dot gauge. Lit dots represent the charge level, the dot at [limitPercent] is a
 * red marker, and a white "comet" sweeps over the lit dots while [charging].
 */
@Composable
fun DotRing(
    levelPercent: Int,
    limitPercent: Int,
    charging: Boolean,
    modifier: Modifier = Modifier,
    dots: Int = 60,
    critical: Boolean = false,
) {
    val sweep by rememberInfiniteTransition(label = "sweep").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2_400, easing = LinearEasing), RepeatMode.Restart),
        label = "sweepProgress",
    )
    Canvas(modifier) {
        val radius = min(size.width, size.height) / 2f
        val dotRadius = radius * 0.035f
        val ringRadius = radius - dotRadius * 1.5f
        val lit = (levelPercent.coerceIn(0, 100) / 100f * dots).roundToInt()
        val limitDot = (limitPercent / 100f * dots).roundToInt().coerceIn(0, dots - 1)
        val head = (sweep * lit).toInt()
        val litColor = if (critical) NothingColors.Red else NothingColors.White

        for (i in 0 until dots) {
            // Start at 12 o'clock, run clockwise.
            val angle = -PI / 2 + 2 * PI * i / dots
            val center = Offset(
                x = size.width / 2f + ringRadius * cos(angle).toFloat(),
                y = size.height / 2f + ringRadius * sin(angle).toFloat(),
            )
            val color: Color = when {
                i == limitDot -> NothingColors.Red
                i < lit && charging && i in (head - 3)..head -> litColor
                i < lit && charging -> litColor.copy(alpha = 0.55f)
                i < lit -> litColor
                else -> NothingColors.DotOff
            }
            val r = if (i == limitDot) dotRadius * 1.25f else dotRadius
            drawCircle(color, r, center)
        }
    }
}
