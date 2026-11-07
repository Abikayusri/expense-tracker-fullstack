package com.abika.expense.application.port.in;

import com.abika.expense.domain.Category;
import com.abika.expense.domain.Transaction;

/** Called after an expense transaction is created or updated, inside the same database transaction. */
public interface ExpenseSavedListener {

    void onExpenseSaved(Transaction saved, Category category);
}
