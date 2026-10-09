package com.orbit.ui

import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Pure-Kotlin relative timestamp formatter ("2h ago"), kept free of Android
 * dependencies so it is unit-testable on the JVM with a fixed "now".
 */
object RelativeTime {

    private val fallbackFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US)

    fun format(
        publishedAt: Instant,
        now: Instant,
        zone: ZoneId = ZoneId.systemDefault(),
    ): String {
        val elapsed = Duration.between(publishedAt, now)
        return when {
            elapsed.isNegative || elapsed.toMinutes() < 1 -> "just now"
            elapsed.toMinutes() < 60 -> "${elapsed.toMinutes()}m ago"
            elapsed.toHours() < 24 -> "${elapsed.toHours()}h ago"
            elapsed.toDays() < 7 -> "${elapsed.toDays()}d ago"
            else -> fallbackFormatter.format(publishedAt.atZone(zone))
        }
    }
}
