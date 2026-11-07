package com.abika.expense.application.port.out;

import com.abika.expense.domain.BudgetAlert;

public interface BudgetAlertRepository extends BudgetAlertCleaner {

    /** Returns true only for the call that actually inserted the row, so concurrent requests alert once. */
    boolean insertIfAbsent(BudgetAlert alert);
}
