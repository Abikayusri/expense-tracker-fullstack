package com.abika.expense.feature.transaction

import com.abika.expense.domain.category.Category
import com.abika.expense.domain.category.CategoryType
import com.abika.expense.domain.transaction.Transaction
import com.abika.expense.domain.transaction.TransactionInput
import kotlinx.coroutines.CompletableDeferred
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
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionsViewModelTest {

    private val today = LocalDate(2026, 11, 7)
    private val makan = Category(1, "Makan", CategoryType.EXPENSE, 1_000_000)
    private val transactions = FakeTransactionRepository(
        (1..25L).map { Transaction(it, 1, 1_000, LocalDate(2026, 11, 1), null) },
    )

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = TransactionsViewModel(transactions, FakeCategoryRepository(listOf(makan))) { today }

    @Test
    fun startsOnCurrentMonthPageZero() {
        val viewModel = viewModel()

        assertEquals(listOf("2026-11 - p0"), transactions.listCalls)
        assertEquals(20, viewModel.stateFlow.value.items.size)
        assertTrue(viewModel.stateFlow.value.hasMore)
        assertEquals(listOf(makan), viewModel.stateFlow.value.categories)
    }

    @Test
    fun loadMoreAppendsUntilTheEnd() {
        val viewModel = viewModel()

        viewModel.onAction(TransactionsAction.LoadMore)
        viewModel.onAction(TransactionsAction.LoadMore)

        assertEquals(listOf("2026-11 - p0", "2026-11 - p1"), transactions.listCalls)
        assertEquals(25, viewModel.stateFlow.value.items.size)
        assertFalse(viewModel.stateFlow.value.hasMore)
    }

    @Test
    fun loadMoreWhileLoadingSendsOneRequest() {
        val viewModel = viewModel()
        transactions.gate = CompletableDeferred()

        viewModel.onAction(TransactionsAction.LoadMore)
        viewModel.onAction(TransactionsAction.LoadMore)
        transactions.gate!!.complete(Unit)

        assertEquals(listOf("2026-11 - p0", "2026-11 - p1"), transactions.listCalls)
    }

    @Test
    fun changingMonthOrFilterRestartsAtPageZero() {
        val viewModel = viewModel()
        viewModel.onAction(TransactionsAction.LoadMore)

        viewModel.onAction(TransactionsAction.ChangeMonth(-1))
        viewModel.onAction(TransactionsAction.ChangeFilter(1))

        assertEquals(listOf("2026-11 - p0", "2026-11 - p1", "2026-10 - p0", "2026-10 1 p0"), transactions.listCalls)
        assertEquals(0, viewModel.stateFlow.value.page)
    }

    @Test
    fun staleResponseForOldMonthIsDropped() {
        val viewModel = viewModel()
        transactions.gate = CompletableDeferred()
        viewModel.onAction(TransactionsAction.LoadMore)

        transactions.all = emptyList()
        transactions.gate = null
        viewModel.onAction(TransactionsAction.ChangeMonth(1))

        assertTrue(viewModel.stateFlow.value.items.isEmpty())
        assertFalse(viewModel.stateFlow.value.isLoadingMore)
    }

    @Test
    fun invalidAmountsAreRejected() {
        val viewModel = viewModel()
        viewModel.onAction(TransactionsAction.OpenCreate)
        viewModel.onAction(TransactionsAction.CategoryChanged(1))

        for (amount in listOf("25000.5", "0", "", "-10", "1e3")) {
            viewModel.onAction(TransactionsAction.AmountChanged(amount))
            viewModel.onAction(TransactionsAction.SubmitForm)
            assertEquals("Jumlah harus bilangan bulat lebih dari 0", viewModel.stateFlow.value.form?.fieldErrors?.get("amount"))
        }
        assertTrue(transactions.saved.isEmpty())
    }

    @Test
    fun categoryIsRequired() {
        val viewModel = viewModel()
        viewModel.onAction(TransactionsAction.OpenCreate)
        viewModel.onAction(TransactionsAction.AmountChanged("1000"))

        viewModel.onAction(TransactionsAction.SubmitForm)

        assertEquals("Pilih kategori", viewModel.stateFlow.value.form?.fieldErrors?.get("categoryId"))
    }

    @Test
    fun validSubmitCreatesWithTodayAndNullNoteThenRefreshes() {
        val viewModel = viewModel()
        viewModel.onAction(TransactionsAction.OpenCreate)
        viewModel.onAction(TransactionsAction.CategoryChanged(1))
        viewModel.onAction(TransactionsAction.AmountChanged(" 25000 "))
        viewModel.onAction(TransactionsAction.NoteChanged("   "))

        viewModel.onAction(TransactionsAction.SubmitForm)

        assertEquals(listOf("create ${TransactionInput(1, 25_000, today, null)}"), transactions.saved)
        assertNull(viewModel.stateFlow.value.form)
        assertEquals(2, transactions.listCalls.size)
    }

    @Test
    fun editPrefillsAndUpdates() {
        val viewModel = viewModel()
        val existing = Transaction(5, 1, 30_000, LocalDate(2026, 11, 3), "Kopi")

        viewModel.onAction(TransactionsAction.OpenEdit(existing))
        viewModel.onAction(TransactionsAction.DateChanged(LocalDate(2026, 11, 4)))
        viewModel.onAction(TransactionsAction.SubmitForm)

        assertEquals(listOf("update 5 ${TransactionInput(1, 30_000, LocalDate(2026, 11, 4), "Kopi")}"), transactions.saved)
    }

    @Test
    fun noteLongerThan255IsRejected() {
        val viewModel = viewModel()
        viewModel.onAction(TransactionsAction.OpenCreate)
        viewModel.onAction(TransactionsAction.CategoryChanged(1))
        viewModel.onAction(TransactionsAction.AmountChanged("1000"))
        viewModel.onAction(TransactionsAction.NoteChanged("x".repeat(256)))

        viewModel.onAction(TransactionsAction.SubmitForm)

        assertEquals("Catatan maksimal 255 karakter", viewModel.stateFlow.value.form?.fieldErrors?.get("note"))
    }
}
