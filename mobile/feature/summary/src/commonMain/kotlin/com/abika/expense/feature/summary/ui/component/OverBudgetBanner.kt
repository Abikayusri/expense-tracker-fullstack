package com.abika.expense.feature.summary.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abika.expense.core.designsystem.ExpenseColors

@Composable
fun OverBudgetBanner(lines: List<String>, modifier: Modifier = Modifier) {
    if (lines.isEmpty()) return
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .fillMaxWidth()
            .background(ExpenseColors.OverBudgetBackground, RoundedCornerShape(12.dp))
            .padding(16.dp)
            .semantics { liveRegion = LiveRegionMode.Polite },
    ) {
        Text("Budget terlampaui", color = ExpenseColors.Expense, fontWeight = FontWeight.SemiBold)
        lines.forEach { Text(it, color = ExpenseColors.Expense, style = MaterialTheme.typography.bodyMedium) }
    }
}
