package com.abika.expense.data.summary

import com.abika.expense.core.common.ResultState
import com.abika.expense.core.common.map
import com.abika.expense.core.network.ApiClient
import com.abika.expense.data.summary.dto.SummaryDto
import com.abika.expense.domain.summary.Summary
import com.abika.expense.domain.summary.SummaryRepository

internal class SummaryRepositoryImpl(private val api: ApiClient) : SummaryRepository {

    override suspend fun get(month: String): ResultState<Summary> =
        api.get<SummaryDto>("/api/summary", mapOf("month" to month)).map { it.toDomain() }
}
