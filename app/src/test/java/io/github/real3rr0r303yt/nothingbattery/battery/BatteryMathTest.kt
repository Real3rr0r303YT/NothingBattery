package io.github.real3rr0r303yt.nothingbattery.battery

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BatteryMathTest {

    @Test fun `level is scaled to percent`() {
        assertEquals(87, BatteryMath.levelPercent(87, 100))
        assertEquals(50, BatteryMath.levelPercent(128, 256))
        assertEquals(-1, BatteryMath.levelPercent(-1, 100))
        assertEquals(-1, BatteryMath.levelPercent(50, 0))
    }

    @Test fun `temperature is tenths of a degree`() {
        assertEquals(31.4f, BatteryMath.temperatureC(314), 0.001f)
    }

    @Test fun `current in microamps is converted to milliamps`() {
        assertEquals(1480, BatteryMath.currentMa(1_480_000))
        assertEquals(-350, BatteryMath.currentMa(-350_000))
    }

    @Test fun `current already in milliamps is kept`() {
        assertEquals(1480, BatteryMath.currentMa(1480))
        assertEquals(-350, BatteryMath.currentMa(-350))
    }

    @Test fun `unsupported current is null`() {
        assertNull(BatteryMath.currentMa(Int.MIN_VALUE))
    }

    @Test fun `charge counter is converted to mAh`() {
        assertEquals(3936, BatteryMath.chargeCounterMah(3_936_000))
        assertNull(BatteryMath.chargeCounterMah(0))
        assertNull(BatteryMath.chargeCounterMah(Int.MIN_VALUE))
    }

    @Test fun `capacity estimate needs enough charge`() {
        assertEquals(4800, BatteryMath.estimateCapacityMah(3840, 80))
        assertNull(BatteryMath.estimateCapacityMah(500, 10))
        assertNull(BatteryMath.estimateCapacityMah(null, 80))
    }

    @Test fun `plug bitmask maps to type`() {
        assertEquals(PlugType.AC, BatteryMath.plug(BatteryMath.PLUGGED_AC))
        assertEquals(PlugType.WIRELESS, BatteryMath.plug(BatteryMath.PLUGGED_WIRELESS))
        assertEquals(PlugType.NONE, BatteryMath.plug(0))
    }

    @Test fun `status and health map platform constants`() {
        assertEquals(ChargeStatus.CHARGING, BatteryMath.status(BatteryMath.STATUS_CHARGING))
        assertEquals(ChargeStatus.UNKNOWN, BatteryMath.status(42))
        assertEquals(BatteryHealth.OVERHEAT, BatteryMath.health(BatteryMath.HEALTH_OVERHEAT))
        assertEquals(BatteryHealth.UNKNOWN, BatteryMath.health(-1))
    }
}
