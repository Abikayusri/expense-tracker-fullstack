package com.abika.expense.data.auth

import com.abika.expense.core.common.ResultState
import com.abika.expense.core.datastore.TokenStore
import com.abika.expense.core.network.ApiClient
import com.abika.expense.core.network.ApiConfig
import com.abika.expense.core.network.SessionEvents
import com.abika.expense.core.network.createHttpClient
import com.abika.expense.domain.auth.DeviceRegistry
import com.abika.expense.domain.auth.LoginHook
import com.abika.expense.domain.auth.PushTokenProvider
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PushDeviceTest {

    private class MemoryTokenStore : TokenStore {
        override val token = MutableStateFlow<String?>(null)
        override suspend fun current() = token.value
        override suspend fun save(token: String) { this.token.value = token }
        override suspend fun clear() { token.value = null }
    }

    private class RecordingRegistry : DeviceRegistry {
        val calls = mutableListOf<String>()
        override suspend fun register(fcmToken: String): ResultState<Unit> {
            calls += "register $fcmToken"
            return ResultState.Success(Unit)
        }
        override suspend fun unregister(fcmToken: String): ResultState<Unit> {
            calls += "unregister $fcmToken"
            return ResultState.Success(Unit)
        }
    }

    private val requests = mutableListOf<String>()
    private val tokenStore = MemoryTokenStore()

    private fun api(status: HttpStatusCode = HttpStatusCode.NoContent, body: String = ""): ApiClient {
        val engine = MockEngine { request ->
            val payload = request.body.toByteArray().decodeToString()
            requests += "${request.method.value} ${request.url.encodedPath} $payload".trim()
            respond(body, status, headersOf(HttpHeaders.ContentType, "application/json"))
        }
        val http = createHttpClient(engine, ApiConfig("http://test", enableNetworkInspector = false), tokenStore)
        return ApiClient(http, tokenStore, SessionEvents())
    }

    @Test
    fun registerPostsTokenAndPlatform() = runTest {
        DeviceRegistryImpl(api()).register("fcm:abc")

        assertEquals("""POST /api/devices {"fcmToken":"fcm:abc","platform":"ANDROID"}""", requests.single())
    }

    @Test
    fun unregisterEncodesTheToken() = runTest {
        DeviceRegistryImpl(api()).unregister("fcm:a/b c")

        assertEquals("DELETE /api/devices/fcm:a%2Fb%20c", requests.single())
    }

    @Test
    fun hooksRegisterAfterLoginAndUnregisterBeforeLogout() = runTest {
        val registry = RecordingRegistry()
        val hooks = PushDeviceHooks({ "token-1" }, registry)

        hooks.afterLogin()
        hooks.beforeLogout()

        assertEquals(listOf("register token-1", "unregister token-1"), registry.calls)
    }

    @Test
    fun noTokenMeansNoCalls() = runTest {
        val registry = RecordingRegistry()
        val hooks = PushDeviceHooks({ null }, registry)

        hooks.afterLogin()
        hooks.beforeLogout()

        assertTrue(registry.calls.isEmpty())
    }

    @Test
    fun brokenTokenProviderNeverBlocksLogin() = runTest {
        val registry = RecordingRegistry()
        val hooks = PushDeviceHooks(PushTokenProvider { error("Firebase not initialised") }, registry)

        hooks.afterLogin()

        assertTrue(registry.calls.isEmpty())
    }

    @Test
    fun loginRunsHooksAfterTheTokenIsStored() = runTest {
        var tokenSeenByHook: String? = null
        val repository = AuthRepositoryImpl(
            api(HttpStatusCode.OK, """{"token":"jwt-1"}"""),
            tokenStore,
            logoutHooks = emptyList(),
            loginHooks = listOf(LoginHook { tokenSeenByHook = tokenStore.current() }),
        )

        repository.login("budi@mail.com", "rahasia123")

        assertEquals("jwt-1", tokenSeenByHook)
    }

    @Test
    fun failedLoginRunsNoHooks() = runTest {
        var called = false
        val repository = AuthRepositoryImpl(
            api(HttpStatusCode.Unauthorized, """{"detail":"Email atau password salah"}"""),
            tokenStore,
            logoutHooks = emptyList(),
            loginHooks = listOf(LoginHook { called = true }),
        )

        repository.login("budi@mail.com", "salah")

        assertEquals(false, called)
    }
}
