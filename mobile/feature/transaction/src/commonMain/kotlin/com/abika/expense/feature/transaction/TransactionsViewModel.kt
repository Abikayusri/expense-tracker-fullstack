package com.abika.expense.feature.transaction

import androidx.lifecycle.viewModelScope
import com.abika.expense.core.common.BaseViewModel
import com.abika.expense.core.common.MonthText
import com.abika.expense.core.common.ResultState
import com.abika.expense.domain.category.CategoryRepository
import com.abika.expense.domain.transaction.TransactionInput
import com.abika.expense.domain.transaction.TransactionRepository
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class TransactionsViewModel(
    private val transactions: TransactionRepository,
    private val categories: CategoryRepository,
    private val today: () -> LocalDate,
) : BaseViewModel<TransactionsState, Nothing, TransactionsAction>(TransactionsState(month = MonthText.of(today()))) {

    // Bumped on every refresh so a slow response for an old month or filter is dropped.
    private var generation = 0

    init {
        loadCategories()
        refresh()
    }

    override fun onAction(action: TransactionsAction) {
        when (action) {
            is TransactionsAction.ChangeMonth -> {
                updateState { it.copy(month = MonthText.shift(it.month, action.delta)) }
                refresh()
            }
            is TransactionsAction.ChangeFilter -> {
                updateState { it.copy(categoryFilter = action.categoryId) }
                refresh()
            }
            TransactionsAction.LoadMore -> loadMore()
            TransactionsAction.Refresh -> {
                loadCategories()
                refresh()
            }
            TransactionsAction.OpenCreate -> updateState { it.copy(form = TransactionFormState(date = today())) }
            is TransactionsAction.OpenEdit -> updateState {
                val transaction = action.transaction
                it.copy(
                    form = TransactionFormState(
                        date = transaction.date,
                        editingId = transaction.id,
                        categoryId = transaction.categoryId,
                        amountText = transaction.amount.toString(),
                        note = transaction.note.orEmpty(),
                    ),
                )
            }
            is TransactionsAction.CategoryChanged -> updateForm {
                it.copy(categoryId = action.categoryId, fieldErrors = it.fieldErrors - "categoryId")
            }
            is TransactionsAction.AmountChanged -> updateForm { it.copy(amountText = action.value, fieldErrors = it.fieldErrors - "amount") }
            is TransactionsAction.DateChanged -> updateForm { it.copy(date = action.value) }
            is TransactionsAction.NoteChanged -> updateForm { it.copy(note = action.value, fieldErrors = it.fieldErrors - "note") }
            TransactionsAction.SubmitForm -> submit()
            TransactionsAction.DismissForm -> updateState { it.copy(form = null) }
            is TransactionsAction.RequestDelete -> updateState { it.copy(pendingDelete = action.transaction) }
            TransactionsAction.CancelDelete -> updateState { it.copy(pendingDelete = null) }
            TransactionsAction.ConfirmDelete -> delete()
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val result = categories.list()
            if (result is ResultState.Success) updateState { it.copy(categories = result.data) }
        }
    }

    private fun refresh() {
        val requested = ++generation
        updateState { it.copy(isLoading = true, isLoadingMore = false, errorMessage = null) }
        viewModelScope.launch {
            val result = transactions.list(state.month, state.categoryFilter, 0, PAGE_SIZE)
            if (requested != generation) return@launch
            when (result) {
                is ResultState.Success -> updateState {
                    it.copy(isLoading = false, items = result.data.content, page = 0, hasMore = result.data.hasMore)
                }
                is ResultState.Error -> updateState {
                    it.copy(isLoading = false, items = emptyList(), hasMore = false, errorMessage = result.error.message)
                }
            }
        }
    }

    private fun loadMore() {
        if (!state.hasMore || state.isLoading || state.isLoadingMore) return
        val requested = generation
        val nextPage = state.page + 1
        updateState { it.copy(isLoadingMore = true) }
        viewModelScope.launch {
            val result = transactions.list(state.month, state.categoryFilter, nextPage, PAGE_SIZE)
            if (requested != generation) return@launch
            when (result) {
                is ResultState.Success -> updateState {
                    it.copy(
                        isLoadingMore = false,
                        items = (it.items + result.data.content).distinctBy { item -> item.id },
                        page = nextPage,
                        hasMore = result.data.hasMore,
                    )
                }
                is ResultState.Error -> updateState { it.copy(isLoadingMore = false, errorMessage = result.error.message) }
            }
        }
    }

    private fun submit() {
        val form = state.form ?: return
        if (form.isSubmitting) return
        val amount = form.amountText.trim().toLongOrNull()?.takeIf { it > 0 }
        val fieldErrors = buildMap {
            if (form.categoryId == null) put("categoryId", "Pilih kategori")
            if (amount == null) put("amount", "Jumlah harus bilangan bulat lebih dari 0")
            if (form.note.length > MAX_NOTE) put("note", "Catatan maksimal $MAX_NOTE karakter")
        }
        if (fieldErrors.isNotEmpty() || form.categoryId == null || amount == null) {
            updateForm { it.copy(fieldErrors = fieldErrors, errorMessage = null) }
            return
        }
        val input = TransactionInput(form.categoryId, amount, form.date, form.note.trim().ifEmpty { null })
        updateForm { it.copy(isSubmitting = true, errorMessage = null, fieldErrors = emptyMap()) }
        viewModelScope.launch {
            val result = form.editingId?.let { transactions.update(it, input) } ?: transactions.create(input)
            when (result) {
                is ResultState.Success -> {
                    updateState { it.copy(form = null) }
                    refresh()
                }
                is ResultState.Error -> updateForm {
                    it.copy(isSubmitting = false, errorMessage = result.error.message, fieldErrors = result.error.fieldErrors)
                }
            }
        }
    }

    private fun delete() {
        val transaction = state.pendingDelete ?: return
        updateState { it.copy(pendingDelete = null) }
        viewModelScope.launch {
            when (val result = transactions.delete(transaction.id)) {
                is ResultState.Success -> refresh()
                is ResultState.Error -> updateState { it.copy(errorMessage = result.error.message) }
            }
        }
    }

    private fun updateForm(transform: (TransactionFormState) -> TransactionFormState) {
        updateState { state -> state.form?.let { state.copy(form = transform(it)) } ?: state }
    }

    companion object {
        const val PAGE_SIZE = 20
        private const val MAX_NOTE = 255
    }
}
