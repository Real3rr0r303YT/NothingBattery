// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 Real3rr0r303YT — NothingBattery. Made by Real3rr0r303YT.

package io.github.real3rr0r303yt.nothingbattery.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.real3rr0r303yt.nothingbattery.battery.BatteryMonitor
import io.github.real3rr0r303yt.nothingbattery.battery.BatteryState
import io.github.real3rr0r303yt.nothingbattery.service.ChargeGuardService
import io.github.real3rr0r303yt.nothingbattery.settings.Settings
import io.github.real3rr0r303yt.nothingbattery.settings.SettingsStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(app: Application) : AndroidViewModel(app) {

    private val store = SettingsStore.get(app)

    val battery: StateFlow<BatteryState?> = BatteryMonitor.states(app)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BatteryMonitor.snapshot(app))

    /** Rolling window of current samples (mA) for the dot graph. */
    val currentHistory: StateFlow<List<Int>> = battery
        .filterNotNull()
        .mapNotNull { it.currentMa }
        .runningFold(emptyList<Int>()) { acc, v -> (acc + v).takeLast(HISTORY_SIZE) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val settings: StateFlow<Settings> = store.state

    fun setChargeLimit(limit: Int) = store.setChargeLimit(limit)

    fun setAlertsEnabled(enabled: Boolean) {
        store.setAlertsEnabled(enabled)
        val context = getApplication<Application>()
        if (enabled) ChargeGuardService.start(context) else ChargeGuardService.stop(context)
    }

    companion object {
        const val HISTORY_SIZE = 60
    }
}
