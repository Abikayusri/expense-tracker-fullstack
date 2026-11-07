package com.abika.expense.feature.auth.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.abika.expense.feature.auth.login.ui.LoginScreen
import com.abika.expense.feature.auth.register.ui.RegisterScreen

fun EntryProviderScope<Any>.authEntries(
    onLoggedIn: () -> Unit,
    onOpenRegister: () -> Unit,
    onOpenLogin: () -> Unit,
) {
    entry<LoginRoute> {
        LoginScreen(onLoggedIn = onLoggedIn, onOpenRegister = onOpenRegister)
    }
    entry<RegisterRoute> {
        RegisterScreen(onRegistered = onLoggedIn, onOpenLogin = onOpenLogin)
    }
}
