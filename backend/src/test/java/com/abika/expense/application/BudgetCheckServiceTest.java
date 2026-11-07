package com.abika.expense.application;

import com.abika.expense.application.fake.InMemoryBudgetAlertRepository;
import com.abika.expense.application.fake.InMemoryCategoryRepository;
import com.abika.expense.application.fake.InMemoryDeviceTokenRepository;
import com.abika.expense.application.fake.InMemoryTransactionRepository;
import com.abika.expense.application.fake.RecordingPushSender;
import com.abika.expense.application.port.in.SaveTransactionCommand;
import com.abika.expense.application.service.BudgetCheckService;
import com.abika.expense.application.service.TransactionService;
import com.abika.expense.domain.BudgetAlert;
import com.abika.expense.domain.Category;
import com.abika.expense.domain.CategoryType;
import com.abika.expense.domain.DeviceToken;
import com.abika.expense.domain.Platform;
import com.abika.expense.domain.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class BudgetCheckServiceTest {

    private static final Long ALICE = 1L;

    private final InMemoryCategoryRepository categories = new InMemoryCategoryRepository();
    private final InMemoryTransactionRepository transactions = new InMemoryTransactionRepository();
    private final InMemoryBudgetAlertRepository alerts = new InMemoryBudgetAlertRepository();
    private final InMemoryDeviceTokenRepository devices = new InMemoryDeviceTokenRepository();
    private final RecordingPushSender push = new RecordingPushSender();
    private final BudgetCheckService budgetCheck = new BudgetCheckService(transactions, alerts, devices, push);
    private final TransactionService service = new TransactionService(transactions, categories, budgetCheck);

    private final Category makan = categories.save(new Category(null, ALICE, "Makan", CategoryType.EXPENSE, 1_000_000L));
    private final Category hobi = categories.save(new Category(null, ALICE, "Hobi", CategoryType.EXPENSE, 100_000L));
    private final Category transport = categories.save(new Category(null, ALICE, "Transport", CategoryType.EXPENSE, null));

    @BeforeEach
    void registerPhone() {
        devices.upsert(new DeviceToken(null, ALICE, "phone-1", Platform.ANDROID));
    }

    private Transaction spend(Category category, long amount, LocalDate date) {
        return service.create(new SaveTransactionCommand(ALICE, category.id(), amount, date, null));
    }

    @Test
    void no_push_when_under_or_equal_budget() {
        spend(makan, 600_000, LocalDate.of(2026, 11, 1));
        spend(makan, 400_000, LocalDate.of(2026, 11, 2));

        assertThat(push.sent()).isEmpty();
    }

    @Test
    void push_once_when_over_budget() {
        spend(makan, 600_000, LocalDate.of(2026, 11, 1));
        spend(makan, 500_000, LocalDate.of(2026, 11, 2));
        spend(makan, 100_000, LocalDate.of(2026, 11, 3));

        assertThat(push.sent()).singleElement().satisfies(p -> {
            assertThat(p.tokens()).containsExactly("phone-1");
            assertThat(p.title()).isEqualTo("Budget terlampaui");
            assertThat(p.body()).isEqualTo("Pengeluaran Makan bulan ini Rp1.100.000 melebihi budget Rp1.000.000");
        });
    }

    @Test
    void new_month_can_alert_again() {
        spend(makan, 1_500_000, LocalDate.of(2026, 11, 1));
        spend(makan, 1_500_000, LocalDate.of(2026, 12, 1));

        assertThat(push.sent()).hasSize(2);
        assertThat(alerts.all()).containsExactlyInAnyOrder(
                new BudgetAlert(ALICE, makan.id(), YearMonth.of(2026, 11)),
                new BudgetAlert(ALICE, makan.id(), YearMonth.of(2026, 12)));
    }

    @Test
    void update_moving_to_other_category_checks_new_category() {
        Transaction tx = spend(makan, 150_000, LocalDate.of(2026, 11, 1));

        service.update(tx.id(), new SaveTransactionCommand(ALICE, hobi.id(), 150_000, LocalDate.of(2026, 11, 1), null));

        assertThat(push.sent()).singleElement()
                .extracting(RecordingPushSender.Push::body)
                .isEqualTo("Pengeluaran Hobi bulan ini Rp150.000 melebihi budget Rp100.000");
    }

    @Test
    void update_moving_to_other_month_checks_new_month() {
        spend(makan, 900_000, LocalDate.of(2026, 12, 5));
        Transaction tx = spend(makan, 200_000, LocalDate.of(2026, 11, 1));

        service.update(tx.id(), new SaveTransactionCommand(ALICE, makan.id(), 200_000, LocalDate.of(2026, 12, 1), null));

        assertThat(push.sent()).hasSize(1);
        assertThat(alerts.all()).containsExactly(new BudgetAlert(ALICE, makan.id(), YearMonth.of(2026, 12)));
    }

    @Test
    void no_budget_no_push() {
        spend(transport, 99_000_000, LocalDate.of(2026, 11, 1));

        assertThat(push.sent()).isEmpty();
        assertThat(alerts.all()).isEmpty();
    }

    @Test
    void no_device_tokens_no_push_but_alert_recorded() {
        devices.deleteByUserAndToken(ALICE, "phone-1");

        spend(makan, 1_500_000, LocalDate.of(2026, 11, 1));

        assertThat(push.sent()).isEmpty();
        assertThat(alerts.all()).hasSize(1);
    }

    @Test
    void push_failure_does_not_throw() {
        BudgetCheckService failing = new BudgetCheckService(transactions, alerts, devices,
                (tokens, title, body) -> { throw new IllegalStateException("FCM down"); });
        Transaction saved = transactions.save(new Transaction(null, ALICE, makan.id(), 2_000_000, LocalDate.of(2026, 11, 1), null));

        assertThatCode(() -> failing.onExpenseSaved(saved, makan)).doesNotThrowAnyException();
    }

    @Test
    void sends_to_every_registered_device() {
        devices.upsert(new DeviceToken(null, ALICE, "tablet-1", Platform.ANDROID));

        spend(makan, 1_500_000, LocalDate.of(2026, 11, 1));

        assertThat(push.sent()).singleElement()
                .extracting(RecordingPushSender.Push::tokens)
                .isEqualTo(List.of("phone-1", "tablet-1"));
    }
}
