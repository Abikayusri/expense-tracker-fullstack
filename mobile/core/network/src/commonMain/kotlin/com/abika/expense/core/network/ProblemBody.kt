package com.abika.expense.core.network

import kotlinx.serialization.Serializable

/** The parts of the backend's RFC 7807 ProblemDetail the app shows to users. */
@Serializable
internal data class ProblemBody(
    val detail: String? = null,
    val errors: Map<String, String>? = null,
)
