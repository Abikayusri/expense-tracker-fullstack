package com.abika.expense.data.auth

import com.abika.expense.domain.auth.DeviceRegistry
import com.abika.expense.domain.auth.LoginHook
import com.abika.expense.domain.auth.LogoutHook
import com.abika.expense.domain.auth.PushTokenProvider
import kotlinx.coroutines.CancellationException

/** Registers this device after login and removes it before logout, so pushes reach only the current account. */
internal class PushDeviceHooks(
    private val tokens: PushTokenProvider,
    private val registry: DeviceRegistry,
) : LoginHook, LogoutHook {

    override suspend fun afterLogin() {
        currentTokenOrNull()?.let { registry.register(it) }
    }

    override suspend fun beforeLogout() {
        currentTokenOrNull()?.let { registry.unregister(it) }
    }

    // Push is optional: a missing or broken Firebase setup must never block login or logout.
    private suspend fun currentTokenOrNull(): String? = try {
        tokens.currentToken()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        null
    }
}
