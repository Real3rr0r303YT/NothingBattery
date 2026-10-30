package io.github.real3rr0r303yt.nothingbattery.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.content.ContextCompat
import io.github.real3rr0r303yt.nothingbattery.battery.BatteryMonitor
import io.github.real3rr0r303yt.nothingbattery.battery.ChargeStatus
import io.github.real3rr0r303yt.nothingbattery.battery.ChargeLimitPolicy
import io.github.real3rr0r303yt.nothingbattery.settings.SettingsStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * Lightweight foreground service that watches the battery while "Notify me" is enabled
 * and raises a single alert per charging session once the charge limit is reached.
 */
class ChargeGuardService : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var armed = true

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        val settings = SettingsStore.get(this)
        goForeground(settings.state.value.chargeLimit)

        scope.launch {
            var lastKey: MonitorKey? = null
            combine(BatteryMonitor.states(this@ChargeGuardService, POLL_MS), settings.state) { battery, s ->
                battery to s
            }.collect { (battery, s) ->
                if (!s.alertsEnabled) {
                    stopSelf()
                    return@collect
                }
                val decision = ChargeLimitPolicy.evaluate(battery.levelPercent, battery.isPlugged, s.chargeLimit, armed)
                if (decision.notify) {
                    Notifications.postLimitReached(this@ChargeGuardService, s.chargeLimit, battery.levelPercent)
                }
                if (!battery.isPlugged) Notifications.cancelAlert(this@ChargeGuardService)
                armed = decision.armed

                // Refresh the silent ongoing notification only when something visible changes.
                val key = MonitorKey(s.chargeLimit, battery.levelPercent, battery.status, battery.currentMa?.div(50))
                if (key != lastKey) {
                    lastKey = key
                    Notifications.updateMonitor(this@ChargeGuardService, s.chargeLimit, battery)
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!SettingsStore.get(this).state.value.alertsEnabled) {
            stopSelf()
            return START_NOT_STICKY
        }
        return START_STICKY
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private fun goForeground(limit: Int) {
        val notification = Notifications.monitor(this, limit, BatteryMonitor.snapshot(this))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(Notifications.ID_MONITOR, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(Notifications.ID_MONITOR, notification)
        }
    }

    private data class MonitorKey(val limit: Int, val level: Int, val status: ChargeStatus, val currentBucket: Int?)

    companion object {
        private const val POLL_MS = 5_000L

        fun start(context: Context) {
            ContextCompat.startForegroundService(context, Intent(context, ChargeGuardService::class.java))
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, ChargeGuardService::class.java))
        }
    }
}
