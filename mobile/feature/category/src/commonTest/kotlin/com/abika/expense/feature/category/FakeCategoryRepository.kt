package com.abika.expense.feature.category

import com.abika.expense.core.common.AppError
import com.abika.expense.core.common.ResultState
import com.abika.expense.domain.category.Category
import com.abika.expense.domain.category.CategoryRepository
import com.abika.expense.domain.category.CategoryType

class FakeCategoryRepository(initial: List<Category> = emptyList()) : CategoryRepository {
    val categories = initial.toMutableList()
    val calls = mutableListOf<String>()
    var deleteError: AppError? = null
    var saveError: AppError? = null
    private var nextId = 100L

    override suspend fun list(type: CategoryType?): ResultState<List<Category>> {
        calls += "list"
        return ResultState.Success(categories.filter { type == null || it.type == type }.sortedBy { it.name })
    }

    override suspend fun create(name: String, type: CategoryType, monthlyBudget: Long?): ResultState<Category> {
        calls += "create $name $type $monthlyBudget"
        saveError?.let { return ResultState.Error(it) }
        val category = Category(nextId++, name, type, monthlyBudget)
        categories += category
        return ResultState.Success(category)
    }

    override suspend fun update(id: Long, name: String, monthlyBudget: Long?): ResultState<Category> {
        calls += "update $id $name $monthlyBudget"
        saveError?.let { return ResultState.Error(it) }
        val index = categories.indexOfFirst { it.id == id }
        categories[index] = categories[index].copy(name = name, monthlyBudget = monthlyBudget)
        return ResultState.Success(categories[index])
    }

    override suspend fun delete(id: Long): ResultState<Unit> {
        calls += "delete $id"
        deleteError?.let { return ResultState.Error(it) }
        categories.removeAll { it.id == id }
        return ResultState.Success(Unit)
    }
}
