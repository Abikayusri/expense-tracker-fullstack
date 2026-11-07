package com.abika.expense.core.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** MVI base: [S] screen state, [E] one-off events (navigation, snackbars), [A] user actions. */
abstract class BaseViewModel<S, E, A>(initialState: S) : ViewModel() {

    private val mutableState = MutableStateFlow(initialState)
    val stateFlow: StateFlow<S> = mutableState.asStateFlow()

    private val events = Channel<E>(Channel.BUFFERED)
    val eventFlow: Flow<E> = events.receiveAsFlow()

    protected val state: S
        get() = mutableState.value

    abstract fun onAction(action: A)

    protected fun updateState(transform: (S) -> S) {
        mutableState.update(transform)
    }

    protected fun sendEvent(event: E) {
        viewModelScope.launch { events.send(event) }
    }
}
