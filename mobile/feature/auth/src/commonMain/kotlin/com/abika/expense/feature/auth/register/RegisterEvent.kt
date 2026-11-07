package com.abika.expense.feature.auth.register

sealed interface RegisterEvent {
    data object Registered : RegisterEvent
}
