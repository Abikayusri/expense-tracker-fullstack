package com.abika.expense.feature.transaction.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abika.expense.feature.transaction.TransactionsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TransactionsScreen(modifier: Modifier = Modifier, viewModel: TransactionsViewModel = koinViewModel()) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    TransactionsContent(state = state, onAction = viewModel::onAction, modifier = modifier)
}
