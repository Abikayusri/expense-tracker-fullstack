package com.abika.expense.core.common

import app.cash.turbine.test
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

@OptIn(ExperimentalCoroutinesApi::class)
class BaseViewModelTest {

    private class CounterViewModel : BaseViewModel<Int, String, Int>(0) {
        override fun onAction(action: Int) {
            updateState { it + action }
            if (state >= 3) sendEvent("reached $state")
        }
    }

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun actionsUpdateStateAndEmitEventsOnce() = runTest {
        val viewModel = CounterViewModel()

        viewModel.eventFlow.test {
            viewModel.onAction(1)
            viewModel.onAction(2)
            assertEquals(3, viewModel.stateFlow.value)
            assertEquals("reached 3", awaitItem())
            expectNoEvents()
        }
    }
}
