package com.abika.expense.domain.transaction

import kotlinx.datetime.LocalDate

data class Transaction(
    val id: Long,
    val categoryId: Long,
    val amount: Long,
    val date: LocalDate,
    val note: String?,
)
