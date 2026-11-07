package com.abika.expense.data.category

import com.abika.expense.core.common.AppError
import com.abika.expense.core.common.ResultState
import com.abika.expense.core.datastore.TokenStore
import com.abika.expense.core.network.ApiClient
import com.abika.expense.core.network.ApiConfig
import com.abika.expense.core.network.SessionEvents
import com.abika.expense.core.network.createHttpClient
import com.abika.expense.domain.category.Category
import com.abika.expense.domain.category.CategoryType
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

class CategoryRepositoryImplTest {

    private object NoToken : TokenStore {
        override val token = MutableStateFlow<String?>(null)
        override suspend fun current(): String? = null
        override suspend fun save(token: String) = Unit
        override suspend fun clear() = Unit
    }

    private val requests = mutableListOf<String>()

    private fun repository(status: HttpStatusCode, body: String): CategoryRepositoryImpl {
        val engine = MockEngine { request ->
            val payload = request.body.toByteArray().decodeToString()
            requests += "${request.method.value} ${request.url.encodedPathAndQuery} $payload".trim()
            respond(body, status, headersOf(HttpHeaders.ContentType, "application/json"))
        }
        val http = createHttpClient(engine, ApiConfig("http://test", enableNetworkInspector = false), NoToken)
        return CategoryRepositoryImpl(ApiClient(http, NoToken, SessionEvents()))
    }

    @Test
    fun listMapsTypesAndFiltersByType() = runTest {
        val repo = repository(
            HttpStatusCode.OK,
            """[{"id":1,"name":"Makan","type":"EXPENSE","monthlyBudget":1000000},{"id":2,"name":"Gaji","type":"INCOME","monthlyBudget":null}]""",
        )

        val result = repo.list(CategoryType.EXPENSE)

        assertEquals(
            ResultState.Success(
                listOf(Category(1, "Makan", CategoryType.EXPENSE, 1_000_000), Category(2, "Gaji", CategoryType.INCOME, null)),
            ),
            result,
        )
        assertEquals("GET /api/categories?type=EXPENSE", requests.single())
    }

    @Test
    fun listWithoutFilterHasNoQuery() = runTest {
        repository(HttpStatusCode.OK, "[]").list()

        assertEquals("GET /api/categories", requests.single())
    }

    @Test
    fun createSendsTypeAndTrimmedName() = runTest {
        repository(HttpStatusCode.Created, """{"id":3,"name":"Hobi","type":"EXPENSE","monthlyBudget":500000}""")
            .create("  Hobi ", CategoryType.EXPENSE, 500_000)

        assertEquals("""POST /api/categories {"name":"Hobi","type":"EXPENSE","monthlyBudget":500000}""", requests.single())
    }

    @Test
    fun updateNeverSendsType() = runTest {
        repository(HttpStatusCode.OK, """{"id":3,"name":"Jajan","type":"EXPENSE"}""").update(3, "Jajan", null)

        assertEquals("""PUT /api/categories/3 {"name":"Jajan"}""", requests.single())
    }

    @Test
    fun deleteConflictKeepsTheBackendMessage() = runTest {
        val result = repository(HttpStatusCode.Conflict, """{"detail":"Kategori masih dipakai transaksi"}""").delete(3)

        assertEquals(ResultState.Error(AppError(409, "Kategori masih dipakai transaksi")), result)
    }
}
