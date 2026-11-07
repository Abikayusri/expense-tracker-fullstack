package com.abika.expense.core.common

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class DateTextTest {

    @Test
    fun shortIndonesianDate() {
        assertEquals("7 Nov 2026", DateText.short(LocalDate(2026, 11, 7)))
        assertEquals("1 Agu 2026", DateText.short(LocalDate(2026, 8, 1)))
    }
}
