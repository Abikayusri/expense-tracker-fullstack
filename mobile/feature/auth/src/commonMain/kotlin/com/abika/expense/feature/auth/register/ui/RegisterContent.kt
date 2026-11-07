package com.abika.expense.feature.auth.register.ui

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
import com.abika.expense.feature.auth.login.ui.component.AuthHeader
import com.abika.expense.feature.auth.login.ui.component.SwitchAuthLink
import com.abika.expense.feature.auth.register.RegisterAction
import com.abika.expense.feature.auth.register.RegisterState
import com.abika.expense.feature.auth.register.ui.component.RegisterForm

@Composable
fun RegisterContent(
    state: RegisterState,
    onAction: (RegisterAction) -> Unit,
    onOpenLogin: () -> Unit,
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
        AuthHeader(title = "Daftar", subtitle = "Buat akun untuk mulai mencatat.")
        RegisterForm(state = state, onAction = onAction)
        SwitchAuthLink(question = "Sudah punya akun?", action = "Masuk", onClick = onOpenLogin)
    }
}
