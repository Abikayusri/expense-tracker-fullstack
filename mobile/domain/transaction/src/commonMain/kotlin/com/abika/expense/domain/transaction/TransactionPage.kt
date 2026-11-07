package com.abika.expense.domain.transaction

data class TransactionPage(
    val content: List<Transaction>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
) {
    val hasMore: Boolean get() = (page + 1).toLong() * size < totalElements
}
