package com.abika.expense.feature.auth.register.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abika.expense.feature.auth.register.RegisterEvent
import com.abika.expense.feature.auth.register.RegisterViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterScreen(
    onRegistered: () -> Unit,
    onOpenLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                RegisterEvent.Registered -> onRegistered()
            }
        }
    }
    RegisterContent(state = state, onAction = viewModel::onAction, onOpenLogin = onOpenLogin, modifier = modifier)
}
