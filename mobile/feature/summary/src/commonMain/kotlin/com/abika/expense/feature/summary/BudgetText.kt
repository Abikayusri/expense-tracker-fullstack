package com.abika.expense.feature.summary

import com.abika.expense.core.common.formatRupiah
import com.abika.expense.domain.category.CategoryType
import com.abika.expense.domain.summary.CategorySummary
import com.abika.expense.domain.summary.Summary

/** Pure helpers behind the dashboard, kept out of composables so they are easy to test. */
object BudgetText {

    fun overBudgetLines(summary: Summary): List<String> =
        summary.perCategory.filter { it.overBudget }.map {
            "Pengeluaran ${it.name} sudah ${formatRupiah(it.spent)}, melebihi budget ${formatRupiah(it.budget ?: 0)}"
        }

    fun budgets(summary: Summary): List<CategorySummary> =
        summary.perCategory.filter { it.type == CategoryType.EXPENSE && it.budget != null }

    fun progress(item: CategorySummary): Float {
        val budget = item.budget ?: return 0f
        if (budget <= 0) return 0f
        return (item.spent.toFloat() / budget).coerceIn(0f, 1f)
    }
}
