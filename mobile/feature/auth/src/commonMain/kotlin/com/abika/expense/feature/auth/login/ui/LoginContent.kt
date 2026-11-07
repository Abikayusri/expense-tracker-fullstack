package com.abika.expense.feature.auth.login.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abika.expense.feature.auth.login.LoginAction
import com.abika.expense.feature.auth.login.LoginState
import com.abika.expense.feature.auth.login.ui.component.AuthHeader
import com.abika.expense.feature.auth.login.ui.component.LoginForm
import com.abika.expense.feature.auth.login.ui.component.SwitchAuthLink

@Composable
fun LoginContent(
    state: LoginState,
    onAction: (LoginAction) -> Unit,
    onOpenRegister: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        AuthHeader(title = "Masuk", subtitle = "Catat pemasukan dan pengeluaranmu.")
        LoginForm(state = state, onAction = onAction)
        SwitchAuthLink(question = "Belum punya akun?", action = "Daftar", onClick = onOpenRegister)
    }
}
