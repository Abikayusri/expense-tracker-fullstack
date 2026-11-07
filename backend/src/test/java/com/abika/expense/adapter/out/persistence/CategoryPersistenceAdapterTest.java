package com.abika.expense.adapter.out.persistence;

import com.abika.expense.TestcontainersConfiguration;
import com.abika.expense.domain.Category;
import com.abika.expense.domain.CategoryType;
import com.abika.expense.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainersConfiguration.class, UserPersistenceAdapter.class, CategoryPersistenceAdapter.class})
class CategoryPersistenceAdapterTest {

    @Autowired
    UserPersistenceAdapter users;

    @Autowired
    CategoryPersistenceAdapter adapter;

    private Long alice;
    private Long bob;

    @BeforeEach
    void createUsers() {
        alice = users.save(new User(null, "alice@mail.com", "hash", "Alice", Instant.now())).id();
        bob = users.save(new User(null, "bob@mail.com", "hash", "Bob", Instant.now())).id();
    }

    private Category save(Long userId, String name, CategoryType type, Long budget) {
        return adapter.save(new Category(null, userId, name, type, budget));
    }

    @Test
    void findByUser_returns_only_own_categories_sorted_by_name() {
        save(alice, "Transport", CategoryType.EXPENSE, null);
        save(alice, "Gaji", CategoryType.INCOME, null);
        save(alice, "Makan", CategoryType.EXPENSE, 1_000_000L);
        save(bob, "Bensin", CategoryType.EXPENSE, null);

        assertThat(adapter.findByUser(alice, null)).extracting(Category::name)
                .containsExactly("Gaji", "Makan", "Transport");
        assertThat(adapter.findByUser(alice, CategoryType.EXPENSE)).extracting(Category::name)
                .containsExactly("Makan", "Transport");
    }

    @Test
    void save_round_trips_budget() {
        Category saved = save(alice, "Makan", CategoryType.EXPENSE, 1_000_000L);

        assertThat(adapter.findByIdAndUser(saved.id(), alice)).contains(saved);
    }

    @Test
    void findByIdAndUser_hides_other_users_category() {
        Category aliceCategory = save(alice, "Makan", CategoryType.EXPENSE, null);

        assertThat(adapter.findByIdAndUser(aliceCategory.id(), bob)).isEmpty();
    }

    @Test
    void exists_is_case_insensitive_and_scoped_by_user_and_type() {
        Category makan = save(alice, "Makan", CategoryType.EXPENSE, null);

        assertThat(adapter.existsByUserAndNameAndType(alice, "makan", CategoryType.EXPENSE, null)).isTrue();
        assertThat(adapter.existsByUserAndNameAndType(alice, "makan", CategoryType.INCOME, null)).isFalse();
        assertThat(adapter.existsByUserAndNameAndType(bob, "makan", CategoryType.EXPENSE, null)).isFalse();
        assertThat(adapter.existsByUserAndNameAndType(alice, "MAKAN", CategoryType.EXPENSE, makan.id())).isFalse();
    }

    @Test
    void delete_removes_category() {
        Category makan = save(alice, "Makan", CategoryType.EXPENSE, null);

        adapter.delete(makan.id());

        assertThat(adapter.findByIdAndUser(makan.id(), alice)).isEmpty();
    }
}
