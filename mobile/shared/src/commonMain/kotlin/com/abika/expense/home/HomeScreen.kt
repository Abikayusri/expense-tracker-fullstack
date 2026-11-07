package com.abika.expense.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import com.abika.expense.feature.category.ui.CategoriesScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onLogout: () -> Unit, modifier: Modifier = Modifier) {
    var selected by rememberSaveable { mutableIntStateOf(HomeTab.Categories.ordinal) }
    val tab = HomeTab.entries[selected]

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(tab.label) },
                actions = { TextButton(onClick = onLogout) { Text("Keluar") } },
            )
        },
        bottomBar = {
            NavigationBar {
                HomeTab.entries.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = index == selected,
                        onClick = { selected = index },
                        icon = { Text(item.icon, modifier = Modifier.clearAndSetSemantics { }) },
                        label = { Text(item.label) },
                    )
                }
            }
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (tab) {
                HomeTab.Categories -> CategoriesScreen()
                HomeTab.Dashboard, HomeTab.Transactions -> Text(
                    "${tab.label} segera hadir.",
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
    }
}
