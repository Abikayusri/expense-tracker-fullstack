package com.abika.expense.application;

import com.abika.expense.application.fake.InMemoryCategoryRepository;
import com.abika.expense.application.port.in.CreateCategoryCommand;
import com.abika.expense.application.port.in.UpdateCategoryCommand;
import com.abika.expense.application.service.CategoryService;
import com.abika.expense.domain.Category;
import com.abika.expense.domain.CategoryType;
import com.abika.expense.domain.exception.ConflictException;
import com.abika.expense.domain.exception.NotFoundException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CategoryServiceTest {

    private static final Long ALICE = 1L;
    private static final Long BOB = 2L;

    private final InMemoryCategoryRepository categories = new InMemoryCategoryRepository();
    private final Set<Long> categoriesInUse = new HashSet<>();
    private final List<Long> clearedAlerts = new ArrayList<>();
    private final CategoryService service =
            new CategoryService(categories, categoriesInUse::contains, clearedAlerts::add);

    private Category create(Long userId, String name, CategoryType type, Long budget) {
        return service.create(new CreateCategoryCommand(userId, name, type, budget));
    }

    @Test
    void create_duplicate_name_same_type_case_insensitive_conflicts() {
        create(ALICE, "Makan", CategoryType.EXPENSE, null);

        assertThatThrownBy(() -> create(ALICE, "  makan ", CategoryType.EXPENSE, null))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void same_name_different_type_or_user_allowed() {
        create(ALICE, "Bonus", CategoryType.EXPENSE, null);

        create(ALICE, "Bonus", CategoryType.INCOME, null);
        create(BOB, "Bonus", CategoryType.EXPENSE, null);

        assertThat(categories.all()).hasSize(3);
    }

    @Test
    void update_changes_name_and_budget_but_keeps_type() {
        Category makan = create(ALICE, "Makan", CategoryType.EXPENSE, 1_000_000L);

        Category updated = service.update(new UpdateCategoryCommand(ALICE, makan.id(), "Jajan", 500_000L));

        assertThat(updated).isEqualTo(new Category(makan.id(), ALICE, "Jajan", CategoryType.EXPENSE, 500_000L));
    }

    @Test
    void update_keeping_same_name_is_not_a_conflict() {
        Category makan = create(ALICE, "Makan", CategoryType.EXPENSE, 1_000_000L);

        Category updated = service.update(new UpdateCategoryCommand(ALICE, makan.id(), "MAKAN", 2_000_000L));

        assertThat(updated.monthlyBudget()).isEqualTo(2_000_000L);
    }

    @Test
    void update_to_existing_name_conflicts() {
        create(ALICE, "Makan", CategoryType.EXPENSE, null);
        Category jajan = create(ALICE, "Jajan", CategoryType.EXPENSE, null);

        assertThatThrownBy(() -> service.update(new UpdateCategoryCommand(ALICE, jajan.id(), "makan", null)))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void update_other_users_category_not_found() {
        Category makan = create(ALICE, "Makan", CategoryType.EXPENSE, null);

        assertThatThrownBy(() -> service.update(new UpdateCategoryCommand(BOB, makan.id(), "Punya Bob", null)))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void delete_used_category_conflicts() {
        Category makan = create(ALICE, "Makan", CategoryType.EXPENSE, null);
        categoriesInUse.add(makan.id());

        assertThatThrownBy(() -> service.delete(ALICE, makan.id())).isInstanceOf(ConflictException.class);
        assertThat(categories.all()).hasSize(1);
        assertThat(clearedAlerts).isEmpty();
    }

    @Test
    void delete_clears_budget_alerts_and_removes_category() {
        Category makan = create(ALICE, "Makan", CategoryType.EXPENSE, null);

        service.delete(ALICE, makan.id());

        assertThat(clearedAlerts).containsExactly(makan.id());
        assertThat(categories.all()).isEmpty();
    }

    @Test
    void delete_other_users_category_not_found() {
        Category makan = create(ALICE, "Makan", CategoryType.EXPENSE, null);

        assertThatThrownBy(() -> service.delete(BOB, makan.id())).isInstanceOf(NotFoundException.class);
    }

    @Test
    void list_filters_by_user_and_type() {
        create(ALICE, "Makan", CategoryType.EXPENSE, null);
        create(ALICE, "Gaji", CategoryType.INCOME, null);
        create(BOB, "Bensin", CategoryType.EXPENSE, null);

        assertThat(service.list(ALICE, null)).extracting(Category::name).containsExactly("Gaji", "Makan");
        assertThat(service.list(ALICE, CategoryType.INCOME)).extracting(Category::name).containsExactly("Gaji");
    }
}
