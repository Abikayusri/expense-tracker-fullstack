package com.abika.expense.application.port.in;

import com.abika.expense.application.port.out.PageResult;
import com.abika.expense.domain.Transaction;

public interface TransactionUseCase {

    PageResult<Transaction> list(TransactionQuery query);

    Transaction create(SaveTransactionCommand command);

    Transaction update(Long id, SaveTransactionCommand command);

    void delete(Long userId, Long id);
}
