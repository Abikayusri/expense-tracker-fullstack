package com.abika.expense.data.category.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class CreateCategoryDto(val name: String, val type: String, val monthlyBudget: Long?)
