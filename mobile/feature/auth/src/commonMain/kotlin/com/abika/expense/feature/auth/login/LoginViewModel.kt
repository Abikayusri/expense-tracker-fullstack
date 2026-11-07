package com.abika.expense.feature.auth.login

import androidx.lifecycle.viewModelScope
import com.abika.expense.core.common.BaseViewModel
import com.abika.expense.core.common.ResultState
import com.abika.expense.domain.auth.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
) : BaseViewModel<LoginState, LoginEvent, LoginAction>(LoginState()) {

    override fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.EmailChanged -> updateState { it.copy(email = action.value, fieldErrors = it.fieldErrors - "email") }
            is LoginAction.PasswordChanged -> updateState { it.copy(password = action.value, fieldErrors = it.fieldErrors - "password") }
            LoginAction.Submit -> submit()
        }
    }

    private fun submit() {
        if (state.isSubmitting) return
        val fieldErrors = buildMap {
            if (state.email.isBlank()) put("email", "Email wajib diisi")
            if (state.password.isBlank()) put("password", "Password wajib diisi")
        }
        if (fieldErrors.isNotEmpty()) {
            updateState { it.copy(fieldErrors = fieldErrors, errorMessage = null) }
            return
        }
        updateState { it.copy(isSubmitting = true, errorMessage = null, fieldErrors = emptyMap()) }
        viewModelScope.launch {
            when (val result = authRepository.login(state.email, state.password)) {
                is ResultState.Success -> {
                    updateState { it.copy(isSubmitting = false) }
                    sendEvent(LoginEvent.LoggedIn)
                }
                is ResultState.Error -> updateState {
                    it.copy(isSubmitting = false, errorMessage = result.error.message, fieldErrors = result.error.fieldErrors)
                }
            }
        }
    }
}
