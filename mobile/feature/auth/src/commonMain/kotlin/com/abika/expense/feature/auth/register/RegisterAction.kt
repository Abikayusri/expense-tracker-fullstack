package com.abika.expense.feature.auth.register

sealed interface RegisterAction {
    data class NameChanged(val value: String) : RegisterAction
    data class EmailChanged(val value: String) : RegisterAction
    data class PasswordChanged(val value: String) : RegisterAction
    data object Submit : RegisterAction
}
