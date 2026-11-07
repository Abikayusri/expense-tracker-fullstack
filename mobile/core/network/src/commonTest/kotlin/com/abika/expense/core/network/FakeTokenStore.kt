package com.abika.expense.core.network

import com.abika.expense.core.datastore.TokenStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeTokenStore(initial: String? = null) : TokenStore {
    override val token: StateFlow<String?> = MutableStateFlow(initial)

    override suspend fun current(): String? = token.value

    override suspend fun save(token: String) {
        (this.token as MutableStateFlow).value = token
    }

    override suspend fun clear() {
        (token as MutableStateFlow).value = null
    }
}
