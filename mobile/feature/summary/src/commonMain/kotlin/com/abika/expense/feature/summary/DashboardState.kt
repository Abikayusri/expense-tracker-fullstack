package com.abika.expense.feature.summary

import com.abika.expense.domain.summary.Summary

data class DashboardState(
    val month: String,
    val summary: Summary? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
