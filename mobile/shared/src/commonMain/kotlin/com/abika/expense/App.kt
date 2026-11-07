package com.abika.expense

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.abika.expense.core.designsystem.ExpenseTheme
import com.abika.expense.debug.NetworkInspectorOverlay
import com.abika.expense.navigation.AppNavigation

@Composable
fun App() {
    ExpenseTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize()) {
                AppNavigation()
                NetworkInspectorOverlay()
            }
        }
    }
}
