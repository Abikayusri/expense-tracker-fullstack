package com.abika.expense.domain.auth

/** The platform's push token, or null when push is unavailable (iOS, or Android without Firebase). */
fun interface PushTokenProvider {
    suspend fun currentToken(): String?
}
