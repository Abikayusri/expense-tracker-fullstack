package com.abika.expense.core.common

import kotlin.test.Test
import kotlin.test.assertEquals

class RupiahTest {

    @Test
    fun formatsWithDotsAndSign() {
        assertEquals("Rp0", formatRupiah(0))
        assertEquals("Rp999", formatRupiah(999))
        assertEquals("Rp1.000", formatRupiah(1_000))
        assertEquals("Rp1.250.000", formatRupiah(1_250_000))
        assertEquals("-Rp150.000", formatRupiah(-150_000))
        assertEquals("-Rp9.223.372.036.854.775.808", formatRupiah(Long.MIN_VALUE))
    }
}
