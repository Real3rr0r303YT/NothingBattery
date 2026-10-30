package io.github.real3rr0r303yt.nothingbattery

import android.app.Application
import io.github.real3rr0r303yt.nothingbattery.service.Notifications

class NothingBatteryApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Notifications.createChannels(this)
    }
}
