package com.abika.expense.feature.auth.login.ui.component

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
import com.abika.expense.feature.auth.login.LoginAction
import com.abika.expense.feature.auth.login.LoginState

@Composable
fun LoginForm(state: LoginState, onAction: (LoginAction) -> Unit, modifier: Modifier = Modifier) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = modifier) {
        ErrorBanner(state.errorMessage)
        ExpenseTextField(
            value = state.email,
            onValueChange = { onAction(LoginAction.EmailChanged(it)) },
            label = "Email",
            error = state.fieldErrors["email"],
            keyboardType = KeyboardType.Email,
        )
        ExpenseTextField(
            value = state.password,
            onValueChange = { onAction(LoginAction.PasswordChanged(it)) },
            label = "Password",
            error = state.fieldErrors["password"],
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
            isPassword = true,
        )
        PrimaryButton(text = "Masuk", onClick = { onAction(LoginAction.Submit) }, loading = state.isSubmitting)
    }
}
