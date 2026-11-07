package com.abika.expense.feature.category.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.abika.expense.domain.category.Category

fun LazyListScope.categorySection(
    title: String,
    items: List<Category>,
    onEdit: (Category) -> Unit,
    onDelete: (Category) -> Unit,
) {
    item(key = "header-$title") {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp).semantics { heading() },
        )
    }
    if (items.isEmpty()) {
        item(key = "empty-$title") {
            Text("Belum ada kategori.", style = MaterialTheme.typography.bodyMedium)
        }
    }
    items(items, key = { it.id }) { category ->
        CategoryRow(category = category, onEdit = { onEdit(category) }, onDelete = { onDelete(category) })
    }
}
