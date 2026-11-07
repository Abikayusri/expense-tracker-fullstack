package com.abika.expense.feature.category

import androidx.lifecycle.viewModelScope
import com.abika.expense.core.common.BaseViewModel
import com.abika.expense.core.common.ResultState
import com.abika.expense.domain.category.CategoryRepository
import com.abika.expense.domain.category.CategoryType
import kotlinx.coroutines.launch

class CategoriesViewModel(
    private val repository: CategoryRepository,
) : BaseViewModel<CategoriesState, Nothing, CategoriesAction>(CategoriesState()) {

    init {
        load()
    }

    override fun onAction(action: CategoriesAction) {
        when (action) {
            CategoriesAction.Load -> load()
            CategoriesAction.OpenCreate -> updateState { it.copy(form = CategoryFormState(), errorMessage = null) }
            is CategoriesAction.OpenEdit -> updateState {
                val category = action.category
                it.copy(
                    form = CategoryFormState(
                        editingId = category.id,
                        name = category.name,
                        type = category.type,
                        budgetText = category.monthlyBudget?.toString().orEmpty(),
                    ),
                    errorMessage = null,
                )
            }
            is CategoriesAction.NameChanged -> updateForm { it.copy(name = action.value, fieldErrors = it.fieldErrors - "name") }
            is CategoriesAction.TypeChanged -> updateForm { if (it.isEditing) it else it.copy(type = action.value) }
            is CategoriesAction.BudgetChanged -> updateForm {
                it.copy(budgetText = action.value, fieldErrors = it.fieldErrors - "monthlyBudget")
            }
            CategoriesAction.SubmitForm -> submit()
            CategoriesAction.DismissForm -> updateState { it.copy(form = null) }
            is CategoriesAction.RequestDelete -> updateState { it.copy(pendingDelete = action.category) }
            CategoriesAction.CancelDelete -> updateState { it.copy(pendingDelete = null) }
            CategoriesAction.ConfirmDelete -> delete()
            CategoriesAction.DismissError -> updateState { it.copy(errorMessage = null) }
        }
    }

    private fun load() {
        updateState { it.copy(isLoading = true) }
        viewModelScope.launch {
            when (val result = repository.list()) {
                is ResultState.Success -> updateState { state ->
                    state.copy(
                        isLoading = false,
                        expenses = result.data.filter { it.type == CategoryType.EXPENSE },
                        incomes = result.data.filter { it.type == CategoryType.INCOME },
                    )
                }
                is ResultState.Error -> updateState { it.copy(isLoading = false, errorMessage = result.error.message) }
            }
        }
    }

    private fun submit() {
        val form = state.form ?: return
        if (form.isSubmitting) return
        val budget = parseBudget(form)
        val fieldErrors = buildMap {
            if (form.name.isBlank()) put("name", "Nama kategori wajib diisi")
            if (form.name.trim().length > MAX_NAME) put("name", "Nama kategori maksimal $MAX_NAME karakter")
            if (budget is BudgetInput.Invalid) put("monthlyBudget", "Budget harus bilangan bulat lebih dari 0")
        }
        if (fieldErrors.isNotEmpty() || budget !is BudgetInput.Valid) {
            updateForm { it.copy(fieldErrors = fieldErrors, errorMessage = null) }
            return
        }
        updateForm { it.copy(isSubmitting = true, errorMessage = null, fieldErrors = emptyMap()) }
        viewModelScope.launch {
            val result = if (form.editingId != null) {
                repository.update(form.editingId, form.name, budget.amount)
            } else {
                repository.create(form.name, form.type, budget.amount)
            }
            when (result) {
                is ResultState.Success -> {
                    updateState { it.copy(form = null) }
                    load()
                }
                is ResultState.Error -> updateForm {
                    it.copy(isSubmitting = false, errorMessage = result.error.message, fieldErrors = result.error.fieldErrors)
                }
            }
        }
    }

    private fun delete() {
        val category = state.pendingDelete ?: return
        updateState { it.copy(pendingDelete = null) }
        viewModelScope.launch {
            when (val result = repository.delete(category.id)) {
                is ResultState.Success -> load()
                is ResultState.Error -> updateState { it.copy(errorMessage = result.error.message) }
            }
        }
    }

    private fun updateForm(transform: (CategoryFormState) -> CategoryFormState) {
        updateState { state -> state.form?.let { state.copy(form = transform(it)) } ?: state }
    }

    private sealed interface BudgetInput {
        data class Valid(val amount: Long?) : BudgetInput
        data object Invalid : BudgetInput
    }

    private fun parseBudget(form: CategoryFormState): BudgetInput {
        if (form.type == CategoryType.INCOME || form.budgetText.isBlank()) return BudgetInput.Valid(null)
        val amount = form.budgetText.trim().toLongOrNull()
        return if (amount != null && amount > 0) BudgetInput.Valid(amount) else BudgetInput.Invalid
    }

    private companion object {
        const val MAX_NAME = 50
    }
}
