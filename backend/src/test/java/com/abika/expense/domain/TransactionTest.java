package com.abika.expense.domain;

import com.abika.expense.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransactionTest {

    private static Transaction tx(long amount, LocalDate date, String note) {
        return new Transaction(null, 1L, 2L, amount, date, note);
    }

    @Test
    void amount_must_be_positive() {
        assertThatThrownBy(() -> tx(0, LocalDate.of(2026, 11, 1), null))
                .isInstanceOf(DomainValidationException.class);
        assertThatThrownBy(() -> tx(-1, LocalDate.of(2026, 11, 1), null))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void date_is_required() {
        assertThatThrownBy(() -> tx(1_000, null, null))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void note_max_255() {
        assertThat(tx(1_000, LocalDate.of(2026, 11, 1), "a".repeat(255)).note()).hasSize(255);
        assertThatThrownBy(() -> tx(1_000, LocalDate.of(2026, 11, 1), "a".repeat(256)))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void note_is_optional() {
        assertThat(tx(1_000, LocalDate.of(2026, 11, 1), null).note()).isNull();
    }

    @Test
    void month_from_date() {
        assertThat(tx(1_000, LocalDate.of(2026, 11, 30), null).month()).isEqualTo(YearMonth.of(2026, 11));
    }
}
