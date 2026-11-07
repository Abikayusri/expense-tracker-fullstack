package com.abika.expense.feature.auth

import com.abika.expense.core.common.ResultState
import com.abika.expense.domain.auth.AuthRepository
import com.abika.expense.domain.auth.Me
import kotlinx.coroutines.flow.MutableStateFlow

class FakeAuthRepository : AuthRepository {
    var result: ResultState<Unit> = ResultState.Success(Unit)
    val calls = mutableListOf<String>()

    override val isLoggedIn = MutableStateFlow(false)

    override suspend fun login(email: String, password: String): ResultState<Unit> {
        calls += "login $email"
        return result
    }

    override suspend fun register(name: String, email: String, password: String): ResultState<Unit> {
        calls += "register $name $email"
        return result
    }

    override suspend fun me(): ResultState<Me> = ResultState.Success(Me(1, "budi@mail.com", "Budi"))

    override suspend fun logout() {
        calls += "logout"
    }
}
