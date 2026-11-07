package com.abika.expense.feature.auth.login

sealed interface LoginEvent {
    data object LoggedIn : LoginEvent
}
