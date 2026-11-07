package com.abika.expense.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.abika.expense.core.network.SessionEvents
import com.abika.expense.domain.auth.AuthRepository
import com.abika.expense.feature.auth.navigation.LoginRoute
import com.abika.expense.feature.auth.navigation.RegisterRoute
import com.abika.expense.feature.auth.navigation.authEntries
import com.abika.expense.home.HomeRoute
import com.abika.expense.home.HomeScreen
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun AppNavigation() {
    val authRepository = koinInject<AuthRepository>()
    val sessionEvents = koinInject<SessionEvents>()
    val scope = rememberCoroutineScope()
    val isLoggedIn by authRepository.isLoggedIn.collectAsStateWithLifecycle(initialValue = null)
    // shortcut: an in-memory back stack, so Android process death returns to the start screen; switch to a saveable back stack if that matters.
    val backStack = remember { mutableStateListOf<Any>() }

    fun resetTo(route: Any) {
        backStack.clear()
        backStack.add(route)
    }

    LaunchedEffect(isLoggedIn) {
        val loggedIn = isLoggedIn ?: return@LaunchedEffect
        if (backStack.isEmpty()) resetTo(if (loggedIn) HomeRoute else LoginRoute)
    }
    LaunchedEffect(sessionEvents) {
        sessionEvents.unauthorized.collect { resetTo(LoginRoute) }
    }

    if (backStack.isEmpty()) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) { CircularProgressIndicator() }
        return
    }

    NavDisplay(
        backStack = backStack,
        onBack = { if (backStack.size > 1) backStack.removeLast() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            authEntries(
                onLoggedIn = { resetTo(HomeRoute) },
                onOpenRegister = { backStack.add(RegisterRoute) },
                onOpenLogin = { resetTo(LoginRoute) },
            )
            entry<HomeRoute> {
                HomeScreen(onLogout = {
                    scope.launch {
                        authRepository.logout()
                        resetTo(LoginRoute)
                    }
                })
            }
        },
    )
}
