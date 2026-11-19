// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 Real3rr0r303YT — NothingBattery. Made by Real3rr0r303YT.

package io.github.real3rr0r303yt.nothingbattery.settings

import android.content.Context
import androidx.core.content.edit
import io.github.real3rr0r303yt.nothingbattery.battery.ChargeLimitPolicy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class Settings(
    val chargeLimit: Int = ChargeLimitPolicy.DEFAULT_LIMIT,
    val alertsEnabled: Boolean = false,
)

/** Tiny SharedPreferences-backed store; local only and excluded from backups. */
class SettingsStore private constructor(context: Context) {

    private val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    private val _state = MutableStateFlow(
        Settings(
            chargeLimit = prefs.getInt(KEY_LIMIT, ChargeLimitPolicy.DEFAULT_LIMIT)
                .takeIf { it in ChargeLimitPolicy.SUPPORTED_LIMITS } ?: ChargeLimitPolicy.DEFAULT_LIMIT,
            alertsEnabled = prefs.getBoolean(KEY_ALERTS, false),
        ),
    )
    val state: StateFlow<Settings> = _state.asStateFlow()

    fun setChargeLimit(limit: Int) {
        require(limit in ChargeLimitPolicy.SUPPORTED_LIMITS) { "Unsupported limit $limit" }
        prefs.edit { putInt(KEY_LIMIT, limit) }
        _state.update { it.copy(chargeLimit = limit) }
    }

    fun setAlertsEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_ALERTS, enabled) }
        _state.update { it.copy(alertsEnabled = enabled) }
    }

    companion object {
        private const val KEY_LIMIT = "charge_limit"
        private const val KEY_ALERTS = "alerts_enabled"

        @Volatile private var instance: SettingsStore? = null

        fun get(context: Context): SettingsStore =
            instance ?: synchronized(this) {
                instance ?: SettingsStore(context.applicationContext).also { instance = it }
            }
    }
}
