package com.abika.expense.data.category

import com.abika.expense.core.common.ResultState
import com.abika.expense.core.common.map
import com.abika.expense.core.network.ApiClient
import com.abika.expense.data.category.dto.CategoryDto
import com.abika.expense.data.category.dto.CreateCategoryDto
import com.abika.expense.data.category.dto.UpdateCategoryDto
import com.abika.expense.domain.category.Category
import com.abika.expense.domain.category.CategoryRepository
import com.abika.expense.domain.category.CategoryType

internal class CategoryRepositoryImpl(private val api: ApiClient) : CategoryRepository {

    override suspend fun list(type: CategoryType?): ResultState<List<Category>> =
        api.get<List<CategoryDto>>("/api/categories", mapOf("type" to type?.name)).map { list -> list.map { it.toDomain() } }

    override suspend fun create(name: String, type: CategoryType, monthlyBudget: Long?): ResultState<Category> =
        api.post<CreateCategoryDto, CategoryDto>("/api/categories", CreateCategoryDto(name.trim(), type.name, monthlyBudget))
            .map { it.toDomain() }

    override suspend fun update(id: Long, name: String, monthlyBudget: Long?): ResultState<Category> =
        api.put<UpdateCategoryDto, CategoryDto>("/api/categories/$id", UpdateCategoryDto(name.trim(), monthlyBudget))
            .map { it.toDomain() }

    override suspend fun delete(id: Long): ResultState<Unit> = api.delete("/api/categories/$id")
}
