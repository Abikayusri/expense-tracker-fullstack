package com.abika.expense.adapter.out.persistence;

import com.abika.expense.TestcontainersConfiguration;
import com.abika.expense.application.port.out.PageResult;
import com.abika.expense.domain.Category;
import com.abika.expense.domain.CategoryType;
import com.abika.expense.domain.Transaction;
import com.abika.expense.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainersConfiguration.class, UserPersistenceAdapter.class,
        CategoryPersistenceAdapter.class, TransactionPersistenceAdapter.class})
class TransactionPersistenceAdapterTest {

    private static final YearMonth NOVEMBER = YearMonth.of(2026, 11);

    @Autowired
    UserPersistenceAdapter users;

    @Autowired
    CategoryPersistenceAdapter categories;

    @Autowired
    TransactionPersistenceAdapter adapter;

    private Long alice;
    private Long bob;
    private Long makan;
    private Long transport;
    private Long bobMakan;

    @BeforeEach
    void setUp() {
        alice = users.save(new User(null, "alice@mail.com", "hash", "Alice", Instant.now())).id();
        bob = users.save(new User(null, "bob@mail.com", "hash", "Bob", Instant.now())).id();
        makan = categories.save(new Category(null, alice, "Makan", CategoryType.EXPENSE, null)).id();
        transport = categories.save(new Category(null, alice, "Transport", CategoryType.EXPENSE, null)).id();
        bobMakan = categories.save(new Category(null, bob, "Makan", CategoryType.EXPENSE, null)).id();
    }

    private Transaction save(Long userId, Long categoryId, long amount, LocalDate date) {
        return adapter.save(new Transaction(null, userId, categoryId, amount, date, null));
    }

    @Test
    void findByUserAndMonth_filters_month_and_sorts_newest_first() {
        save(alice, makan, 10_000, LocalDate.of(2026, 10, 31));
        Transaction first = save(alice, makan, 20_000, LocalDate.of(2026, 11, 1));
        Transaction last = save(alice, makan, 30_000, LocalDate.of(2026, 11, 15));
        save(alice, makan, 40_000, LocalDate.of(2026, 12, 1));
        save(bob, bobMakan, 50_000, LocalDate.of(2026, 11, 10));

        PageResult<Transaction> result = adapter.findByUserAndMonth(alice, NOVEMBER, null, 0, 20);

        assertThat(result.content()).containsExactly(last, first);
        assertThat(result.totalElements()).isEqualTo(2);
    }

    @Test
    void same_date_sorted_by_id_descending() {
        Transaction older = save(alice, makan, 10_000, LocalDate.of(2026, 11, 5));
        Transaction newer = save(alice, makan, 20_000, LocalDate.of(2026, 11, 5));

        assertThat(adapter.findByUserAndMonth(alice, NOVEMBER, null, 0, 20).content()).containsExactly(newer, older);
    }

    @Test
    void findByUserAndMonth_filters_category_and_paginates() {
        for (int day = 1; day <= 5; day++) {
            save(alice, makan, 1_000L * day, LocalDate.of(2026, 11, day));
        }
        save(alice, transport, 7_000, LocalDate.of(2026, 11, 3));

        PageResult<Transaction> page1 = adapter.findByUserAndMonth(alice, NOVEMBER, makan, 1, 2);

        assertThat(page1.totalElements()).isEqualTo(5);
        assertThat(page1.content()).extracting(Transaction::date)
                .containsExactly(LocalDate.of(2026, 11, 3), LocalDate.of(2026, 11, 2));
    }

    @Test
    void sumByCategoryAndMonth_ignores_other_months() {
        save(alice, makan, 100_000, LocalDate.of(2026, 11, 1));
        save(alice, makan, 250_000, LocalDate.of(2026, 11, 30));
        save(alice, makan, 999_000, LocalDate.of(2026, 12, 1));

        assertThat(adapter.sumByCategoryAndMonth(makan, NOVEMBER)).isEqualTo(350_000);
        assertThat(adapter.sumByCategoryAndMonth(transport, NOVEMBER)).isZero();
    }

    @Test
    void sumPerCategory_groups_by_category_for_one_user() {
        save(alice, makan, 100_000, LocalDate.of(2026, 11, 1));
        save(alice, makan, 50_000, LocalDate.of(2026, 11, 2));
        save(alice, transport, 20_000, LocalDate.of(2026, 11, 3));
        save(bob, bobMakan, 70_000, LocalDate.of(2026, 11, 3));

        assertThat(adapter.sumPerCategory(alice, NOVEMBER)).isEqualTo(Map.of(makan, 150_000L, transport, 20_000L));
    }

    @Test
    void existsByCategory_and_ownership() {
        Transaction t = save(alice, makan, 10_000, LocalDate.of(2026, 11, 1));

        assertThat(adapter.existsByCategory(makan)).isTrue();
        assertThat(adapter.existsByCategory(transport)).isFalse();
        assertThat(adapter.findByIdAndUser(t.id(), bob)).isEmpty();
        assertThat(adapter.findByIdAndUser(t.id(), alice)).contains(t);
    }
}
