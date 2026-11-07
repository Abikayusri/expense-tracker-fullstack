package com.abika.expense.feature.summary

import androidx.lifecycle.viewModelScope
import com.abika.expense.core.common.BaseViewModel
import com.abika.expense.core.common.MonthText
import com.abika.expense.core.common.ResultState
import com.abika.expense.domain.summary.SummaryRepository
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

/** Loads on [DashboardAction.Refresh], which the screen sends every time the tab is shown. */
class DashboardViewModel(
    private val repository: SummaryRepository,
    today: () -> LocalDate,
) : BaseViewModel<DashboardState, Nothing, DashboardAction>(DashboardState(month = MonthText.of(today()))) {

    private var generation = 0

    override fun onAction(action: DashboardAction) {
        when (action) {
            is DashboardAction.ChangeMonth -> {
                updateState { it.copy(month = MonthText.shift(it.month, action.delta)) }
                load()
            }
            DashboardAction.Refresh -> load()
        }
    }

    private fun load() {
        val requested = ++generation
        val month = state.month
        updateState { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = repository.get(month)
            if (requested != generation) return@launch
            when (result) {
                is ResultState.Success -> updateState { it.copy(isLoading = false, summary = result.data) }
                is ResultState.Error -> updateState { it.copy(isLoading = false, errorMessage = result.error.message) }
            }
        }
    }
}
