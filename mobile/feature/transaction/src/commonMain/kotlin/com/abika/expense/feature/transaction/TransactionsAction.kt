package com.abika.expense.feature.transaction

import com.abika.expense.domain.transaction.Transaction
import kotlinx.datetime.LocalDate

sealed interface TransactionsAction {
    data class ChangeMonth(val delta: Int) : TransactionsAction
    data class ChangeFilter(val categoryId: Long?) : TransactionsAction
    data object LoadMore : TransactionsAction
    data object Refresh : TransactionsAction
    data object OpenCreate : TransactionsAction
    data class OpenEdit(val transaction: Transaction) : TransactionsAction
    data class CategoryChanged(val categoryId: Long) : TransactionsAction
    data class AmountChanged(val value: String) : TransactionsAction
    data class DateChanged(val value: LocalDate) : TransactionsAction
    data class NoteChanged(val value: String) : TransactionsAction
    data object SubmitForm : TransactionsAction
    data object DismissForm : TransactionsAction
    data class RequestDelete(val transaction: Transaction) : TransactionsAction
    data object ConfirmDelete : TransactionsAction
    data object CancelDelete : TransactionsAction
}
