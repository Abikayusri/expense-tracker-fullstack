package com.abika.expense.domain.category

import com.abika.expense.core.common.ResultState

interface CategoryRepository {
    suspend fun list(type: CategoryType? = null): ResultState<List<Category>>

    suspend fun create(name: String, type: CategoryType, monthlyBudget: Long?): ResultState<Category>

    /** The type of an existing category cannot change, so it is not sent. */
    suspend fun update(id: Long, name: String, monthlyBudget: Long?): ResultState<Category>

    suspend fun delete(id: Long): ResultState<Unit>
}
