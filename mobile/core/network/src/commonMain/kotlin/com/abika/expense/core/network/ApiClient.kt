package com.abika.expense.core.network

import com.abika.expense.core.common.AppError
import com.abika.expense.core.common.ResultState
import com.abika.expense.core.datastore.TokenStore
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException

/** Every backend call goes through here, so errors and expired sessions are handled the same way everywhere. */
class ApiClient(
    @PublishedApi internal val http: HttpClient,
    private val tokenStore: TokenStore,
    private val sessionEvents: SessionEvents,
) {

    suspend inline fun <reified T> get(path: String, query: Map<String, Any?> = emptyMap()): ResultState<T> =
        call(HttpMethod.Get, path) {
            query.forEach { (key, value) -> if (value != null) parameter(key, value) }
        }

    suspend inline fun <reified B : Any, reified T> post(path: String, body: B): ResultState<T> =
        call(HttpMethod.Post, path) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }

    suspend inline fun <reified B : Any, reified T> put(path: String, body: B): ResultState<T> =
        call(HttpMethod.Put, path) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }

    suspend fun delete(path: String): ResultState<Unit> = call(HttpMethod.Delete, path) {}

    suspend inline fun <reified T> call(
        method: HttpMethod,
        path: String,
        crossinline block: HttpRequestBuilder.() -> Unit,
    ): ResultState<T> {
        val response = try {
            http.request {
                this.method = method
                url(path)
                block()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return ResultState.Error(NETWORK_ERROR)
        }
        if (!response.status.isSuccess()) {
            return ResultState.Error(toError(path, response))
        }
        if (T::class == Unit::class) {
            @Suppress("UNCHECKED_CAST")
            return ResultState.Success(Unit as T)
        }
        return try {
            ResultState.Success(response.body<T>())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            ResultState.Error(AppError(response.status.value, "Respons server tidak dikenali"))
        }
    }

    @PublishedApi
    internal suspend fun toError(path: String, response: HttpResponse): AppError {
        if (response.status == HttpStatusCode.Unauthorized && !path.startsWith("/api/auth/")) {
            tokenStore.clear()
            sessionEvents.notifyUnauthorized()
        }
        val problem = try {
            ApiJson.decodeFromString(ProblemBody.serializer(), response.bodyAsText())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }
        return AppError(
            status = response.status.value,
            message = problem?.detail ?: "Terjadi kesalahan (${response.status.value})",
            fieldErrors = problem?.errors.orEmpty(),
        )
    }

    companion object {
        val NETWORK_ERROR = AppError(0, "Tidak bisa terhubung ke server")
    }
}
