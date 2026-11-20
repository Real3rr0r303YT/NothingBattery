// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 Real3rr0r303YT — NothingBattery. Made by 3rr0r303YT on TikTok.

package io.github.real3rr0r303yt.nothingbattery.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.widget.RemoteViews
import io.github.real3rr0r303yt.nothingbattery.MainActivity
import io.github.real3rr0r303yt.nothingbattery.R
import io.github.real3rr0r303yt.nothingbattery.battery.BatteryMonitor
import io.github.real3rr0r303yt.nothingbattery.battery.ChargeStatus
import io.github.real3rr0r303yt.nothingbattery.settings.SettingsStore

/**
 * Battery level as a 40-dot ring with the percentage in dot-matrix digits, plus status and
 * charge limit. Refreshed by the charge-limit monitor when it runs, when the app closes and
 * every ~15 min by a *non-wakeup* alarm (nothing runs while the phone sleeps).
 */
class BatteryWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        render(context, manager, ids)
        schedule(context)
    }

    override fun onEnabled(context: Context) = schedule(context)

    override fun onDisabled(context: Context) {
        context.getSystemService(AlarmManager::class.java)?.cancel(refreshIntent(context))
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            ACTION_REFRESH, Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_LOCALE_CHANGED,
            -> updateAll(context)
        }
    }

    companion object {
        private const val ACTION_REFRESH = "io.github.real3rr0r303yt.nothingbattery.widget.REFRESH"
        private const val LOW = 20

        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, BatteryWidget::class.java))
            if (ids.isNotEmpty()) render(context, manager, ids)
        }

        private fun render(context: Context, manager: AppWidgetManager, ids: IntArray) {
            val state = BatteryMonitor.snapshot(context) ?: return
            val density = context.resources.displayMetrics.density
            val level = state.levelPercent.coerceIn(0, 100)
            val charging = state.status == ChargeStatus.CHARGING
            val ringColor = when {
                charging -> DotRender.WHITE
                level <= LOW -> DotRender.RED
                else -> DotRender.WHITE
            }
            val views = RemoteViews(context.packageName, R.layout.widget_battery)
            views.setImageViewBitmap(
                R.id.widget_ring,
                DotRender.ring((120 * density).toInt(), level / 100f, ringColor, label = "$level%"),
            )
            views.setContentDescription(R.id.widget_ring, "$level %")

            val locale = context.resources.configuration.locales[0]
            val limit = SettingsStore.get(context).state.value
            val status = buildString {
                append(context.getString(state.status.label).uppercase(locale))
                if (limit.alertsEnabled) append(" • ").append(limit.chargeLimit).append('%')
            }
            views.setImageViewBitmap(
                R.id.widget_status,
                DotRender.text(status, 3f * density) { _, c ->
                    if (charging && c != '•') DotRender.RED else DotRender.GREY
                },
            )
            views.setContentDescription(R.id.widget_status, status)

            val open = PendingIntent.getActivity(
                context, 0,
                Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
            views.setOnClickPendingIntent(R.id.widget_root, open)
            manager.updateAppWidget(ids, views)
        }

        private fun refreshIntent(context: Context): PendingIntent = PendingIntent.getBroadcast(
            context, 0,
            Intent(context, BatteryWidget::class.java).setAction(ACTION_REFRESH),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

        private fun schedule(context: Context) {
            // ELAPSED_REALTIME (not _WAKEUP), inexact: batched with other alarms, never wakes the phone.
            context.getSystemService(AlarmManager::class.java)?.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                refreshIntent(context),
            )
        }
    }
}
