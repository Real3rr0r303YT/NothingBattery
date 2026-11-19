// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 Real3rr0r303YT — NothingBattery. Made by Real3rr0r303YT.

package io.github.real3rr0r303yt.nothingbattery.battery

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ChargeLimitPolicyTest {

    @Test fun `alerts once when limit is reached while plugged`() {
        val first = ChargeLimitPolicy.evaluate(80, plugged = true, limit = 80, armed = true)
        assertTrue(first.notify)
        assertFalse(first.armed)

        val second = ChargeLimitPolicy.evaluate(81, plugged = true, limit = 80, armed = first.armed)
        assertFalse(second.notify)
    }

    @Test fun `no alert below limit or when unplugged`() {
        assertFalse(ChargeLimitPolicy.evaluate(79, plugged = true, limit = 80, armed = true).notify)
        assertFalse(ChargeLimitPolicy.evaluate(90, plugged = false, limit = 80, armed = true).notify)
    }

    @Test fun `unplugging re-arms`() {
        val d = ChargeLimitPolicy.evaluate(85, plugged = false, limit = 85, armed = false)
        assertTrue(d.armed)
    }

    @Test fun `small dips below the limit do not re-arm`() {
        val d = ChargeLimitPolicy.evaluate(78, plugged = true, limit = 80, armed = false)
        assertFalse(d.armed)
        val rearmed = ChargeLimitPolicy.evaluate(76, plugged = true, limit = 80, armed = false)
        assertTrue(rearmed.armed)
    }

    @Test fun `indicator reflects distance to limit`() {
        assertEquals(LimitIndicator.IDLE, ChargeLimitPolicy.indicator(60, plugged = true, limit = 80))
        assertEquals(LimitIndicator.APPROACHING, ChargeLimitPolicy.indicator(76, plugged = true, limit = 80))
        assertEquals(LimitIndicator.REACHED, ChargeLimitPolicy.indicator(85, plugged = true, limit = 85))
        assertEquals(LimitIndicator.IDLE, ChargeLimitPolicy.indicator(90, plugged = false, limit = 80))
    }
}
