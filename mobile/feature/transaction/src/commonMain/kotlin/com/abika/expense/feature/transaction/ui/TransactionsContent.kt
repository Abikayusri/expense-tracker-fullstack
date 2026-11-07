package com.abika.expense.feature.transaction.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abika.expense.core.sharedui.ErrorBanner
import com.abika.expense.core.sharedui.MonthHeader
import com.abika.expense.feature.transaction.TransactionsAction
import com.abika.expense.feature.transaction.TransactionsState
import com.abika.expense.feature.transaction.ui.component.CategoryFilterRow
import com.abika.expense.feature.transaction.ui.component.DeleteTransactionDialog
import com.abika.expense.feature.transaction.ui.component.TransactionFormSheet
import com.abika.expense.feature.transaction.ui.component.TransactionList

@Composable
fun TransactionsContent(state: TransactionsState, onAction: (TransactionsAction) -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            MonthHeader(month = state.month, onShift = { onAction(TransactionsAction.ChangeMonth(it)) })
            CategoryFilterRow(
                categories = state.categories,
                selected = state.categoryFilter,
                onSelect = { onAction(TransactionsAction.ChangeFilter(it)) },
            )
            ErrorBanner(state.errorMessage, modifier = Modifier.padding(horizontal = 16.dp))
            TransactionList(state = state, onAction = onAction, modifier = Modifier.weight(1f))
        }
        ExtendedFloatingActionButton(
            onClick = { onAction(TransactionsAction.OpenCreate) },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
        ) {
            Text("Tambah transaksi")
        }
    }
    state.form?.let { TransactionFormSheet(form = it, categories = state.categories, onAction = onAction) }
    state.pendingDelete?.let { DeleteTransactionDialog(transaction = it, onAction = onAction) }
}
