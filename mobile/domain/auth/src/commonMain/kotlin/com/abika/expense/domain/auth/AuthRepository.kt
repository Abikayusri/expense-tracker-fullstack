package com.abika.expense.domain.auth

import com.abika.expense.core.common.ResultState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isLoggedIn: Flow<Boolean>

    suspend fun login(email: String, password: String): ResultState<Unit>

    suspend fun register(name: String, email: String, password: String): ResultState<Unit>

    suspend fun me(): ResultState<Me>

    suspend fun logout()
}
