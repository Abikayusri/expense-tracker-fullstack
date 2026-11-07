package com.abika.expense.core.common

/** A failed call as the UI needs it: HTTP status (0 = no connection), a message, and per-field messages. */
data class AppError(
    val status: Int,
    val message: String,
    val fieldErrors: Map<String, String> = emptyMap(),
)
