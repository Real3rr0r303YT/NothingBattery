// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 Real3rr0r303YT — NothingBattery. Made by Real3rr0r303YT.

package io.github.real3rr0r303yt.nothingbattery.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import io.github.real3rr0r303yt.nothingbattery.ui.theme.NothingColors
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

/**
 * Dot-column history chart. Each sample is one column; height is |value| relative to the
 * largest sample. The most recent column is highlighted red.
 */
@Composable
fun DotGraph(
    samples: List<Int>,
    modifier: Modifier = Modifier,
    columns: Int = 60,
    rows: Int = 10,
) {
    Canvas(modifier) {
        val pitchX = size.width / columns
        val pitchY = size.height / rows
        val r = min(pitchX, pitchY) * 0.32f
        val peak = max(1, samples.maxOfOrNull { abs(it) } ?: 1)
        val offset = columns - samples.size

        for (c in 0 until columns) {
            val sample = samples.getOrNull(c - offset)
            val height = sample?.let { ceil(abs(it).toFloat() / peak * rows).toInt().coerceIn(1, rows) } ?: 0
            val color = if (sample != null && c == columns - 1) NothingColors.Red else NothingColors.White
            for (row in 0 until rows) {
                val on = rows - row <= height
                drawCircle(
                    color = if (on) color else NothingColors.DotOff,
                    radius = r,
                    center = Offset(c * pitchX + pitchX / 2f, row * pitchY + pitchY / 2f),
                )
            }
        }
    }
}
