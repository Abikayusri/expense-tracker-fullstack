package com.abika.expense.feature.category.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abika.expense.feature.category.CategoriesViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CategoriesScreen(modifier: Modifier = Modifier, viewModel: CategoriesViewModel = koinViewModel()) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    CategoriesContent(state = state, onAction = viewModel::onAction, modifier = modifier)
}
