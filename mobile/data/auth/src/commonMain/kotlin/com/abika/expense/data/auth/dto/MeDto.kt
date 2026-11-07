package com.abika.expense.data.auth.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class MeDto(val id: Long, val email: String, val name: String)
