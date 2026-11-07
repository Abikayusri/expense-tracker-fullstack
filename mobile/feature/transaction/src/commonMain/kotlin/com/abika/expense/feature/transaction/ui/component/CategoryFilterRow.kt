package com.abika.expense.feature.transaction.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abika.expense.domain.category.Category

@Composable
fun CategoryFilterRow(categories: List<Category>, selected: Long?, onSelect: (Long?) -> Unit, modifier: Modifier = Modifier) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = modifier,
    ) {
        item { FilterChip(selected = selected == null, onClick = { onSelect(null) }, label = { Text("Semua") }) }
        items(categories, key = { it.id }) { category ->
            FilterChip(
                selected = selected == category.id,
                onClick = { onSelect(category.id) },
                label = { Text(category.name) },
            )
        }
    }
}
