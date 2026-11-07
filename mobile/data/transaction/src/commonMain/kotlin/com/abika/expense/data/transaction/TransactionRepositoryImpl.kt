package com.abika.expense.data.transaction

import com.abika.expense.core.common.ResultState
import com.abika.expense.core.common.map
import com.abika.expense.core.network.ApiClient
import com.abika.expense.data.transaction.dto.TransactionDto
import com.abika.expense.data.transaction.dto.TransactionPageDto
import com.abika.expense.data.transaction.dto.TransactionRequestDto
import com.abika.expense.domain.transaction.Transaction
import com.abika.expense.domain.transaction.TransactionInput
import com.abika.expense.domain.transaction.TransactionPage
import com.abika.expense.domain.transaction.TransactionRepository

internal class TransactionRepositoryImpl(private val api: ApiClient) : TransactionRepository {

    override suspend fun list(month: String, categoryId: Long?, page: Int, size: Int): ResultState<TransactionPage> =
        api.get<TransactionPageDto>(
            "/api/transactions",
            mapOf("month" to month, "categoryId" to categoryId, "page" to page, "size" to size),
        ).map { it.toDomain() }

    override suspend fun create(input: TransactionInput): ResultState<Transaction> =
        api.post<TransactionRequestDto, TransactionDto>("/api/transactions", TransactionRequestDto.from(input))
            .map { it.toDomain() }

    override suspend fun update(id: Long, input: TransactionInput): ResultState<Transaction> =
        api.put<TransactionRequestDto, TransactionDto>("/api/transactions/$id", TransactionRequestDto.from(input))
            .map { it.toDomain() }

    override suspend fun delete(id: Long): ResultState<Unit> = api.delete("/api/transactions/$id")
}
