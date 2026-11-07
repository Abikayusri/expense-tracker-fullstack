package com.abika.expense.feature.auth.register.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.abika.expense.core.sharedui.ErrorBanner
import com.abika.expense.core.sharedui.ExpenseTextField
import com.abika.expense.core.sharedui.PrimaryButton
import com.abika.expense.feature.auth.register.RegisterAction
import com.abika.expense.feature.auth.register.RegisterState

@Composable
fun RegisterForm(state: RegisterState, onAction: (RegisterAction) -> Unit, modifier: Modifier = Modifier) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = modifier) {
        ErrorBanner(state.errorMessage)
        ExpenseTextField(
            value = state.name,
            onValueChange = { onAction(RegisterAction.NameChanged(it)) },
            label = "Nama",
            error = state.fieldErrors["name"],
        )
        ExpenseTextField(
            value = state.email,
            onValueChange = { onAction(RegisterAction.EmailChanged(it)) },
            label = "Email",
            error = state.fieldErrors["email"],
            keyboardType = KeyboardType.Email,
        )
        ExpenseTextField(
            value = state.password,
            onValueChange = { onAction(RegisterAction.PasswordChanged(it)) },
            label = "Password (minimal 8 karakter)",
            error = state.fieldErrors["password"],
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
            isPassword = true,
        )
        PrimaryButton(text = "Daftar", onClick = { onAction(RegisterAction.Submit) }, loading = state.isSubmitting)
    }
}
