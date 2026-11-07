package com.abika.expense.adapter.in.web;

import com.abika.expense.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class SummaryControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    JdbcTemplate jdbc;

    private ApiTestClient alice;
    private ApiTestClient bob;
    private Long gaji;
    private Long makan;

    @BeforeEach
    void setUp() throws Exception {
        jdbc.execute("TRUNCATE users RESTART IDENTITY CASCADE");
        alice = ApiTestClient.registerNew(mvc, "alice@mail.com");
        bob = ApiTestClient.registerNew(mvc, "bob@mail.com");
        gaji = ApiTestClient.idFrom(alice.post("/api/categories", """
                {"name":"Gaji","type":"INCOME"}"""));
        makan = ApiTestClient.idFrom(alice.post("/api/categories", """
                {"name":"Makan","type":"EXPENSE","monthlyBudget":1000000}"""));
    }

    private void add(ApiTestClient client, Long categoryId, long amount, String date) throws Exception {
        client.post("/api/transactions", """
                        {"categoryId":%d,"amount":%d,"date":"%s"}""".formatted(categoryId, amount, date))
                .andExpect(status().isCreated());
    }

    @Test
    void summary_end_to_end() throws Exception {
        add(alice, gaji, 5_000_000, "2026-11-01");
        add(alice, makan, 1_250_000, "2026-11-05");
        add(alice, makan, 300_000, "2026-10-31");

        alice.get("/api/summary?month=2026-11")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIncome").value(5000000))
                .andExpect(jsonPath("$.totalExpense").value(1250000))
                .andExpect(jsonPath("$.balance").value(3750000))
                .andExpect(jsonPath("$.perCategory", hasSize(2)))
                .andExpect(jsonPath("$.perCategory[1].name").value("Makan"))
                .andExpect(jsonPath("$.perCategory[1].spent").value(1250000))
                .andExpect(jsonPath("$.perCategory[1].budget").value(1000000))
                .andExpect(jsonPath("$.perCategory[1].overBudget").value(true))
                .andExpect(jsonPath("$.perCategory[0].budget").doesNotExist());
    }

    @Test
    void empty_month_returns_zeros_and_all_categories() throws Exception {
        alice.get("/api/summary?month=2025-01")
                .andExpect(jsonPath("$.totalIncome").value(0))
                .andExpect(jsonPath("$.totalExpense").value(0))
                .andExpect(jsonPath("$.balance").value(0))
                .andExpect(jsonPath("$.perCategory", hasSize(2)))
                .andExpect(jsonPath("$.perCategory[0].spent").value(0))
                .andExpect(jsonPath("$.perCategory[1].overBudget").value(false));
    }

    @Test
    void invalid_month_returns_400() throws Exception {
        alice.get("/api/summary?month=2026-13")
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
        alice.get("/api/summary").andExpect(status().isBadRequest());
    }

    @Test
    void summary_only_counts_own_data() throws Exception {
        Long bobMakan = ApiTestClient.idFrom(bob.post("/api/categories", """
                {"name":"Makan","type":"EXPENSE"}"""));
        add(bob, bobMakan, 7_000_000, "2026-11-05");
        add(alice, makan, 100_000, "2026-11-05");

        alice.get("/api/summary?month=2026-11")
                .andExpect(jsonPath("$.totalExpense").value(100000))
                .andExpect(jsonPath("$.perCategory", hasSize(2)));
        bob.get("/api/summary?month=2026-11")
                .andExpect(jsonPath("$.totalExpense").value(7000000))
                .andExpect(jsonPath("$.perCategory", hasSize(1)));
    }

    @Test
    void requires_token() throws Exception {
        mvc.perform(get("/api/summary?month=2026-11")).andExpect(status().isUnauthorized());
    }
}
