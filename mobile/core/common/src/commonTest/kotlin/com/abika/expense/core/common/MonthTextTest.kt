package com.abika.expense.core.common

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MonthTextTest {

    @Test
    fun ofPadsTheMonth() {
        assertEquals("2026-01", MonthText.of(LocalDate(2026, 1, 31)))
    }

    @Test
    fun shiftCrossesYears() {
        assertEquals("2025-12", MonthText.shift("2026-01", -1))
        assertEquals("2027-01", MonthText.shift("2026-12", 1))
        assertEquals("2026-11", MonthText.shift("2026-11", 0))
        assertEquals("2024-11", MonthText.shift("2026-11", -24))
    }

    @Test
    fun labelInIndonesian() {
        assertEquals("November 2026", MonthText.label("2026-11"))
    }

    @Test
    fun rejectsBadInput() {
        assertFailsWith<IllegalArgumentException> { MonthText.shift("2026-13", 1) }
        assertFailsWith<IllegalArgumentException> { MonthText.label("2026") }
    }
}
