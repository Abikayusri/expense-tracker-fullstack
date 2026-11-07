package com.abika.expense.data.category.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class UpdateCategoryDto(val name: String, val monthlyBudget: Long?)
