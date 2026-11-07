package com.abika.expense.application;

import com.abika.expense.application.fake.InMemoryCategoryRepository;
import com.abika.expense.application.fake.InMemoryTransactionRepository;
import com.abika.expense.application.service.SummaryService;
import com.abika.expense.domain.Category;
import com.abika.expense.domain.CategorySummary;
import com.abika.expense.domain.CategoryType;
import com.abika.expense.domain.MonthlySummary;
import com.abika.expense.domain.Transaction;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class SummaryServiceTest {

    private static final Long ALICE = 1L;
    private static final Long BOB = 2L;

    private final InMemoryCategoryRepository categories = new InMemoryCategoryRepository();
    private final InMemoryTransactionRepository transactions = new InMemoryTransactionRepository();
    private final SummaryService service = new SummaryService(categories, transactions);

    private final Category gaji = categories.save(new Category(null, ALICE, "Gaji", CategoryType.INCOME, null));
    private final Category makan = categories.save(new Category(null, ALICE, "Makan", CategoryType.EXPENSE, 1_000_000L));
    private final Category bobMakan = categories.save(new Category(null, BOB, "Makan", CategoryType.EXPENSE, null));

    private void add(Long userId, Category category, long amount, LocalDate date) {
        transactions.save(new Transaction(null, userId, category.id(), amount, date, null));
    }

    @Test
    void combines_users_categories_with_month_totals() {
        add(ALICE, gaji, 5_000_000, LocalDate.of(2026, 11, 1));
        add(ALICE, makan, 1_250_000, LocalDate.of(2026, 11, 5));
        add(ALICE, makan, 300_000, LocalDate.of(2026, 10, 31));
        add(BOB, bobMakan, 9_999_999, LocalDate.of(2026, 11, 5));

        MonthlySummary summary = service.get(ALICE, YearMonth.of(2026, 11));

        assertThat(summary.totalIncome()).isEqualTo(5_000_000);
        assertThat(summary.totalExpense()).isEqualTo(1_250_000);
        assertThat(summary.balance()).isEqualTo(3_750_000);
        assertThat(summary.perCategory()).extracting(CategorySummary::name, CategorySummary::overBudget)
                .containsExactly(tuple("Gaji", false),
                        tuple("Makan", true));
    }

    @Test
    void empty_month_lists_all_categories_with_zero() {
        MonthlySummary summary = service.get(ALICE, YearMonth.of(2025, 1));

        assertThat(summary.balance()).isZero();
        assertThat(summary.perCategory()).extracting(CategorySummary::spent).containsExactly(0L, 0L);
    }
}
