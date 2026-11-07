package com.abika.expense.data.auth.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class RegisterRequestDto(val name: String, val email: String, val password: String)
