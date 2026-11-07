package com.abika.expense.feature.transaction.ui.component

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

// The Material date picker works in UTC milliseconds, so conversions use UTC to keep the same calendar day.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDatePicker(initial: LocalDate, onPicked: (LocalDate) -> Unit, onDismiss: () -> Unit) {
    val state = rememberDatePickerState(initialSelectedDateMillis = initial.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds())
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                state.selectedDateMillis?.let {
                    onPicked(Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.UTC).date)
                } ?: onDismiss()
            }) { Text("Pilih") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } },
    ) {
        DatePicker(state = state)
    }
}
