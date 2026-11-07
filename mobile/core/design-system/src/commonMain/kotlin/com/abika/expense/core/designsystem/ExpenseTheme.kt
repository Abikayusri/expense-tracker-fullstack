package com.abika.expense.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun ExpenseTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) {
        darkColorScheme(primary = ExpenseColors.Primary, error = ExpenseColors.Expense)
    } else {
        lightColorScheme(primary = ExpenseColors.Primary, error = ExpenseColors.Expense)
    }
    MaterialTheme(colorScheme = colors, content = content)
}
