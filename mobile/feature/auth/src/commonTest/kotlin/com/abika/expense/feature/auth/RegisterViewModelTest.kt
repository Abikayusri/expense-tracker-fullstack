package com.abika.expense.feature.auth

import app.cash.turbine.test
import com.abika.expense.core.common.AppError
import com.abika.expense.core.common.ResultState
import com.abika.expense.feature.auth.register.RegisterAction
import com.abika.expense.feature.auth.register.RegisterEvent
import com.abika.expense.feature.auth.register.RegisterViewModel
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

    private val repository = FakeAuthRepository()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel(password: String) = RegisterViewModel(repository).apply {
        onAction(RegisterAction.NameChanged("Budi"))
        onAction(RegisterAction.EmailChanged("budi@mail.com"))
        onAction(RegisterAction.PasswordChanged(password))
    }

    @Test
    fun shortPasswordIsRejectedLocally() {
        val viewModel = viewModel("1234567")

        viewModel.onAction(RegisterAction.Submit)

        assertEquals("Password minimal 8 karakter", viewModel.stateFlow.value.fieldErrors["password"])
        assertTrue(repository.calls.isEmpty())
    }

    @Test
    fun backendFieldErrorsAreMapped() {
        repository.result = ResultState.Error(
            AppError(400, "Input tidak valid", mapOf("email" to "must be a well-formed email address")),
        )
        val viewModel = viewModel("rahasia123")

        viewModel.onAction(RegisterAction.Submit)

        assertEquals("must be a well-formed email address", viewModel.stateFlow.value.fieldErrors["email"])
        assertEquals("Input tidak valid", viewModel.stateFlow.value.errorMessage)
    }

    @Test
    fun successfulRegisterEmitsRegistered() = runTest {
        val viewModel = viewModel("rahasia123")

        viewModel.eventFlow.test {
            viewModel.onAction(RegisterAction.Submit)
            assertEquals(RegisterEvent.Registered, awaitItem())
        }
        assertEquals(listOf("register Budi budi@mail.com"), repository.calls)
    }
}
