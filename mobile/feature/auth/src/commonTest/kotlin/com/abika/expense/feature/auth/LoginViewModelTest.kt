package com.abika.expense.feature.auth

import app.cash.turbine.test
import com.abika.expense.core.common.AppError
import com.abika.expense.core.common.ResultState
import com.abika.expense.feature.auth.login.LoginAction
import com.abika.expense.feature.auth.login.LoginEvent
import com.abika.expense.feature.auth.login.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val repository = FakeAuthRepository()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun filledViewModel() = LoginViewModel(repository).apply {
        onAction(LoginAction.EmailChanged("budi@mail.com"))
        onAction(LoginAction.PasswordChanged("rahasia123"))
    }

    @Test
    fun successfulLoginEmitsLoggedIn() = runTest {
        val viewModel = filledViewModel()

        viewModel.eventFlow.test {
            viewModel.onAction(LoginAction.Submit)
            assertEquals(LoginEvent.LoggedIn, awaitItem())
        }
        assertEquals(listOf("login budi@mail.com"), repository.calls)
        assertFalse(viewModel.stateFlow.value.isSubmitting)
    }

    @Test
    fun backendErrorIsShown() = runTest {
        repository.result = ResultState.Error(AppError(401, "Email atau password salah"))
        val viewModel = filledViewModel()

        viewModel.onAction(LoginAction.Submit)

        assertEquals("Email atau password salah", viewModel.stateFlow.value.errorMessage)
        assertFalse(viewModel.stateFlow.value.isSubmitting)
    }

    @Test
    fun emptyFieldsAreRejectedWithoutCallingTheBackend() {
        val viewModel = LoginViewModel(repository)

        viewModel.onAction(LoginAction.Submit)

        assertEquals("Email wajib diisi", viewModel.stateFlow.value.fieldErrors["email"])
        assertEquals("Password wajib diisi", viewModel.stateFlow.value.fieldErrors["password"])
        assertTrue(repository.calls.isEmpty())
    }

    @Test
    fun typingClearsThatFieldError() {
        val viewModel = LoginViewModel(repository)
        viewModel.onAction(LoginAction.Submit)

        viewModel.onAction(LoginAction.EmailChanged("b"))

        assertNull(viewModel.stateFlow.value.fieldErrors["email"])
        assertEquals("Password wajib diisi", viewModel.stateFlow.value.fieldErrors["password"])
    }
}
