// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 Real3rr0r303YT — NothingBattery. Made by Real3rr0r303YT.

package io.github.real3rr0r303yt.nothingbattery.battery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicReference

/**
 * Live battery data from the native [BatteryManager].
 *
 * ACTION_BATTERY_CHANGED only fires when level/status/temperature change, but current
 * flow changes constantly, so the last sticky intent is re-sampled every [pollMs].
 */
object BatteryMonitor {

    fun states(context: Context, pollMs: Long = 1_000L): Flow<BatteryState> = callbackFlow {
        val app = context.applicationContext
        val manager = app.getSystemService(BatteryManager::class.java)
        val lastIntent = AtomicReference<Intent?>(null)

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context, intent: Intent) {
                lastIntent.set(intent)
                trySend(read(intent, manager))
            }
        }
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val sticky = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            app.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            app.registerReceiver(receiver, filter)
        }
        sticky?.let {
            lastIntent.set(it)
            trySend(read(it, manager))
        }

        val poller = launch {
            while (true) {
                delay(pollMs)
                lastIntent.get()?.let { trySend(read(it, manager)) }
            }
        }

        awaitClose {
            poller.cancel()
            app.unregisterReceiver(receiver)
        }
    }.conflate()

    /** One-shot read of the current sticky battery intent, or null if unavailable. */
    fun snapshot(context: Context): BatteryState? {
        val app = context.applicationContext
        val intent = app.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED)) ?: return null
        return read(intent, app.getSystemService(BatteryManager::class.java))
    }

    fun read(intent: Intent, manager: BatteryManager?): BatteryState {
        val cycles = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            intent.getIntExtra(BatteryManager.EXTRA_CYCLE_COUNT, -1).takeIf { it >= 0 }
        } else {
            null
        }
        return BatteryState(
            levelPercent = BatteryMath.levelPercent(
                intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1),
                intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1),
            ),
            status = BatteryMath.status(intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)),
            plug = BatteryMath.plug(intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)),
            health = BatteryMath.health(intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)),
            temperatureC = BatteryMath.temperatureC(intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)),
            voltageMv = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0),
            currentMa = manager?.let {
                BatteryMath.currentMa(it.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW))
            },
            chargeCounterMah = manager?.let {
                BatteryMath.chargeCounterMah(it.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER))
            },
            cycleCount = cycles,
            technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)?.takeIf { it.isNotBlank() },
            timestampMs = System.currentTimeMillis(),
        )
    }
}
