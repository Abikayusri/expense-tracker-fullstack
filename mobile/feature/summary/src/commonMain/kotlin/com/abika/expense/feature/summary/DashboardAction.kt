package com.abika.expense.feature.summary

sealed interface DashboardAction {
    data class ChangeMonth(val delta: Int) : DashboardAction
    data object Refresh : DashboardAction
}
