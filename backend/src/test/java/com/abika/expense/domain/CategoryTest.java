package com.abika.expense.domain;

import com.abika.expense.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CategoryTest {

    @Test
    void income_category_with_budget_is_rejected() {
        assertThatThrownBy(() -> new Category(null, 1L, "Gaji", CategoryType.INCOME, 1_000L))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void income_category_without_budget_is_allowed() {
        assertThat(new Category(null, 1L, "Gaji", CategoryType.INCOME, null).monthlyBudget()).isNull();
    }

    @Test
    void budget_must_be_positive() {
        assertThatThrownBy(() -> new Category(null, 1L, "Makan", CategoryType.EXPENSE, 0L))
                .isInstanceOf(DomainValidationException.class);
        assertThatThrownBy(() -> new Category(null, 1L, "Makan", CategoryType.EXPENSE, -5L))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void name_is_trimmed() {
        assertThat(new Category(null, 1L, "  Makan  ", CategoryType.EXPENSE, null).name()).isEqualTo("Makan");
    }

    @Test
    void name_max_50_after_trim() {
        assertThat(new Category(null, 1L, "a".repeat(50), CategoryType.EXPENSE, null).name()).hasSize(50);
        assertThatThrownBy(() -> new Category(null, 1L, "a".repeat(51), CategoryType.EXPENSE, null))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void blank_name_is_rejected() {
        assertThatThrownBy(() -> new Category(null, 1L, "   ", CategoryType.EXPENSE, null))
                .isInstanceOf(DomainValidationException.class);
        assertThatThrownBy(() -> new Category(null, 1L, null, CategoryType.EXPENSE, null))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void type_is_required() {
        assertThatThrownBy(() -> new Category(null, 1L, "Makan", null, null))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void withChanges_keeps_id_user_and_type() {
        var original = new Category(7L, 1L, "Makan", CategoryType.EXPENSE, 1_000_000L);

        var changed = original.withChanges("Jajan", 500_000L);

        assertThat(changed).isEqualTo(new Category(7L, 1L, "Jajan", CategoryType.EXPENSE, 500_000L));
    }

    @Test
    void withChanges_still_validates() {
        var income = new Category(7L, 1L, "Gaji", CategoryType.INCOME, null);

        assertThatThrownBy(() -> income.withChanges("Gaji", 100L))
                .isInstanceOf(DomainValidationException.class);
    }
}
