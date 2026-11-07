package com.abika.expense.feature.category.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.abika.expense.core.designsystem.ExpenseColors
import com.abika.expense.domain.category.Category
import com.abika.expense.feature.category.CategoriesAction

@Composable
fun DeleteCategoryDialog(category: Category, onAction: (CategoriesAction) -> Unit) {
    AlertDialog(
        onDismissRequest = { onAction(CategoriesAction.CancelDelete) },
        title = { Text("Hapus kategori?") },
        text = { Text("Kategori ${category.name} akan dihapus.") },
        confirmButton = {
            TextButton(onClick = { onAction(CategoriesAction.ConfirmDelete) }) { Text("Hapus", color = ExpenseColors.Expense) }
        },
        dismissButton = {
            TextButton(onClick = { onAction(CategoriesAction.CancelDelete) }) { Text("Batal") }
        },
    )
}
