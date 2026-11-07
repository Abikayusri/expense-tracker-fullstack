package com.abika.expense.adapter.in.web;

import com.abika.expense.application.port.in.SaveTransactionCommand;
import com.abika.expense.application.port.in.TransactionQuery;
import com.abika.expense.application.port.in.TransactionUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

import java.time.YearMonth;

@RestController
@RequestMapping("/api/transactions")
class TransactionController {

    private final TransactionUseCase transactions;

    TransactionController(TransactionUseCase transactions) {
        this.transactions = transactions;
    }

    @GetMapping
    PageResponse<TransactionResponse> list(@AuthenticationPrincipal Jwt jwt,
                                           @RequestParam String month,
                                           @RequestParam(required = false) Long categoryId,
                                           @RequestParam(defaultValue = "0") @Min(0) int page,
                                           @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        TransactionQuery query = new TransactionQuery(CurrentUser.id(jwt), YearMonth.parse(month), categoryId, page, size);
        return PageResponse.from(transactions.list(query), TransactionResponse::from);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    TransactionResponse create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody TransactionRequest request) {
        return TransactionResponse.from(transactions.create(toCommand(jwt, request)));
    }

    @PutMapping("/{id}")
    TransactionResponse update(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id,
                               @Valid @RequestBody TransactionRequest request) {
        return TransactionResponse.from(transactions.update(id, toCommand(jwt, request)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        transactions.delete(CurrentUser.id(jwt), id);
    }

    private static SaveTransactionCommand toCommand(Jwt jwt, TransactionRequest request) {
        return new SaveTransactionCommand(CurrentUser.id(jwt), request.categoryId(), request.amount(),
                request.date(), request.note());
    }
}
