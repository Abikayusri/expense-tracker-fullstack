package com.abika.expense.core.datastore

import kotlinx.coroutines.flow.Flow

/** Where the JWT lives between app launches. */
interface TokenStore {
    val token: Flow<String?>

    suspend fun current(): String?

    suspend fun save(token: String)

    suspend fun clear()
}
