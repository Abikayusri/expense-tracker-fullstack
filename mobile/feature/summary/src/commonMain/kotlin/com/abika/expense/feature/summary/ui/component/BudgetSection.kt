package com.abika.expense.feature.summary.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.abika.expense.core.common.formatRupiah
import com.abika.expense.core.designsystem.ExpenseColors
import com.abika.expense.domain.summary.CategorySummary
import com.abika.expense.feature.summary.BudgetText
import kotlin.math.roundToInt

@Composable
fun BudgetSection(items: List<CategorySummary>, modifier: Modifier = Modifier) {
    if (items.isEmpty()) return
    Card(modifier = modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(16.dp)) {
            Text("Budget bulan ini", style = MaterialTheme.typography.titleMedium)
            items.forEach { BudgetRow(it) }
        }
    }
}

@Composable
private fun BudgetRow(item: CategorySummary) {
    val progress = BudgetText.progress(item)
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row {
            Text(item.name, modifier = Modifier.weight(1f))
            Text(
                "${formatRupiah(item.spent)} / ${formatRupiah(item.budget ?: 0)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        LinearProgressIndicator(
            progress = { progress },
            color = if (item.overBudget) ExpenseColors.Expense else ExpenseColors.Primary,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Budget ${item.name} terpakai ${(progress * 100).roundToInt()} persen" },
        )
    }
}
