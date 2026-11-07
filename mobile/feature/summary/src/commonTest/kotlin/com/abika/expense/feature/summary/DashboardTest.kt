package com.abika.expense.feature.summary

import com.abika.expense.core.common.AppError
import com.abika.expense.core.common.ResultState
import com.abika.expense.domain.category.CategoryType
import com.abika.expense.domain.summary.CategorySummary
import com.abika.expense.domain.summary.Summary
import com.abika.expense.domain.summary.SummaryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardTest {

    private val makan = CategorySummary(1, "Makan", CategoryType.EXPENSE, 1_250_000, 1_000_000, true)
    private val hobi = CategorySummary(2, "Hobi", CategoryType.EXPENSE, 150_000, 100_000, true)
    private val transport = CategorySummary(3, "Transport", CategoryType.EXPENSE, 50_000, null, false)
    private val gaji = CategorySummary(4, "Gaji", CategoryType.INCOME, 5_000_000, null, false)
    private val summary = Summary(5_000_000, 1_450_000, 3_550_000, listOf(makan, hobi, transport, gaji))

    private class FakeRepository(var result: ResultState<Summary>) : SummaryRepository {
        val months = mutableListOf<String>()
        override suspend fun get(month: String): ResultState<Summary> {
            months += month
            return result
        }
    }

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun overBudgetLinesMatchTheWebCopy() {
        assertEquals(
            listOf(
                "Pengeluaran Makan sudah Rp1.250.000, melebihi budget Rp1.000.000",
                "Pengeluaran Hobi sudah Rp150.000, melebihi budget Rp100.000",
            ),
            BudgetText.overBudgetLines(summary),
        )
    }

    @Test
    fun budgetsOnlyListExpenseCategoriesWithABudget() {
        assertEquals(listOf(makan, hobi), BudgetText.budgets(summary))
    }

    @Test
    fun progressIsCappedAtOne() {
        assertEquals(1f, BudgetText.progress(makan.copy(spent = 1_500_000)))
        assertEquals(0.5f, BudgetText.progress(makan.copy(spent = 500_000)))
        assertEquals(0f, BudgetText.progress(transport))
    }

    @Test
    fun refreshLoadsCurrentMonthAndMonthChangeReloads() {
        val repository = FakeRepository(ResultState.Success(summary))
        val viewModel = DashboardViewModel(repository) { LocalDate(2026, 11, 7) }

        viewModel.onAction(DashboardAction.Refresh)
        viewModel.onAction(DashboardAction.ChangeMonth(-1))

        assertEquals(listOf("2026-11", "2026-10"), repository.months)
        assertEquals(summary, viewModel.stateFlow.value.summary)
        assertEquals("2026-10", viewModel.stateFlow.value.month)
    }

    @Test
    fun errorIsShown() {
        val repository = FakeRepository(ResultState.Error(AppError(0, "Tidak bisa terhubung ke server")))
        val viewModel = DashboardViewModel(repository) { LocalDate(2026, 11, 7) }

        viewModel.onAction(DashboardAction.Refresh)

        assertEquals("Tidak bisa terhubung ke server", viewModel.stateFlow.value.errorMessage)
        assertNull(viewModel.stateFlow.value.summary)
    }

    @Test
    fun nothingLoadsBeforeTheScreenAsks() {
        val repository = FakeRepository(ResultState.Success(summary))

        DashboardViewModel(repository) { LocalDate(2026, 11, 7) }

        assertTrue(repository.months.isEmpty())
    }
}
