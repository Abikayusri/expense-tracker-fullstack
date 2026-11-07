package com.abika.expense.feature.category.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abika.expense.core.sharedui.ErrorBanner
import com.abika.expense.feature.category.CategoriesAction
import com.abika.expense.feature.category.CategoriesState
import com.abika.expense.feature.category.ui.component.CategoryFormSheet
import com.abika.expense.feature.category.ui.component.DeleteCategoryDialog
import com.abika.expense.feature.category.ui.component.categorySection

@Composable
fun CategoriesContent(state: CategoriesState, onAction: (CategoriesAction) -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        if (state.isLoading && state.expenses.isEmpty() && state.incomes.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp)) {
                item { ErrorBanner(state.errorMessage, modifier = Modifier.padding(bottom = 8.dp)) }
                categorySection(
                    title = "Pengeluaran",
                    items = state.expenses,
                    onEdit = { onAction(CategoriesAction.OpenEdit(it)) },
                    onDelete = { onAction(CategoriesAction.RequestDelete(it)) },
                )
                categorySection(
                    title = "Pemasukan",
                    items = state.incomes,
                    onEdit = { onAction(CategoriesAction.OpenEdit(it)) },
                    onDelete = { onAction(CategoriesAction.RequestDelete(it)) },
                )
            }
        }
        ExtendedFloatingActionButton(
            onClick = { onAction(CategoriesAction.OpenCreate) },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
        ) {
            Text("Tambah kategori")
        }
    }
    state.form?.let { form -> CategoryFormSheet(form = form, onAction = onAction) }
    state.pendingDelete?.let { category -> DeleteCategoryDialog(category = category, onAction = onAction) }
}
