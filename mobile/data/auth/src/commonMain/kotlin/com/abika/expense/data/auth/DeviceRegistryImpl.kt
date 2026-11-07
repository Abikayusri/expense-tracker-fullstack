package com.abika.expense.data.auth

import com.abika.expense.core.common.ResultState
import com.abika.expense.core.network.ApiClient
import com.abika.expense.data.auth.dto.DeviceRequestDto
import com.abika.expense.domain.auth.DeviceRegistry
import io.ktor.http.encodeURLPathPart

internal class DeviceRegistryImpl(private val api: ApiClient) : DeviceRegistry {

    override suspend fun register(fcmToken: String): ResultState<Unit> =
        api.post<DeviceRequestDto, Unit>("/api/devices", DeviceRequestDto(fcmToken, PLATFORM))

    // FCM tokens contain ':' and other characters, so the path segment is encoded.
    override suspend fun unregister(fcmToken: String): ResultState<Unit> =
        api.delete("/api/devices/${fcmToken.encodeURLPathPart()}")

    private companion object {
        const val PLATFORM = "ANDROID"
    }
}
