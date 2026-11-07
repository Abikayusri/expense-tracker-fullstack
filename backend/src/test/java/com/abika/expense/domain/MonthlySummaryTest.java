package com.abika.expense.domain;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class MonthlySummaryTest {

    private final Category gaji = new Category(1L, 9L, "Gaji", CategoryType.INCOME, null);
    private final Category makan = new Category(2L, 9L, "Makan", CategoryType.EXPENSE, 1_000_000L);
    private final Category hobi = new Category(3L, 9L, "Hobi", CategoryType.EXPENSE, 500_000L);

    @Test
    void aggregates_income_expense_and_flags_over_budget() {
        var summary = MonthlySummary.of(List.of(gaji, makan, hobi), Map.of(1L, 5_000_000L, 2L, 1_250_000L));

        assertThat(summary.totalIncome()).isEqualTo(5_000_000L);
        assertThat(summary.totalExpense()).isEqualTo(1_250_000L);
        assertThat(summary.balance()).isEqualTo(3_750_000L);
        assertThat(summary.perCategory())
                .extracting(CategorySummary::categoryId, CategorySummary::spent, CategorySummary::overBudget)
                .containsExactly(
                        tuple(1L, 5_000_000L, false),
                        tuple(2L, 1_250_000L, true),
                        tuple(3L, 0L, false));
    }

    @Test
    void spent_equal_to_budget_is_not_over() {
        var summary = MonthlySummary.of(List.of(makan), Map.of(2L, 1_000_000L));

        assertThat(summary.perCategory().getFirst().overBudget()).isFalse();
    }

    @Test
    void no_categories_gives_zeros() {
        var summary = MonthlySummary.of(List.of(), Map.of());

        assertThat(summary.totalIncome()).isZero();
        assertThat(summary.totalExpense()).isZero();
        assertThat(summary.balance()).isZero();
        assertThat(summary.perCategory()).isEmpty();
    }

    @Test
    void negative_balance_when_expense_exceeds_income() {
        var summary = MonthlySummary.of(List.of(gaji, makan), Map.of(1L, 100_000L, 2L, 300_000L));

        assertThat(summary.balance()).isEqualTo(-200_000L);
    }

    @Test
    void category_summary_without_budget_is_never_over() {
        assertThat(new CategorySummary(1L, "Gaji", CategoryType.INCOME, 9_999_999L, null).overBudget()).isFalse();
    }
}
