package com.abika.expense.feature.summary.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abika.expense.feature.summary.DashboardAction
import com.abika.expense.feature.summary.DashboardViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DashboardScreen(
    onOpenCategories: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    // Each visit to the tab reloads, so changes made in the other tabs show up.
    LaunchedEffect(Unit) { viewModel.onAction(DashboardAction.Refresh) }
    DashboardContent(state = state, onAction = viewModel::onAction, onOpenCategories = onOpenCategories, modifier = modifier)
}
