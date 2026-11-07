package com.abika.expense.feature.auth.register

import androidx.lifecycle.viewModelScope
import com.abika.expense.core.common.BaseViewModel
import com.abika.expense.core.common.ResultState
import com.abika.expense.domain.auth.AuthRepository
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository,
) : BaseViewModel<RegisterState, RegisterEvent, RegisterAction>(RegisterState()) {

    override fun onAction(action: RegisterAction) {
        when (action) {
            is RegisterAction.NameChanged -> updateState { it.copy(name = action.value, fieldErrors = it.fieldErrors - "name") }
            is RegisterAction.EmailChanged -> updateState { it.copy(email = action.value, fieldErrors = it.fieldErrors - "email") }
            is RegisterAction.PasswordChanged -> updateState { it.copy(password = action.value, fieldErrors = it.fieldErrors - "password") }
            RegisterAction.Submit -> submit()
        }
    }

    private fun submit() {
        if (state.isSubmitting) return
        val fieldErrors = buildMap {
            if (state.name.isBlank()) put("name", "Nama wajib diisi")
            if (state.email.isBlank()) put("email", "Email wajib diisi")
            if (state.password.length < MIN_PASSWORD) put("password", "Password minimal $MIN_PASSWORD karakter")
        }
        if (fieldErrors.isNotEmpty()) {
            updateState { it.copy(fieldErrors = fieldErrors, errorMessage = null) }
            return
        }
        updateState { it.copy(isSubmitting = true, errorMessage = null, fieldErrors = emptyMap()) }
        viewModelScope.launch {
            when (val result = authRepository.register(state.name, state.email, state.password)) {
                is ResultState.Success -> {
                    updateState { it.copy(isSubmitting = false) }
                    sendEvent(RegisterEvent.Registered)
                }
                is ResultState.Error -> updateState {
                    it.copy(isSubmitting = false, errorMessage = result.error.message, fieldErrors = result.error.fieldErrors)
                }
            }
        }
    }

    private companion object {
        const val MIN_PASSWORD = 8
    }
}
