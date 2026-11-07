package com.abika.expense.domain.summary

import com.abika.expense.core.common.ResultState

interface SummaryRepository {
    /** [month] is "YYYY-MM". */
    suspend fun get(month: String): ResultState<Summary>
}
