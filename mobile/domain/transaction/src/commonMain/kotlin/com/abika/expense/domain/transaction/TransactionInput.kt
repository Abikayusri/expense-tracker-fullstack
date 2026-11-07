package com.abika.expense.domain.transaction

import kotlinx.datetime.LocalDate

data class TransactionInput(
    val categoryId: Long,
    val amount: Long,
    val date: LocalDate,
    val note: String?,
)
