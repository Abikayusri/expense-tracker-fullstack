package com.abika.expense.core.network

import com.abika.expense.core.datastore.TokenStore
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import ro.cosminmihu.ktor.monitor.KtorMonitorLogging

internal val ApiJson = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
}

fun createHttpClient(engine: HttpClientEngine, config: ApiConfig, tokenStore: TokenStore): HttpClient =
    HttpClient(engine) {
        expectSuccess = false
        install(ContentNegotiation) { json(ApiJson) }
        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 10_000
        }
        defaultRequest { url(config.baseUrl) }
        install(createClientPlugin("BearerToken") {
            onRequest { request, _ ->
                tokenStore.current()?.let { request.headers.append(HttpHeaders.Authorization, "Bearer $it") }
            }
        })
        install(KtorMonitorLogging) {
            isActive = config.enableNetworkInspector
            // The in-app "API" button opens the inspector; a notification would make iOS ask for permission at login.
            showNotification = false
            sanitizeHeader { header -> header == HttpHeaders.Authorization }
        }
    }
