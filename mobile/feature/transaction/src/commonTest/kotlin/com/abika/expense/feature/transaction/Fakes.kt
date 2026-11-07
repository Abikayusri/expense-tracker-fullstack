package com.abika.expense.feature.transaction

import com.abika.expense.core.common.ResultState
import com.abika.expense.domain.category.Category
import com.abika.expense.domain.category.CategoryRepository
import com.abika.expense.domain.category.CategoryType
import com.abika.expense.domain.transaction.Transaction
import com.abika.expense.domain.transaction.TransactionInput
import com.abika.expense.domain.transaction.TransactionPage
import com.abika.expense.domain.transaction.TransactionRepository
import kotlinx.coroutines.CompletableDeferred

class FakeCategoryRepository(private val categories: List<Category>) : CategoryRepository {
    override suspend fun list(type: CategoryType?) = ResultState.Success(categories)
    override suspend fun create(name: String, type: CategoryType, monthlyBudget: Long?): ResultState<Category> = error("unused")
    override suspend fun update(id: Long, name: String, monthlyBudget: Long?): ResultState<Category> = error("unused")
    override suspend fun delete(id: Long): ResultState<Unit> = error("unused")
}

class FakeTransactionRepository(var all: List<Transaction> = emptyList()) : TransactionRepository {
    val listCalls = mutableListOf<String>()
    val saved = mutableListOf<String>()

    /** When set, list() waits on it, so tests can hold a request in flight. */
    var gate: CompletableDeferred<Unit>? = null

    override suspend fun list(month: String, categoryId: Long?, page: Int, size: Int): ResultState<TransactionPage> {
        listCalls += "$month ${categoryId ?: "-"} p$page"
        gate?.await()
        val matching = all.filter { categoryId == null || it.categoryId == categoryId }
        return ResultState.Success(TransactionPage(matching.drop(page * size).take(size), page, size, matching.size.toLong()))
    }

    override suspend fun create(input: TransactionInput): ResultState<Transaction> {
        saved += "create $input"
        return ResultState.Success(Transaction(999, input.categoryId, input.amount, input.date, input.note))
    }

    override suspend fun update(id: Long, input: TransactionInput): ResultState<Transaction> {
        saved += "update $id $input"
        return ResultState.Success(Transaction(id, input.categoryId, input.amount, input.date, input.note))
    }

    override suspend fun delete(id: Long): ResultState<Unit> {
        saved += "delete $id"
        return ResultState.Success(Unit)
    }
}
