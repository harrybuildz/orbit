package com.orbit

import com.orbit.ui.RelativeTime
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import org.junit.Assert.assertEquals
import org.junit.Test

class RelativeTimeTest {

    private val now: Instant = Instant.parse("2026-10-09T12:00:00Z")
    private val zone = ZoneOffset.UTC

    @Test
    fun `under a minute formats as just now`() {
        val published = now.minusSeconds(30)
        assertEquals("just now", RelativeTime.format(published, now, zone))
    }

    @Test
    fun `future timestamp clamps to just now`() {
        val published = now.plusSeconds(120)
        assertEquals("just now", RelativeTime.format(published, now, zone))
    }

    @Test
    fun `minutes ago`() {
        val published = now.minus(45, ChronoUnit.MINUTES)
        assertEquals("45m ago", RelativeTime.format(published, now, zone))
    }

    @Test
    fun `hours ago`() {
        val published = now.minus(2, ChronoUnit.HOURS)
        assertEquals("2h ago", RelativeTime.format(published, now, zone))
    }

    @Test
    fun `days ago`() {
        val published = now.minus(3, ChronoUnit.DAYS)
        assertEquals("3d ago", RelativeTime.format(published, now, zone))
    }

    @Test
    fun `a week or older falls back to an absolute date`() {
        val published = Instant.parse("2026-09-15T08:00:00Z")
        assertEquals("Sep 15, 2026", RelativeTime.format(published, now, zone))
    }
}
