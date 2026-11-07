package com.abika.expense.domain.transaction

import com.abika.expense.core.common.ResultState

interface TransactionRepository {
    /** [month] is "YYYY-MM"; results are newest first. */
    suspend fun list(month: String, categoryId: Long?, page: Int, size: Int): ResultState<TransactionPage>

    suspend fun create(input: TransactionInput): ResultState<Transaction>

    suspend fun update(id: Long, input: TransactionInput): ResultState<Transaction>

    suspend fun delete(id: Long): ResultState<Unit>
}
