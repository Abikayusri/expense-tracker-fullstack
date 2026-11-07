package com.abika.expense.adapter.out.persistence;

import com.abika.expense.TestcontainersConfiguration;
import com.abika.expense.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainersConfiguration.class, UserPersistenceAdapter.class})
class UserPersistenceAdapterTest {

    @Autowired
    UserPersistenceAdapter adapter;

    private final Instant createdAt = Instant.now().truncatedTo(ChronoUnit.MICROS);

    @Test
    void save_then_findByEmail_and_findById() {
        User saved = adapter.save(new User(null, "budi@mail.com", "hash", "Budi", createdAt));

        assertThat(saved.id()).isNotNull();
        assertThat(adapter.findByEmail("budi@mail.com")).contains(saved);
        assertThat(adapter.findById(saved.id())).contains(saved);
    }

    @Test
    void existsByEmail_true_only_after_save() {
        assertThat(adapter.existsByEmail("budi@mail.com")).isFalse();

        adapter.save(new User(null, "budi@mail.com", "hash", "Budi", createdAt));

        assertThat(adapter.existsByEmail("budi@mail.com")).isTrue();
    }
}
