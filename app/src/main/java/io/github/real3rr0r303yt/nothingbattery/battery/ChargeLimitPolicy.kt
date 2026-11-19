// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 Real3rr0r303YT — NothingBattery. Made by Real3rr0r303YT.

package io.github.real3rr0r303yt.nothingbattery.battery

/**
 * Decides when the charge-limit alert fires. Alerts once per charging session and re-arms
 * after unplugging or after the level falls [REARM_HYSTERESIS] points below the limit.
 */
object ChargeLimitPolicy {

    val SUPPORTED_LIMITS = listOf(80, 85)
    const val DEFAULT_LIMIT = 80
    const val REARM_HYSTERESIS = 3
    const val APPROACH_WINDOW = 5

    data class Decision(val notify: Boolean, val armed: Boolean)

    fun evaluate(levelPercent: Int, plugged: Boolean, limit: Int, armed: Boolean): Decision {
        if (!plugged || levelPercent in 0 until limit - REARM_HYSTERESIS) {
            return Decision(notify = false, armed = true)
        }
        if (armed && levelPercent >= limit) {
            return Decision(notify = true, armed = false)
        }
        return Decision(notify = false, armed = armed)
    }

    fun indicator(levelPercent: Int, plugged: Boolean, limit: Int): LimitIndicator = when {
        !plugged || levelPercent < 0 -> LimitIndicator.IDLE
        levelPercent >= limit -> LimitIndicator.REACHED
        levelPercent >= limit - APPROACH_WINDOW -> LimitIndicator.APPROACHING
        else -> LimitIndicator.IDLE
    }
}

enum class LimitIndicator { IDLE, APPROACHING, REACHED }
