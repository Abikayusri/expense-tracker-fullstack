package com.abika.expense.adapter.in.web;

import com.abika.expense.application.port.in.CategoryUseCase;
import com.abika.expense.application.port.in.CreateCategoryCommand;
import com.abika.expense.application.port.in.UpdateCategoryCommand;
import com.abika.expense.domain.CategoryType;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
class CategoryController {

    private final CategoryUseCase categories;

    CategoryController(CategoryUseCase categories) {
        this.categories = categories;
    }

    @GetMapping
    List<CategoryResponse> list(@AuthenticationPrincipal Jwt jwt, @RequestParam(required = false) CategoryType type) {
        return categories.list(CurrentUser.id(jwt), type).stream().map(CategoryResponse::from).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CategoryResponse create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CategoryRequest request) {
        return CategoryResponse.from(categories.create(new CreateCategoryCommand(
                CurrentUser.id(jwt), request.name(), request.type(), request.monthlyBudget())));
    }

    @PutMapping("/{id}")
    CategoryResponse update(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id,
                            @Valid @RequestBody CategoryUpdateRequest request) {
        return CategoryResponse.from(categories.update(new UpdateCategoryCommand(
                CurrentUser.id(jwt), id, request.name(), request.monthlyBudget())));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        categories.delete(CurrentUser.id(jwt), id);
    }
}
