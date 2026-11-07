package com.abika.expense.feature.transaction.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abika.expense.feature.transaction.TransactionsAction
import com.abika.expense.feature.transaction.TransactionsState

@Composable
fun TransactionList(state: TransactionsState, onAction: (TransactionsAction) -> Unit, modifier: Modifier = Modifier) {
    val listState = rememberLazyListState()
    val nearEnd by remember {
        derivedStateOf {
            val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            last >= listState.layoutInfo.totalItemsCount - 3
        }
    }
    LaunchedEffect(nearEnd, state.items.size) {
        if (nearEnd) onAction(TransactionsAction.LoadMore)
    }
    LaunchedEffect(state.month, state.categoryFilter) { listState.scrollToItem(0) }

    val categoriesById = remember(state.categories) { state.categories.associateBy { it.id } }

    if (state.isLoading && state.items.isEmpty()) {
        Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }
    LazyColumn(state = listState, contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp), modifier = modifier) {
        if (state.items.isEmpty()) {
            item { Text("Belum ada transaksi di bulan ini.", modifier = Modifier.padding(vertical = 24.dp)) }
        }
        items(state.items, key = { it.id }) { transaction ->
            TransactionRow(
                transaction = transaction,
                category = categoriesById[transaction.categoryId],
                onEdit = { onAction(TransactionsAction.OpenEdit(transaction)) },
                onDelete = { onAction(TransactionsAction.RequestDelete(transaction)) },
            )
        }
        if (state.isLoadingMore) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
