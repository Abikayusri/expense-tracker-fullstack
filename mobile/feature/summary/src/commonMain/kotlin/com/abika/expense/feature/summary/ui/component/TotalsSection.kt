package com.abika.expense.feature.summary.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abika.expense.core.common.formatRupiah
import com.abika.expense.core.designsystem.ExpenseColors
import com.abika.expense.domain.summary.Summary

@Composable
fun TotalsSection(summary: Summary, modifier: Modifier = Modifier) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TotalCard("Pemasukan", summary.totalIncome, ExpenseColors.Income, Modifier.weight(1f))
            TotalCard("Pengeluaran", summary.totalExpense, ExpenseColors.Expense, Modifier.weight(1f))
        }
        TotalCard(
            label = "Saldo",
            amount = summary.balance,
            color = if (summary.balance < 0) ExpenseColors.Expense else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun TotalCard(label: String, amount: Long, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(formatRupiah(amount), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = color)
        }
    }
}
