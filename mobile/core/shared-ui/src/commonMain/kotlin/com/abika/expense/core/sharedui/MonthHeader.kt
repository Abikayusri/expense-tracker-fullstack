package com.abika.expense.core.sharedui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.abika.expense.core.common.MonthText

@Composable
fun MonthHeader(month: String, onShift: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth().padding(horizontal = 8.dp),
    ) {
        TextButton(onClick = { onShift(-1) }, modifier = Modifier.semantics { contentDescription = "Bulan sebelumnya" }) {
            Text("‹", style = MaterialTheme.typography.titleLarge)
        }
        Text(MonthText.label(month), style = MaterialTheme.typography.titleMedium)
        TextButton(onClick = { onShift(1) }, modifier = Modifier.semantics { contentDescription = "Bulan berikutnya" }) {
            Text("›", style = MaterialTheme.typography.titleLarge)
        }
    }
}
