package com.abika.expense.feature.transaction.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.abika.expense.core.common.DateText
import com.abika.expense.core.common.formatRupiah
import com.abika.expense.core.designsystem.ExpenseColors
import com.abika.expense.domain.transaction.Transaction
import com.abika.expense.feature.transaction.TransactionsAction

@Composable
fun DeleteTransactionDialog(transaction: Transaction, onAction: (TransactionsAction) -> Unit) {
    AlertDialog(
        onDismissRequest = { onAction(TransactionsAction.CancelDelete) },
        title = { Text("Hapus transaksi?") },
        text = { Text("${formatRupiah(transaction.amount)} pada ${DateText.short(transaction.date)} akan dihapus.") },
        confirmButton = {
            TextButton(onClick = { onAction(TransactionsAction.ConfirmDelete) }) { Text("Hapus", color = ExpenseColors.Expense) }
        },
        dismissButton = { TextButton(onClick = { onAction(TransactionsAction.CancelDelete) }) { Text("Batal") } },
    )
}
