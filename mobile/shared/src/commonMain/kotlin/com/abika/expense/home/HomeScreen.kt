package com.abika.expense.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abika.expense.core.common.ResultState
import com.abika.expense.domain.auth.AuthRepository
import org.koin.compose.koinInject

// Placeholder until the categories, transactions and dashboard tabs exist.
@Composable
fun HomeScreen(onLogout: () -> Unit, modifier: Modifier = Modifier) {
    val authRepository = koinInject<AuthRepository>()
    var name by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        (authRepository.me() as? ResultState.Success)?.let { name = it.data.name }
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxSize().safeDrawingPadding().padding(24.dp),
    ) {
        Text("Halo${name?.let { ", $it" } ?: ""}", style = MaterialTheme.typography.headlineMedium)
        Text("Kategori, transaksi, dan dashboard menyusul.")
        OutlinedButton(onClick = onLogout) { Text("Keluar") }
    }
}
