package io.github.real3rr0r303yt.nothingbattery.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.github.real3rr0r303yt.nothingbattery.settings.SettingsStore

/** Restores the charge guard after a reboot or app update if the user had it enabled. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action != Intent.ACTION_BOOT_COMPLETED && action != Intent.ACTION_MY_PACKAGE_REPLACED) return
        if (SettingsStore.get(context).state.value.alertsEnabled) {
            ChargeGuardService.start(context)
        }
    }
}
