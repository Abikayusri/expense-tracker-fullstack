package com.abika.expense.application;

import com.abika.expense.application.fake.InMemoryCategoryRepository;
import com.abika.expense.application.fake.InMemoryTransactionRepository;
import com.abika.expense.application.port.in.SaveTransactionCommand;
import com.abika.expense.application.port.in.TransactionQuery;
import com.abika.expense.application.service.TransactionService;
import com.abika.expense.domain.Category;
import com.abika.expense.domain.CategoryType;
import com.abika.expense.domain.Transaction;
import com.abika.expense.domain.exception.NotFoundException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransactionServiceTest {

    private static final Long ALICE = 1L;
    private static final Long BOB = 2L;
    private static final LocalDate NOV_10 = LocalDate.of(2026, 11, 10);

    private final InMemoryCategoryRepository categories = new InMemoryCategoryRepository();
    private final InMemoryTransactionRepository transactions = new InMemoryTransactionRepository();
    private final List<String> notified = new ArrayList<>();
    private final TransactionService service = new TransactionService(transactions, categories,
            (saved, category) -> notified.add(category.name() + ":" + saved.amount()));

    private final Category makan = categories.save(new Category(null, ALICE, "Makan", CategoryType.EXPENSE, 1_000_000L));
    private final Category gaji = categories.save(new Category(null, ALICE, "Gaji", CategoryType.INCOME, null));
    private final Category bobMakan = categories.save(new Category(null, BOB, "Makan", CategoryType.EXPENSE, null));

    private SaveTransactionCommand command(Long userId, Category category, long amount) {
        return new SaveTransactionCommand(userId, category.id(), amount, NOV_10, null);
    }

    @Test
    void create_expense_notifies_listener() {
        Transaction saved = service.create(command(ALICE, makan, 50_000));

        assertThat(saved.id()).isNotNull();
        assertThat(notified).containsExactly("Makan:50000");
    }

    @Test
    void create_income_does_not_notify() {
        service.create(command(ALICE, gaji, 5_000_000));

        assertThat(notified).isEmpty();
        assertThat(transactions.all()).hasSize(1);
    }

    @Test
    void create_with_other_users_category_not_found() {
        assertThatThrownBy(() -> service.create(command(ALICE, bobMakan, 50_000)))
                .isInstanceOf(NotFoundException.class);
        assertThat(transactions.all()).isEmpty();
    }

    @Test
    void update_replaces_fields_and_notifies_for_expense() {
        Transaction created = service.create(command(ALICE, makan, 50_000));
        notified.clear();

        Transaction updated = service.update(created.id(),
                new SaveTransactionCommand(ALICE, makan.id(), 75_000, LocalDate.of(2026, 12, 1), "Revisi"));

        assertThat(updated).isEqualTo(new Transaction(created.id(), ALICE, makan.id(), 75_000, LocalDate.of(2026, 12, 1), "Revisi"));
        assertThat(notified).containsExactly("Makan:75000");
    }

    @Test
    void update_other_users_transaction_not_found() {
        Transaction created = service.create(command(ALICE, makan, 50_000));

        assertThatThrownBy(() -> service.update(created.id(), command(BOB, bobMakan, 1)))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_to_other_users_category_not_found() {
        Transaction created = service.create(command(ALICE, makan, 50_000));

        assertThatThrownBy(() -> service.update(created.id(), command(ALICE, bobMakan, 1)))
                .isInstanceOf(NotFoundException.class);
        assertThat(transactions.findByIdAndUser(created.id(), ALICE)).get()
                .extracting(Transaction::categoryId).isEqualTo(makan.id());
    }

    @Test
    void delete_own_and_not_others() {
        Transaction created = service.create(command(ALICE, makan, 50_000));

        assertThatThrownBy(() -> service.delete(BOB, created.id())).isInstanceOf(NotFoundException.class);
        service.delete(ALICE, created.id());

        assertThat(transactions.all()).isEmpty();
    }

    @Test
    void list_delegates_with_query() {
        service.create(command(ALICE, makan, 50_000));
        service.create(command(BOB, bobMakan, 70_000));

        assertThat(service.list(new TransactionQuery(ALICE, YearMonth.of(2026, 11), null, 0, 20)).content())
                .extracting(Transaction::amount).containsExactly(50_000L);
    }
}
