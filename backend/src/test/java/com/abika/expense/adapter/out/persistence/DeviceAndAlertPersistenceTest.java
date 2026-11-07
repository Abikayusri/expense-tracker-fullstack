package com.abika.expense.adapter.out.persistence;

import com.abika.expense.TestcontainersConfiguration;
import com.abika.expense.domain.BudgetAlert;
import com.abika.expense.domain.Category;
import com.abika.expense.domain.CategoryType;
import com.abika.expense.domain.DeviceToken;
import com.abika.expense.domain.Platform;
import com.abika.expense.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainersConfiguration.class, UserPersistenceAdapter.class, CategoryPersistenceAdapter.class,
        DevicePersistenceAdapter.class, BudgetAlertPersistenceAdapter.class})
class DeviceAndAlertPersistenceTest {

    @Autowired
    UserPersistenceAdapter users;

    @Autowired
    CategoryPersistenceAdapter categories;

    @Autowired
    DevicePersistenceAdapter devices;

    @Autowired
    BudgetAlertPersistenceAdapter alerts;

    private Long alice;
    private Long bob;
    private Long makan;

    @BeforeEach
    void setUp() {
        alice = users.save(new User(null, "alice@mail.com", "hash", "Alice", Instant.now())).id();
        bob = users.save(new User(null, "bob@mail.com", "hash", "Bob", Instant.now())).id();
        makan = categories.save(new Category(null, alice, "Makan", CategoryType.EXPENSE, 1_000L)).id();
    }

    @Test
    void upsert_is_idempotent() {
        devices.upsert(new DeviceToken(null, alice, "token-1", Platform.ANDROID));
        devices.upsert(new DeviceToken(null, alice, "token-1", Platform.ANDROID));

        assertThat(devices.findByUser(alice)).extracting(DeviceToken::fcmToken).containsExactly("token-1");
    }

    @Test
    void upsert_moves_token_to_new_user() {
        devices.upsert(new DeviceToken(null, alice, "shared-phone", Platform.ANDROID));

        devices.upsert(new DeviceToken(null, bob, "shared-phone", Platform.ANDROID));

        assertThat(devices.findByUser(alice)).isEmpty();
        assertThat(devices.findByUser(bob)).extracting(DeviceToken::fcmToken).containsExactly("shared-phone");
    }

    @Test
    void delete_only_removes_own_token() {
        devices.upsert(new DeviceToken(null, alice, "token-1", Platform.ANDROID));

        devices.deleteByUserAndToken(bob, "token-1");
        assertThat(devices.findByUser(alice)).hasSize(1);

        devices.deleteByUserAndToken(alice, "token-1");
        assertThat(devices.findByUser(alice)).isEmpty();
    }

    @Test
    void insertIfAbsent_true_only_once_per_month() {
        BudgetAlert november = new BudgetAlert(alice, makan, YearMonth.of(2026, 11));

        assertThat(alerts.insertIfAbsent(november)).isTrue();
        assertThat(alerts.insertIfAbsent(november)).isFalse();
        assertThat(alerts.insertIfAbsent(new BudgetAlert(alice, makan, YearMonth.of(2026, 12)))).isTrue();
    }

    @Test
    void deleteByCategory_allows_alerting_again() {
        BudgetAlert november = new BudgetAlert(alice, makan, YearMonth.of(2026, 11));
        alerts.insertIfAbsent(november);

        alerts.deleteByCategory(makan);

        assertThat(alerts.insertIfAbsent(november)).isTrue();
    }
}
