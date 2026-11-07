package com.abika.expense.data.summary

import com.abika.expense.core.common.ResultState
import com.abika.expense.core.datastore.TokenStore
import com.abika.expense.core.network.ApiClient
import com.abika.expense.core.network.ApiConfig
import com.abika.expense.core.network.SessionEvents
import com.abika.expense.core.network.createHttpClient
import com.abika.expense.domain.category.CategoryType
import com.abika.expense.domain.summary.CategorySummary
import com.abika.expense.domain.summary.Summary
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SummaryRepositoryImplTest {

    private object NoToken : TokenStore {
        override val token = MutableStateFlow<String?>(null)
        override suspend fun current(): String? = null
        override suspend fun save(token: String) = Unit
        override suspend fun clear() = Unit
    }

    @Test
    fun mapsTheSummaryAndSendsTheMonth() = runTest {
        val urls = mutableListOf<String>()
        val engine = MockEngine { request ->
            urls += request.url.encodedPathAndQuery
            respond(
                """{"totalIncome":0,"totalExpense":150000,"balance":-150000,"perCategory":[
                   {"categoryId":1,"name":"Makan","type":"EXPENSE","spent":150000,"budget":100000,"overBudget":true},
                   {"categoryId":2,"name":"Gaji","type":"INCOME","spent":0,"budget":null,"overBudget":false}]}""",
                HttpStatusCode.OK,
                headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val http = createHttpClient(engine, ApiConfig("http://test", enableNetworkInspector = false), NoToken)

        val result = SummaryRepositoryImpl(ApiClient(http, NoToken, SessionEvents())).get("2026-11")

        assertEquals(
            ResultState.Success(
                Summary(
                    0, 150_000, -150_000,
                    listOf(
                        CategorySummary(1, "Makan", CategoryType.EXPENSE, 150_000, 100_000, true),
                        CategorySummary(2, "Gaji", CategoryType.INCOME, 0, null, false),
                    ),
                ),
            ),
            result,
        )
        assertEquals(listOf("/api/summary?month=2026-11"), urls)
    }
}
