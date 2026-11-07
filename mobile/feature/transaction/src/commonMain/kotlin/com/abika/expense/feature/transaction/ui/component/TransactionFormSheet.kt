package com.abika.expense.feature.transaction.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.abika.expense.core.common.DateText
import com.abika.expense.core.designsystem.ExpenseColors
import com.abika.expense.core.sharedui.ErrorBanner
import com.abika.expense.core.sharedui.ExpenseTextField
import com.abika.expense.core.sharedui.PrimaryButton
import com.abika.expense.domain.category.Category
import com.abika.expense.feature.transaction.TransactionFormState
import com.abika.expense.feature.transaction.TransactionsAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFormSheet(form: TransactionFormState, categories: List<Category>, onAction: (TransactionsAction) -> Unit) {
    var pickingDate by remember { mutableStateOf(false) }
    ModalBottomSheet(
        onDismissRequest = { onAction(TransactionsAction.DismissForm) },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 24.dp).imePadding(),
        ) {
            Text(if (form.isEditing) "Ubah transaksi" else "Transaksi baru", style = MaterialTheme.typography.titleLarge)
            ErrorBanner(form.errorMessage)
            Text("Kategori", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                categories.forEach { category ->
                    FilterChip(
                        selected = form.categoryId == category.id,
                        onClick = { onAction(TransactionsAction.CategoryChanged(category.id)) },
                        label = { Text(category.name) },
                    )
                }
            }
            form.fieldErrors["categoryId"]?.let { Text(it, color = ExpenseColors.Expense, style = MaterialTheme.typography.bodySmall) }
            ExpenseTextField(
                value = form.amountText,
                onValueChange = { onAction(TransactionsAction.AmountChanged(it)) },
                label = "Jumlah (Rp)",
                error = form.fieldErrors["amount"],
                keyboardType = KeyboardType.Number,
            )
            OutlinedButton(onClick = { pickingDate = true }) { Text("Tanggal: ${DateText.short(form.date)}") }
            ExpenseTextField(
                value = form.note,
                onValueChange = { onAction(TransactionsAction.NoteChanged(it)) },
                label = "Catatan (opsional)",
                error = form.fieldErrors["note"],
                imeAction = ImeAction.Done,
            )
            PrimaryButton(text = "Simpan", onClick = { onAction(TransactionsAction.SubmitForm) }, loading = form.isSubmitting)
        }
    }
    if (pickingDate) {
        TransactionDatePicker(
            initial = form.date,
            onPicked = {
                onAction(TransactionsAction.DateChanged(it))
                pickingDate = false
            },
            onDismiss = { pickingDate = false },
        )
    }
}
