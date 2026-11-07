package com.abika.expense.data.auth.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class TokenResponseDto(val token: String)
