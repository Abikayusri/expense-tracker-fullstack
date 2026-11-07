package com.abika.expense.feature.category.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.abika.expense.core.sharedui.ErrorBanner
import com.abika.expense.core.sharedui.ExpenseTextField
import com.abika.expense.core.sharedui.PrimaryButton
import com.abika.expense.domain.category.CategoryType
import com.abika.expense.feature.category.CategoriesAction
import com.abika.expense.feature.category.CategoryFormState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFormSheet(form: CategoryFormState, onAction: (CategoriesAction) -> Unit) {
    ModalBottomSheet(
        onDismissRequest = { onAction(CategoriesAction.DismissForm) },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 24.dp).imePadding(),
        ) {
            Text(if (form.isEditing) "Ubah kategori" else "Kategori baru", style = MaterialTheme.typography.titleLarge)
            ErrorBanner(form.errorMessage)
            ExpenseTextField(
                value = form.name,
                onValueChange = { onAction(CategoriesAction.NameChanged(it)) },
                label = "Nama",
                error = form.fieldErrors["name"],
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TypeChip("Pengeluaran", CategoryType.EXPENSE, form, onAction)
                TypeChip("Pemasukan", CategoryType.INCOME, form, onAction)
            }
            if (form.type == CategoryType.EXPENSE) {
                ExpenseTextField(
                    value = form.budgetText,
                    onValueChange = { onAction(CategoriesAction.BudgetChanged(it)) },
                    label = "Budget bulanan (kosongkan jika tidak ada)",
                    error = form.fieldErrors["monthlyBudget"],
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                )
            }
            PrimaryButton(text = "Simpan", onClick = { onAction(CategoriesAction.SubmitForm) }, loading = form.isSubmitting)
        }
    }
}

@Composable
private fun TypeChip(label: String, type: CategoryType, form: CategoryFormState, onAction: (CategoriesAction) -> Unit) {
    FilterChip(
        selected = form.type == type,
        onClick = { onAction(CategoriesAction.TypeChanged(type)) },
        enabled = !form.isEditing,
        label = { Text(label) },
    )
}
