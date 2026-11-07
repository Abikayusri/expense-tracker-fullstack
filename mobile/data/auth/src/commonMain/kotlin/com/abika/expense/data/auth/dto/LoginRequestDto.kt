package com.abika.expense.data.auth.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class LoginRequestDto(val email: String, val password: String)
