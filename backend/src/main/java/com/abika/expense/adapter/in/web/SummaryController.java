package com.abika.expense.adapter.in.web;

import com.abika.expense.application.port.in.SummaryUseCase;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/summary")
class SummaryController {

    private final SummaryUseCase summaries;

    SummaryController(SummaryUseCase summaries) {
        this.summaries = summaries;
    }

    @GetMapping
    SummaryResponse get(@AuthenticationPrincipal Jwt jwt, @RequestParam String month) {
        return SummaryResponse.from(summaries.get(CurrentUser.id(jwt), YearMonth.parse(month)));
    }
}
