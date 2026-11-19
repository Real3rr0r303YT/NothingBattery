// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 Real3rr0r303YT — NothingBattery. Made by Real3rr0r303YT.

package io.github.real3rr0r303yt.nothingbattery.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.real3rr0r303yt.nothingbattery.ui.theme.NothingColors
import io.github.real3rr0r303yt.nothingbattery.ui.theme.NothingType

val PanelShape = RoundedCornerShape(24.dp)

/** Flat, outlined Nothing-style surface. */
@Composable
fun Panel(
    modifier: Modifier = Modifier,
    borderColor: Color = NothingColors.Outline,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier
            .background(NothingColors.Surface, PanelShape)
            .border(1.dp, borderColor, PanelShape)
            .padding(18.dp),
        content = content,
    )
}

@Composable
fun Label(text: String, modifier: Modifier = Modifier, color: Color = NothingColors.Muted) {
    Text(text.uppercase(), modifier, color = color, style = NothingType.Label)
}

@Composable
fun StatTile(
    label: String,
    value: String,
    unit: String?,
    modifier: Modifier = Modifier,
    alert: Boolean = false,
) {
    Panel(modifier, borderColor = if (alert) NothingColors.Red else NothingColors.Outline) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (alert) {
                PulseDot(size = 6.dp)
                Spacer(Modifier.size(8.dp))
            }
            Label(label)
        }
        Spacer(Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, color = if (alert) NothingColors.Red else NothingColors.White, style = NothingType.Value)
            if (unit != null) {
                Spacer(Modifier.size(6.dp))
                Text(unit, Modifier.padding(bottom = 4.dp), color = NothingColors.Muted, style = NothingType.Unit)
            }
        }
    }
}

/** Blinking red recording-style dot. */
@Composable
fun PulseDot(size: Dp = 8.dp, color: Color = NothingColors.Red) {
    val a by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 1f,
        targetValue = 0.15f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "pulseAlpha",
    )
    Box(Modifier.size(size).alpha(a).background(color, CircleShape))
}

/** Pill segmented control: selected = solid white, others = outlined. */
@Composable
fun <T> Segmented(
    options: List<T>,
    selected: T,
    label: (T) -> String,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { option ->
            val isSelected = option == selected
            Box(
                Modifier
                    .background(if (isSelected) NothingColors.White else Color.Transparent, CircleShape)
                    .border(1.dp, if (isSelected) NothingColors.White else NothingColors.Outline, CircleShape)
                    .clickable(role = Role.RadioButton) { onSelect(option) }
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    label(option),
                    color = if (isSelected) NothingColors.Black else NothingColors.White,
                    style = NothingType.Unit,
                )
            }
        }
    }
}

/** Small outlined pill used for unobtrusive outbound links (support, source code). */
@Composable
fun LinkChip(text: String, onClick: () -> Unit) {
    Box(
        Modifier
            .border(1.dp, NothingColors.Outline, CircleShape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text.uppercase(), color = NothingColors.White, style = NothingType.Label)
    }
}

@Composable
fun NothingSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = NothingColors.White,
            checkedTrackColor = NothingColors.Red,
            checkedBorderColor = NothingColors.Red,
            uncheckedThumbColor = NothingColors.Muted,
            uncheckedTrackColor = NothingColors.Black,
            uncheckedBorderColor = NothingColors.Outline,
        ),
    )
}
