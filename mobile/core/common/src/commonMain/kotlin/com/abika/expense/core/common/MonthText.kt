package com.abika.expense.core.common

import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.number

/** Months travel as "YYYY-MM" strings, the format the API uses. */
object MonthText {

    fun of(date: LocalDate): String = format(date.year, date.month.number)

    fun shift(month: String, delta: Int): String {
        val (year, monthNumber) = parse(month)
        val index = year * 12 + (monthNumber - 1) + delta
        return format(index.floorDiv(12), index.mod(12) + 1)
    }

    /** "2026-11" -> "November 2026" */
    fun label(month: String): String {
        val (year, monthNumber) = parse(month)
        return "${MONTH_NAMES[monthNumber - 1]} $year"
    }

    private fun parse(month: String): Pair<Int, Int> {
        val parts = month.split("-")
        require(parts.size == 2) { "Expected YYYY-MM but was $month" }
        val monthNumber = parts[1].toInt()
        require(monthNumber in 1..12) { "Expected YYYY-MM but was $month" }
        return parts[0].toInt() to monthNumber
    }

    private fun format(year: Int, month: Int) = "$year-${month.toString().padStart(2, '0')}"

    private val MONTH_NAMES = listOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember",
    ).also { check(it.size == Month.entries.size) }
}
