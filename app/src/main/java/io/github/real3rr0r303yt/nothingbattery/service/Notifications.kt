// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 Real3rr0r303YT — NothingBattery. Made by Real3rr0r303YT.

package io.github.real3rr0r303yt.nothingbattery.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import io.github.real3rr0r303yt.nothingbattery.MainActivity
import io.github.real3rr0r303yt.nothingbattery.R
import io.github.real3rr0r303yt.nothingbattery.battery.BatteryState
import java.util.Locale

object Notifications {

    const val CHANNEL_MONITOR = "monitor"
    const val CHANNEL_ALERT = "limit_alert"
    const val ID_MONITOR = 1
    const val ID_ALERT = 2

    private const val RED = 0xFFD71921.toInt()

    fun createChannels(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannels(
            listOf(
                NotificationChannel(
                    CHANNEL_MONITOR,
                    context.getString(R.string.channel_monitor_name),
                    NotificationManager.IMPORTANCE_MIN,
                ).apply {
                    description = context.getString(R.string.channel_monitor_desc)
                    setShowBadge(false)
                },
                NotificationChannel(
                    CHANNEL_ALERT,
                    context.getString(R.string.channel_alert_name),
                    NotificationManager.IMPORTANCE_HIGH,
                ).apply {
                    description = context.getString(R.string.channel_alert_desc)
                    enableLights(true)
                    lightColor = RED
                },
            ),
        )
    }

    fun canPost(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED

    fun monitor(context: Context, limit: Int, state: BatteryState?) =
        NotificationCompat.Builder(context, CHANNEL_MONITOR)
            .setSmallIcon(R.drawable.ic_stat_battery)
            .setContentTitle(context.getString(R.string.notif_monitor_title, limit))
            .setContentText(state?.let { summary(context, it) })
            .setContentIntent(openApp(context))
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setColor(RED)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()

    @SuppressLint("MissingPermission") // guarded by canPost()
    fun postLimitReached(context: Context, limit: Int, level: Int) {
        if (!canPost(context)) return
        val notification = NotificationCompat.Builder(context, CHANNEL_ALERT)
            .setSmallIcon(R.drawable.ic_stat_battery)
            .setContentTitle(context.getString(R.string.notif_alert_title, level))
            .setContentText(context.getString(R.string.notif_alert_text))
            .setContentIntent(openApp(context))
            .setAutoCancel(true)
            .setColor(RED)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()
        NotificationManagerCompat.from(context).notify(ID_ALERT, notification)
    }

    @SuppressLint("MissingPermission") // guarded by canPost()
    fun updateMonitor(context: Context, limit: Int, state: BatteryState?) {
        if (!canPost(context)) return
        NotificationManagerCompat.from(context).notify(ID_MONITOR, monitor(context, limit, state))
    }

    fun cancelAlert(context: Context) = NotificationManagerCompat.from(context).cancel(ID_ALERT)

    private fun summary(context: Context, s: BatteryState): String = buildString {
        append(s.levelPercent).append(" % · ")
        append(context.getString(s.status.label))
        s.currentMa?.let { append(" · ").append(it).append(" mA") }
        append(" · ").append(String.format(Locale.ROOT, "%.1f °C", s.temperatureC))
    }

    private fun openApp(context: Context): PendingIntent = PendingIntent.getActivity(
        context,
        0,
        Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
    )
}
