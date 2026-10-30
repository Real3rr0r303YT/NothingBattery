// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 Real3rr0r303YT — NothingBattery. Made by Real3rr0r303YT.

package io.github.real3rr0r303yt.nothingbattery

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Outbound links. They are handed to the user's browser via ACTION_VIEW — the app itself
 * still has no INTERNET permission and never makes a network request.
 */
object Links {
    const val BUY_ME_A_COFFEE = "https://buymeacoffee.com/3rr0r303YT"
    const val GPL_3_0 = "https://www.gnu.org/licenses/gpl-3.0.html"
    const val SOURCE_CODE = "https://github.com/Real3rr0r303YT/NothingBattery"

    fun open(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            // No browser installed — nothing sensible to do offline.
        }
    }
}
