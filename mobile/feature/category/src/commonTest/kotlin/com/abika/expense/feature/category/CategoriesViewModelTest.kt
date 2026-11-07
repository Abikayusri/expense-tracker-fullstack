package com.abika.expense.feature.category

import com.abika.expense.core.common.AppError
import com.abika.expense.domain.category.Category
import com.abika.expense.domain.category.CategoryType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CategoriesViewModelTest {

    private val makan = Category(1, "Makan", CategoryType.EXPENSE, 1_000_000)
    private val gaji = Category(2, "Gaji", CategoryType.INCOME, null)
    private val repository = FakeCategoryRepository(listOf(makan, gaji))

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = CategoriesViewModel(repository)

    @Test
    fun loadSplitsByType() {
        val state = viewModel().stateFlow.value

        assertEquals(listOf(makan), state.expenses)
        assertEquals(listOf(gaji), state.incomes)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun createValidCategoryAndReload() {
        val viewModel = viewModel()
        viewModel.onAction(CategoriesAction.OpenCreate)
        viewModel.onAction(CategoriesAction.NameChanged("Hobi"))
        viewModel.onAction(CategoriesAction.BudgetChanged("500000"))

        viewModel.onAction(CategoriesAction.SubmitForm)

        assertTrue("create Hobi EXPENSE 500000" in repository.calls)
        assertNull(viewModel.stateFlow.value.form)
        assertEquals(listOf("Hobi", "Makan"), viewModel.stateFlow.value.expenses.map { it.name })
    }

    @Test
    fun invalidBudgetIsRejectedWithoutCallingTheBackend() {
        val viewModel = viewModel()
        viewModel.onAction(CategoriesAction.OpenCreate)
        viewModel.onAction(CategoriesAction.NameChanged("Hobi"))

        for (budget in listOf("12.5", "0", "-5", "abc")) {
            viewModel.onAction(CategoriesAction.BudgetChanged(budget))
            viewModel.onAction(CategoriesAction.SubmitForm)
            assertEquals("Budget harus bilangan bulat lebih dari 0", viewModel.stateFlow.value.form?.fieldErrors?.get("monthlyBudget"))
        }
        assertTrue(repository.calls.none { it.startsWith("create") })
    }

    @Test
    fun incomeIgnoresBudget() {
        val viewModel = viewModel()
        viewModel.onAction(CategoriesAction.OpenCreate)
        viewModel.onAction(CategoriesAction.NameChanged("Bonus"))
        viewModel.onAction(CategoriesAction.BudgetChanged("abc"))
        viewModel.onAction(CategoriesAction.TypeChanged(CategoryType.INCOME))

        viewModel.onAction(CategoriesAction.SubmitForm)

        assertTrue("create Bonus INCOME null" in repository.calls)
    }

    @Test
    fun editKeepsTypeLockedAndSendsUpdate() {
        val viewModel = viewModel()
        viewModel.onAction(CategoriesAction.OpenEdit(makan))
        viewModel.onAction(CategoriesAction.TypeChanged(CategoryType.INCOME))
        viewModel.onAction(CategoriesAction.BudgetChanged("1500000"))

        assertEquals(CategoryType.EXPENSE, viewModel.stateFlow.value.form?.type)
        viewModel.onAction(CategoriesAction.SubmitForm)

        assertTrue("update 1 Makan 1500000" in repository.calls)
    }

    @Test
    fun backendErrorStaysInTheForm() {
        repository.saveError = AppError(409, "Kategori dengan nama ini sudah ada")
        val viewModel = viewModel()
        viewModel.onAction(CategoriesAction.OpenCreate)
        viewModel.onAction(CategoriesAction.NameChanged("makan"))

        viewModel.onAction(CategoriesAction.SubmitForm)

        assertEquals("Kategori dengan nama ini sudah ada", viewModel.stateFlow.value.form?.errorMessage)
        assertEquals(false, viewModel.stateFlow.value.form?.isSubmitting)
    }

    @Test
    fun deleteConflictShowsMessageAndKeepsList() {
        repository.deleteError = AppError(409, "Kategori masih dipakai transaksi")
        val viewModel = viewModel()

        viewModel.onAction(CategoriesAction.RequestDelete(makan))
        viewModel.onAction(CategoriesAction.ConfirmDelete)

        assertEquals("Kategori masih dipakai transaksi", viewModel.stateFlow.value.errorMessage)
        assertEquals(listOf(makan), viewModel.stateFlow.value.expenses)
        assertNull(viewModel.stateFlow.value.pendingDelete)
    }

    @Test
    fun cancelDeleteDoesNothing() {
        val viewModel = viewModel()

        viewModel.onAction(CategoriesAction.RequestDelete(makan))
        viewModel.onAction(CategoriesAction.CancelDelete)

        assertTrue(repository.calls.none { it.startsWith("delete") })
    }

    @Test
    fun blankNameIsRejected() {
        val viewModel = viewModel()
        viewModel.onAction(CategoriesAction.OpenCreate)

        viewModel.onAction(CategoriesAction.SubmitForm)

        assertEquals("Nama kategori wajib diisi", viewModel.stateFlow.value.form?.fieldErrors?.get("name"))
    }
}
