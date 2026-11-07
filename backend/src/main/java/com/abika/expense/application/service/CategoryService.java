package com.abika.expense.application.service;

import com.abika.expense.application.port.in.CategoryUseCase;
import com.abika.expense.application.port.in.CreateCategoryCommand;
import com.abika.expense.application.port.in.UpdateCategoryCommand;
import com.abika.expense.application.port.out.BudgetAlertCleaner;
import com.abika.expense.application.port.out.CategoryRepository;
import com.abika.expense.application.port.out.TransactionUsageChecker;
import com.abika.expense.domain.Category;
import com.abika.expense.domain.CategoryType;
import com.abika.expense.domain.exception.ConflictException;
import com.abika.expense.domain.exception.NotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class CategoryService implements CategoryUseCase {

    private final CategoryRepository categories;
    private final TransactionUsageChecker transactionUsage;
    private final BudgetAlertCleaner budgetAlerts;

    public CategoryService(CategoryRepository categories, TransactionUsageChecker transactionUsage,
                           BudgetAlertCleaner budgetAlerts) {
        this.categories = categories;
        this.transactionUsage = transactionUsage;
        this.budgetAlerts = budgetAlerts;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> list(Long userId, CategoryType typeOrNull) {
        return categories.findByUser(userId, typeOrNull);
    }

    @Override
    @Transactional
    public Category create(CreateCategoryCommand command) {
        Category category = new Category(null, command.userId(), command.name(), command.type(), command.monthlyBudget());
        ensureNameIsFree(category, null);
        return categories.save(category);
    }

    @Override
    @Transactional
    public Category update(UpdateCategoryCommand command) {
        Category changed = findOwned(command.userId(), command.id()).withChanges(command.name(), command.monthlyBudget());
        ensureNameIsFree(changed, changed.id());
        return categories.save(changed);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long id) {
        Category category = findOwned(userId, id);
        if (transactionUsage.existsByCategory(category.id())) {
            throw new ConflictException("Kategori masih dipakai transaksi");
        }
        budgetAlerts.deleteByCategory(category.id());
        categories.delete(category.id());
    }

    private Category findOwned(Long userId, Long id) {
        return categories.findByIdAndUser(id, userId)
                .orElseThrow(() -> new NotFoundException("Kategori tidak ditemukan"));
    }

    private void ensureNameIsFree(Category category, Long excludeId) {
        if (categories.existsByUserAndNameAndType(category.userId(), category.name(), category.type(), excludeId)) {
            throw new ConflictException("Kategori dengan nama ini sudah ada");
        }
    }
}
