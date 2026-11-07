package com.abika.expense.data.auth

import com.abika.expense.core.common.ResultState
import com.abika.expense.core.datastore.TokenStore
import com.abika.expense.core.network.ApiClient
import com.abika.expense.core.network.ApiConfig
import com.abika.expense.core.network.SessionEvents
import com.abika.expense.core.network.createHttpClient
import com.abika.expense.domain.auth.LogoutHook
import com.abika.expense.domain.auth.Me
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AuthRepositoryImplTest {

    private class MemoryTokenStore : TokenStore {
        override val token = MutableStateFlow<String?>(null)
        override suspend fun current() = token.value
        override suspend fun save(token: String) { this.token.value = token }
        override suspend fun clear() { token.value = null }
    }

    private val tokenStore = MemoryTokenStore()
    private val events = mutableListOf<String>()

    private fun repository(status: HttpStatusCode, body: String, hooks: List<LogoutHook> = emptyList()): AuthRepositoryImpl {
        val engine = MockEngine { request ->
            events += "${request.method.value} ${request.url.encodedPath} ${request.body.toByteArray().decodeToString()}"
            respond(body, status, headersOf(HttpHeaders.ContentType, "application/json"))
        }
        val http = createHttpClient(engine, ApiConfig("http://test", enableNetworkInspector = false), tokenStore)
        return AuthRepositoryImpl(ApiClient(http, tokenStore, SessionEvents()), tokenStore, hooks)
    }

    @Test
    fun loginStoresTheTokenAndTrimsEmail() = runTest {
        val repo = repository(HttpStatusCode.OK, """{"token":"jwt-1"}""")

        assertEquals(ResultState.Success(Unit), repo.login("  budi@mail.com ", "rahasia123"))
        assertEquals("jwt-1", tokenStore.current())
        assertTrue(repo.isLoggedIn.first())
        assertEquals("""POST /api/auth/login {"email":"budi@mail.com","password":"rahasia123"}""", events.single())
    }

    @Test
    fun failedLoginKeepsNoToken() = runTest {
        val repo = repository(HttpStatusCode.Unauthorized, """{"detail":"Email atau password salah"}""")

        val result = repo.login("budi@mail.com", "salah")

        assertIs<ResultState.Error>(result)
        assertEquals("Email atau password salah", result.error.message)
        assertNull(tokenStore.current())
        assertFalse(repo.isLoggedIn.first())
    }

    @Test
    fun registerStoresTheToken() = runTest {
        val repo = repository(HttpStatusCode.Created, """{"token":"jwt-2"}""")

        repo.register("Budi", "budi@mail.com", "rahasia123")

        assertEquals("jwt-2", tokenStore.current())
    }

    @Test
    fun meMapsTheResponse() = runTest {
        val repo = repository(HttpStatusCode.OK, """{"id":7,"email":"budi@mail.com","name":"Budi"}""")

        assertEquals(ResultState.Success(Me(7, "budi@mail.com", "Budi")), repo.me())
    }

    @Test
    fun logoutRunsHooksBeforeClearingTheToken() = runTest {
        tokenStore.save("jwt-1")
        var tokenSeenByHook: String? = null
        val repo = repository(HttpStatusCode.OK, "{}", hooks = listOf(LogoutHook { tokenSeenByHook = tokenStore.current() }))

        repo.logout()

        assertEquals("jwt-1", tokenSeenByHook)
        assertNull(tokenStore.current())
    }
}
