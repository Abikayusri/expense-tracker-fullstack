package com.abika.expense.domain.auth

/** Work that needs the new session, e.g. registering this device for push. Failures must not block login. */
fun interface LoginHook {
    suspend fun afterLogin()
}
