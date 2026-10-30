// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 Real3rr0r303YT — NothingBattery. Made by Real3rr0r303YT.

package io.github.real3rr0r303yt.nothingbattery.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.real3rr0r303yt.nothingbattery.Links
import io.github.real3rr0r303yt.nothingbattery.R
import io.github.real3rr0r303yt.nothingbattery.ui.theme.NothingColors
import io.github.real3rr0r303yt.nothingbattery.ui.theme.NothingType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Full-screen viewer for the bundled GPL-3.0 text (assets/LICENSE) — works fully offline. */
@Composable
fun LicenseDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val text by produceState<String?>(initialValue = null) {
        value = withContext(Dispatchers.IO) {
            context.assets.open("LICENSE").bufferedReader().use { it.readText() }
        }
    }
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(
            Modifier
                .fillMaxSize()
                .background(NothingColors.Black)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(20.dp),
        ) {
            Label(stringResource(R.string.license_title), color = NothingColors.Red)
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.license_summary), color = NothingColors.White, style = NothingType.Body)
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LinkChip(stringResource(R.string.license_close), onDismiss)
                LinkChip(stringResource(R.string.license_online)) { Links.open(context, Links.GPL_3_0) }
            }
            Spacer(Modifier.height(16.dp))
            Panel(Modifier.fillMaxWidth().weight(1f)) {
                Text(
                    text ?: "…",
                    Modifier.verticalScroll(rememberScrollState()),
                    color = NothingColors.Muted,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                )
            }
        }
    }
}
