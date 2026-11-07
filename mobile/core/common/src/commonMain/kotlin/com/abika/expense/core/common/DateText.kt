package com.abika.expense.core.common

import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

object DateText {
    /** 2026-11-07 -> "7 Nov 2026" */
    fun short(date: LocalDate): String = "${date.day} ${SHORT_MONTHS[date.month.number - 1]} ${date.year}"

    private val SHORT_MONTHS = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")
}
