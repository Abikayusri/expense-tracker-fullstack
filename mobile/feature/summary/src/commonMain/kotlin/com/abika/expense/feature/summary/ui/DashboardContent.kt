package com.abika.expense.feature.summary.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abika.expense.core.sharedui.ErrorBanner
import com.abika.expense.core.sharedui.MonthHeader
import com.abika.expense.feature.summary.BudgetText
import com.abika.expense.feature.summary.DashboardAction
import com.abika.expense.feature.summary.DashboardState
import com.abika.expense.feature.summary.ui.component.BudgetSection
import com.abika.expense.feature.summary.ui.component.OverBudgetBanner
import com.abika.expense.feature.summary.ui.component.TotalsSection

@Composable
fun DashboardContent(
    state: DashboardState,
    onAction: (DashboardAction) -> Unit,
    onOpenCategories: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 96.dp),
    ) {
        MonthHeader(month = state.month, onShift = { onAction(DashboardAction.ChangeMonth(it)) })
        if (state.isLoading) LinearProgressIndicator(modifier = Modifier.padding(horizontal = 16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(horizontal = 16.dp)) {
            ErrorBanner(state.errorMessage)
            val summary = state.summary ?: return@Column
            TotalsSection(summary)
            OverBudgetBanner(BudgetText.overBudgetLines(summary))
            if (summary.perCategory.isEmpty()) {
                Text("Belum ada kategori.")
                TextButton(onClick = onOpenCategories) { Text("Buat kategori dulu") }
            }
            BudgetSection(BudgetText.budgets(summary))
        }
    }
}
