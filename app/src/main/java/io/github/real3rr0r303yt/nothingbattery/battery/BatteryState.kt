package io.github.real3rr0r303yt.nothingbattery.battery

import androidx.annotation.StringRes
import io.github.real3rr0r303yt.nothingbattery.R

/** One immutable snapshot of everything the platform tells us about the battery. */
data class BatteryState(
    val levelPercent: Int,
    val status: ChargeStatus,
    val plug: PlugType,
    val health: BatteryHealth,
    val temperatureC: Float,
    val voltageMv: Int,
    /** Instantaneous current in mA. Positive = flowing into the battery. Null if unsupported. */
    val currentMa: Int?,
    /** Remaining charge in mAh. Null if unsupported. */
    val chargeCounterMah: Int?,
    val cycleCount: Int?,
    val technology: String?,
    val timestampMs: Long,
) {
    val isPlugged: Boolean get() = plug != PlugType.NONE

    /** Power in watts derived from voltage × current. */
    val powerW: Float? get() = currentMa?.let { it.toFloat() * voltageMv / 1_000_000f }

    /** Rough full-charge capacity extrapolated from the charge counter. */
    val estimatedCapacityMah: Int? get() = BatteryMath.estimateCapacityMah(chargeCounterMah, levelPercent)
}

enum class ChargeStatus(@StringRes val label: Int) {
    CHARGING(R.string.status_charging),
    DISCHARGING(R.string.status_discharging),
    FULL(R.string.status_full),
    NOT_CHARGING(R.string.status_not_charging),
    UNKNOWN(R.string.status_unknown),
}

enum class PlugType(@StringRes val label: Int) {
    AC(R.string.plug_ac),
    USB(R.string.plug_usb),
    WIRELESS(R.string.plug_wireless),
    DOCK(R.string.plug_dock),
    NONE(R.string.plug_none),
}

enum class BatteryHealth(@StringRes val label: Int, val isProblem: Boolean) {
    GOOD(R.string.health_good, false),
    OVERHEAT(R.string.health_overheat, true),
    DEAD(R.string.health_dead, true),
    OVER_VOLTAGE(R.string.health_over_voltage, true),
    FAILURE(R.string.health_failure, true),
    COLD(R.string.health_cold, true),
    UNKNOWN(R.string.health_unknown, false),
}
