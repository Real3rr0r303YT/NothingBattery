package io.github.real3rr0r303yt.nothingbattery.battery

import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Pure conversion helpers for raw [android.os.BatteryManager] values.
 * Kept free of Android types so they can be unit-tested on the JVM.
 */
object BatteryMath {

    /** Platform constants mirrored from BatteryManager so this object stays JVM-only. */
    const val STATUS_CHARGING = 2
    const val STATUS_DISCHARGING = 3
    const val STATUS_NOT_CHARGING = 4
    const val STATUS_FULL = 5

    const val PLUGGED_AC = 1
    const val PLUGGED_USB = 2
    const val PLUGGED_WIRELESS = 4
    const val PLUGGED_DOCK = 8

    const val HEALTH_GOOD = 2
    const val HEALTH_OVERHEAT = 3
    const val HEALTH_DEAD = 4
    const val HEALTH_OVER_VOLTAGE = 5
    const val HEALTH_UNSPECIFIED_FAILURE = 6
    const val HEALTH_COLD = 7

    /** Values at or above this magnitude are assumed to be µA rather than mA. */
    private const val MICRO_AMP_THRESHOLD = 10_000

    fun status(raw: Int): ChargeStatus = when (raw) {
        STATUS_CHARGING -> ChargeStatus.CHARGING
        STATUS_DISCHARGING -> ChargeStatus.DISCHARGING
        STATUS_NOT_CHARGING -> ChargeStatus.NOT_CHARGING
        STATUS_FULL -> ChargeStatus.FULL
        else -> ChargeStatus.UNKNOWN
    }

    fun plug(raw: Int): PlugType = when {
        raw and PLUGGED_AC != 0 -> PlugType.AC
        raw and PLUGGED_USB != 0 -> PlugType.USB
        raw and PLUGGED_WIRELESS != 0 -> PlugType.WIRELESS
        raw and PLUGGED_DOCK != 0 -> PlugType.DOCK
        else -> PlugType.NONE
    }

    fun health(raw: Int): BatteryHealth = when (raw) {
        HEALTH_GOOD -> BatteryHealth.GOOD
        HEALTH_OVERHEAT -> BatteryHealth.OVERHEAT
        HEALTH_DEAD -> BatteryHealth.DEAD
        HEALTH_OVER_VOLTAGE -> BatteryHealth.OVER_VOLTAGE
        HEALTH_UNSPECIFIED_FAILURE -> BatteryHealth.FAILURE
        HEALTH_COLD -> BatteryHealth.COLD
        else -> BatteryHealth.UNKNOWN
    }

    /** EXTRA_LEVEL / EXTRA_SCALE → 0..100, or -1 when the platform gives nothing usable. */
    fun levelPercent(level: Int, scale: Int): Int {
        if (level < 0 || scale <= 0) return -1
        return (level * 100f / scale).roundToInt().coerceIn(0, 100)
    }

    /** EXTRA_TEMPERATURE is reported in tenths of a degree Celsius. */
    fun temperatureC(tenths: Int): Float = tenths / 10f

    /**
     * BATTERY_PROPERTY_CURRENT_NOW is documented as µA, but several OEM kernels report mA.
     * Int.MIN_VALUE (and 0 on some devices) means "not supported".
     */
    fun currentMa(raw: Int): Int? {
        if (raw == Int.MIN_VALUE) return null
        return if (abs(raw) >= MICRO_AMP_THRESHOLD) raw / 1000 else raw
    }

    /** BATTERY_PROPERTY_CHARGE_COUNTER is µAh; non-positive / MIN_VALUE means unsupported. */
    fun chargeCounterMah(raw: Int): Int? {
        if (raw == Int.MIN_VALUE || raw <= 0) return null
        return raw / 1000
    }

    /** Extrapolates full capacity; only meaningful above ~20 % to keep the error small. */
    fun estimateCapacityMah(chargeMah: Int?, levelPercent: Int): Int? {
        if (chargeMah == null || levelPercent < 20) return null
        return (chargeMah * 100f / levelPercent).roundToInt()
    }
}
