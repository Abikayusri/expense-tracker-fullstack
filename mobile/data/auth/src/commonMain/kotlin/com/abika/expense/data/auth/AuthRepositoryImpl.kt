package com.abika.expense.data.auth

import com.abika.expense.core.common.ResultState
import com.abika.expense.core.common.map
import com.abika.expense.core.datastore.TokenStore
import com.abika.expense.core.network.ApiClient
import com.abika.expense.data.auth.dto.LoginRequestDto
import com.abika.expense.data.auth.dto.MeDto
import com.abika.expense.data.auth.dto.RegisterRequestDto
import com.abika.expense.data.auth.dto.TokenResponseDto
import com.abika.expense.domain.auth.AuthRepository
import com.abika.expense.domain.auth.LogoutHook
import com.abika.expense.domain.auth.Me
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal class AuthRepositoryImpl(
    private val api: ApiClient,
    private val tokenStore: TokenStore,
    private val logoutHooks: List<LogoutHook>,
) : AuthRepository {

    override val isLoggedIn: Flow<Boolean> = tokenStore.token.map { it != null }.distinctUntilChanged()

    override suspend fun login(email: String, password: String): ResultState<Unit> =
        storeToken(api.post<LoginRequestDto, TokenResponseDto>("/api/auth/login", LoginRequestDto(email.trim(), password)))

    override suspend fun register(name: String, email: String, password: String): ResultState<Unit> =
        storeToken(
            api.post<RegisterRequestDto, TokenResponseDto>(
                "/api/auth/register",
                RegisterRequestDto(name.trim(), email.trim(), password),
            ),
        )

    override suspend fun me(): ResultState<Me> =
        api.get<MeDto>("/api/me").map { Me(it.id, it.email, it.name) }

    override suspend fun logout() {
        logoutHooks.forEach { it.beforeLogout() }
        tokenStore.clear()
    }

    private suspend fun storeToken(result: ResultState<TokenResponseDto>): ResultState<Unit> {
        if (result is ResultState.Success) {
            tokenStore.save(result.data.token)
        }
        return result.map { }
    }
}
