package com.abika.expense.core.network

import app.cash.turbine.test
import com.abika.expense.core.common.AppError
import com.abika.expense.core.common.ResultState
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ApiClientTest {

    @Serializable
    data class Item(val id: Long, val name: String)

    private val tokenStore = FakeTokenStore("jwt-1")
    private val sessionEvents = SessionEvents()
    private val requests = mutableListOf<HttpRequestData>()

    private fun client(handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData): ApiClient {
        val engine = MockEngine { request ->
            requests += request
            handler(request)
        }
        val http = createHttpClient(engine, ApiConfig("http://test", enableNetworkInspector = false), tokenStore)
        return ApiClient(http, tokenStore, sessionEvents)
    }

    private fun MockRequestHandleScope.json(status: HttpStatusCode, body: String) =
        respond(body, status, headersOf(HttpHeaders.ContentType, "application/json"))

    @Test
    fun sendsBearerTokenAndDecodesBody() = runTest {
        val api = client { json(HttpStatusCode.OK, """{"id":1,"name":"Makan","extra":true}""") }

        val result = api.get<Item>("/api/categories/1", mapOf("type" to "EXPENSE", "skip" to null))

        assertEquals(ResultState.Success(Item(1, "Makan")), result)
        assertEquals("Bearer jwt-1", requests.single().headers[HttpHeaders.Authorization])
        assertEquals("http://test/api/categories/1?type=EXPENSE", requests.single().url.toString())
    }

    @Test
    fun postsJsonBody() = runTest {
        val api = client { json(HttpStatusCode.Created, """{"id":2,"name":"Hobi"}""") }

        api.post<Item, Item>("/api/categories", Item(0, "Hobi"))

        assertEquals("""{"id":0,"name":"Hobi"}""", requests.single().body.toByteArray().decodeToString())
    }

    @Test
    fun noContentIsUnit() = runTest {
        val api = client { respond("", HttpStatusCode.NoContent) }

        assertEquals(ResultState.Success(Unit), api.delete("/api/categories/1"))
    }

    @Test
    fun problemDetailBecomesAppError() = runTest {
        val api = client { json(HttpStatusCode.Conflict, """{"status":409,"detail":"Kategori masih dipakai transaksi"}""") }

        assertEquals(
            ResultState.Error(AppError(409, "Kategori masih dipakai transaksi")),
            api.delete("/api/categories/1"),
        )
    }

    @Test
    fun fieldErrorsAreKept() = runTest {
        val api = client {
            json(HttpStatusCode.BadRequest, """{"detail":"Input tidak valid","errors":{"password":"size must be between 8 and 72"}}""")
        }

        val result = api.post<Item, Item>("/api/auth/register", Item(0, "x")) as ResultState.Error

        assertEquals(mapOf("password" to "size must be between 8 and 72"), result.error.fieldErrors)
    }

    @Test
    fun unauthorizedClearsTokenAndNotifiesOnce() = runTest {
        val api = client { json(HttpStatusCode.Unauthorized, """{"detail":"Token tidak ada, tidak valid, atau kedaluwarsa"}""") }

        sessionEvents.unauthorized.test {
            api.get<Item>("/api/me")
            awaitItem()
            expectNoEvents()
        }
        assertNull(tokenStore.current())
    }

    @Test
    fun failedLoginDoesNotEndTheSession() = runTest {
        val api = client { json(HttpStatusCode.Unauthorized, """{"detail":"Email atau password salah"}""") }

        sessionEvents.unauthorized.test {
            val result = api.post<Item, Item>("/api/auth/login", Item(0, "x")) as ResultState.Error
            assertEquals("Email atau password salah", result.error.message)
            expectNoEvents()
        }
        assertEquals("jwt-1", tokenStore.current())
    }

    @Test
    fun networkFailureIsStatusZero() = runTest {
        val api = client { throw IOException("offline") }

        assertEquals(ResultState.Error(ApiClient.NETWORK_ERROR), api.get<Item>("/api/me"))
    }

    @Test
    fun nonJsonErrorBodyStillProducesAMessage() = runTest {
        val api = client { respond("Bad Gateway", HttpStatusCode.BadGateway) }

        assertEquals(ResultState.Error(AppError(502, "Terjadi kesalahan (502)")), api.get<Item>("/api/me"))
    }
}
