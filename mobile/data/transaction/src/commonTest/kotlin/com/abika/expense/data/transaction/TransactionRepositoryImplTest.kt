package com.abika.expense.data.transaction

import com.abika.expense.core.common.ResultState
import com.abika.expense.core.datastore.TokenStore
import com.abika.expense.core.network.ApiClient
import com.abika.expense.core.network.ApiConfig
import com.abika.expense.core.network.SessionEvents
import com.abika.expense.core.network.createHttpClient
import com.abika.expense.domain.transaction.Transaction
import com.abika.expense.domain.transaction.TransactionInput
import com.abika.expense.domain.transaction.TransactionPage
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TransactionRepositoryImplTest {

    private object NoToken : TokenStore {
        override val token = MutableStateFlow<String?>(null)
        override suspend fun current(): String? = null
        override suspend fun save(token: String) = Unit
        override suspend fun clear() = Unit
    }

    private val requests = mutableListOf<String>()

    private fun repository(status: HttpStatusCode, body: String): TransactionRepositoryImpl {
        val engine = MockEngine { request ->
            val payload = request.body.toByteArray().decodeToString()
            requests += "${request.method.value} ${request.url.encodedPathAndQuery} $payload".trim()
            respond(body, status, headersOf(HttpHeaders.ContentType, "application/json"))
        }
        val http = createHttpClient(engine, ApiConfig("http://test", enableNetworkInspector = false), NoToken)
        return TransactionRepositoryImpl(ApiClient(http, NoToken, SessionEvents()))
    }

    @Test
    fun listParsesDatesAndOmitsMissingCategory() = runTest {
        val repo = repository(
            HttpStatusCode.OK,
            """{"content":[{"id":1,"categoryId":2,"amount":25000,"date":"2026-11-10","note":"Nasi goreng"}],"page":0,"size":20,"totalElements":1}""",
        )

        val result = repo.list("2026-11", null, 0, 20)

        assertEquals(
            ResultState.Success(
                TransactionPage(listOf(Transaction(1, 2, 25_000, LocalDate(2026, 11, 10), "Nasi goreng")), 0, 20, 1),
            ),
            result,
        )
        assertEquals("GET /api/transactions?month=2026-11&page=0&size=20", requests.single())
    }

    @Test
    fun listSendsCategoryFilter() = runTest {
        repository(HttpStatusCode.OK, """{"content":[],"page":1,"size":20,"totalElements":0}""").list("2026-11", 3, 1, 20)

        assertEquals("GET /api/transactions?month=2026-11&categoryId=3&page=1&size=20", requests.single())
    }

    @Test
    fun createSerialisesTheDate() = runTest {
        repository(HttpStatusCode.Created, """{"id":9,"categoryId":2,"amount":150000,"date":"2026-11-07"}""")
            .create(TransactionInput(2, 150_000, LocalDate(2026, 11, 7), null))

        assertEquals("""POST /api/transactions {"categoryId":2,"amount":150000,"date":"2026-11-07"}""", requests.single())
    }

    @Test
    fun hasMoreFollowsTotal() {
        assertTrue(TransactionPage(emptyList(), 0, 20, 25).hasMore)
        assertFalse(TransactionPage(emptyList(), 1, 20, 25).hasMore)
        assertFalse(TransactionPage(emptyList(), 0, 20, 20).hasMore)
    }
}
