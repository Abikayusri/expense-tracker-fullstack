package com.abika.expense.feature.category

import com.abika.expense.domain.category.Category
import com.abika.expense.domain.category.CategoryType

sealed interface CategoriesAction {
    data object Load : CategoriesAction
    data object OpenCreate : CategoriesAction
    data class OpenEdit(val category: Category) : CategoriesAction
    data class NameChanged(val value: String) : CategoriesAction
    data class TypeChanged(val value: CategoryType) : CategoriesAction
    data class BudgetChanged(val value: String) : CategoriesAction
    data object SubmitForm : CategoriesAction
    data object DismissForm : CategoriesAction
    data class RequestDelete(val category: Category) : CategoriesAction
    data object ConfirmDelete : CategoriesAction
    data object CancelDelete : CategoriesAction
    data object DismissError : CategoriesAction
}
