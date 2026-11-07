package com.abika.expense.feature.category.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.abika.expense.core.common.formatRupiah
import com.abika.expense.core.designsystem.ExpenseColors
import com.abika.expense.domain.category.Category
import com.abika.expense.domain.category.CategoryType

@Composable
fun CategoryRow(category: Category, onEdit: () -> Unit, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.weight(1f)) {
            Text(category.name, style = MaterialTheme.typography.titleMedium)
            if (category.type == CategoryType.EXPENSE) {
                Text(
                    text = category.monthlyBudget?.let { "Budget ${formatRupiah(it)}" } ?: "Tanpa budget",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        TextButton(onClick = onEdit, modifier = Modifier.semantics { contentDescription = "Ubah ${category.name}" }) {
            Text("Ubah")
        }
        TextButton(onClick = onDelete, modifier = Modifier.semantics { contentDescription = "Hapus ${category.name}" }) {
            Text("Hapus", color = ExpenseColors.Expense)
        }
    }
}
