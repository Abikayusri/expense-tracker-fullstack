package com.abika.expense.domain.auth

import com.abika.expense.core.common.ResultState

/** Tells the backend which push token belongs to the logged-in user. */
interface DeviceRegistry {
    suspend fun register(fcmToken: String): ResultState<Unit>

    suspend fun unregister(fcmToken: String): ResultState<Unit>
}
