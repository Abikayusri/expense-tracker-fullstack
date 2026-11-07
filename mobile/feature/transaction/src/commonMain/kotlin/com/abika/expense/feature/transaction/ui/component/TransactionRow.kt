package com.abika.expense.feature.transaction.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abika.expense.core.common.DateText
import com.abika.expense.core.common.formatRupiah
import com.abika.expense.core.designsystem.ExpenseColors
import com.abika.expense.domain.category.Category
import com.abika.expense.domain.category.CategoryType
import com.abika.expense.domain.transaction.Transaction

@Composable
fun TransactionRow(
    transaction: Transaction,
    category: Category?,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val income = category?.type == CategoryType.INCOME
    Column(modifier = modifier.fillMaxWidth().clickable(onClick = onEdit)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(category?.name ?: "-", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = listOfNotNull(DateText.short(transaction.date), transaction.note).joinToString(" · "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = formatRupiah(if (income) transaction.amount else -transaction.amount),
                color = if (income) ExpenseColors.Income else ExpenseColors.Expense,
                fontWeight = FontWeight.SemiBold,
            )
            TextButton(
                onClick = onDelete,
                modifier = Modifier.semantics { contentDescription = "Hapus transaksi ${category?.name.orEmpty()}" },
            ) {
                Text("Hapus", color = ExpenseColors.Expense)
            }
        }
        HorizontalDivider()
    }
}
