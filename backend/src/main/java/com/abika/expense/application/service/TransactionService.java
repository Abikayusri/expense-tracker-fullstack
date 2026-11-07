package com.abika.expense.application.service;

import com.abika.expense.application.port.in.ExpenseSavedListener;
import com.abika.expense.application.port.in.SaveTransactionCommand;
import com.abika.expense.application.port.in.TransactionQuery;
import com.abika.expense.application.port.in.TransactionUseCase;
import com.abika.expense.application.port.out.CategoryRepository;
import com.abika.expense.application.port.out.PageResult;
import com.abika.expense.application.port.out.TransactionRepository;
import com.abika.expense.domain.Category;
import com.abika.expense.domain.CategoryType;
import com.abika.expense.domain.Transaction;
import com.abika.expense.domain.exception.NotFoundException;
import org.springframework.transaction.annotation.Transactional;

public class TransactionService implements TransactionUseCase {

    private final TransactionRepository transactions;
    private final CategoryRepository categories;
    private final ExpenseSavedListener expenseSavedListener;

    public TransactionService(TransactionRepository transactions, CategoryRepository categories,
                              ExpenseSavedListener expenseSavedListener) {
        this.transactions = transactions;
        this.categories = categories;
        this.expenseSavedListener = expenseSavedListener;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Transaction> list(TransactionQuery query) {
        return transactions.findByUserAndMonth(
                query.userId(), query.month(), query.categoryIdOrNull(), query.page(), query.size());
    }

    @Override
    @Transactional
    public Transaction create(SaveTransactionCommand command) {
        return save(null, command);
    }

    @Override
    @Transactional
    public Transaction update(Long id, SaveTransactionCommand command) {
        findOwned(command.userId(), id);
        return save(id, command);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long id) {
        transactions.delete(findOwned(userId, id).id());
    }

    private Transaction save(Long id, SaveTransactionCommand command) {
        Category category = categories.findByIdAndUser(command.categoryId(), command.userId())
                .orElseThrow(() -> new NotFoundException("Kategori tidak ditemukan"));
        Transaction saved = transactions.save(new Transaction(
                id, command.userId(), category.id(), command.amount(), command.date(), command.note()));
        if (category.type() == CategoryType.EXPENSE) {
            expenseSavedListener.onExpenseSaved(saved, category);
        }
        return saved;
    }

    private Transaction findOwned(Long userId, Long id) {
        return transactions.findByIdAndUser(id, userId)
                .orElseThrow(() -> new NotFoundException("Transaksi tidak ditemukan"));
    }
}
