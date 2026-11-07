package com.abika.expense.data.transaction.dto

import com.abika.expense.domain.transaction.TransactionPage
import kotlinx.serialization.Serializable

@Serializable
internal data class TransactionPageDto(
    val content: List<TransactionDto>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
) {
    fun toDomain() = TransactionPage(content.map { it.toDomain() }, page, size, totalElements)
}
