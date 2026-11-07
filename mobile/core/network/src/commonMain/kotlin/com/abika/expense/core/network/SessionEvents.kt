package com.abika.expense.core.network

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/** Lets the navigation layer react when the backend rejects the stored token. */
class SessionEvents {
    private val unauthorizedEvents = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val unauthorized: SharedFlow<Unit> = unauthorizedEvents.asSharedFlow()

    fun notifyUnauthorized() {
        unauthorizedEvents.tryEmit(Unit)
    }
}
