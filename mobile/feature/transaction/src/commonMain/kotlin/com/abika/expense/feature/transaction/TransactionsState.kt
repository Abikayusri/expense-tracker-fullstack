package com.abika.expense.feature.transaction

import com.abika.expense.domain.category.Category
import com.abika.expense.domain.transaction.Transaction

data class TransactionsState(
    val month: String,
    val categoryFilter: Long? = null,
    val items: List<Transaction> = emptyList(),
    val page: Int = 0,
    val hasMore: Boolean = false,
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val categories: List<Category> = emptyList(),
    val errorMessage: String? = null,
    val form: TransactionFormState? = null,
    val pendingDelete: Transaction? = null,
)
