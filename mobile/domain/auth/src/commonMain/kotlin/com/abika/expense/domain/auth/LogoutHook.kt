package com.abika.expense.domain.auth

/** Work that must happen while the token is still valid, e.g. unregistering this device from push. */
fun interface LogoutHook {
    suspend fun beforeLogout()
}
