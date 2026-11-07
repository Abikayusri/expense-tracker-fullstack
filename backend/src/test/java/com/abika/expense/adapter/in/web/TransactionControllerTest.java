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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class TransactionControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    JdbcTemplate jdbc;

    private ApiTestClient alice;
    private ApiTestClient bob;
    private Long aliceMakan;

    @BeforeEach
    void setUp() throws Exception {
        jdbc.execute("TRUNCATE users RESTART IDENTITY CASCADE");
        alice = ApiTestClient.registerNew(mvc, "alice@mail.com");
        bob = ApiTestClient.registerNew(mvc, "bob@mail.com");
        aliceMakan = ApiTestClient.idFrom(alice.post("/api/categories", """
                {"name":"Makan","type":"EXPENSE","monthlyBudget":1000000}"""));
    }

    private String body(Long categoryId, long amount, String date) {
        return """
                {"categoryId":%d,"amount":%d,"date":"%s","note":"Nasi goreng"}""".formatted(categoryId, amount, date);
    }

    @Test
    void crud_flow() throws Exception {
        Long id = ApiTestClient.idFrom(alice.post("/api/transactions", body(aliceMakan, 25000, "2026-11-10"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(25000))
                .andExpect(jsonPath("$.date").value("2026-11-10"))
                .andExpect(jsonPath("$.note").value("Nasi goreng")));

        alice.put("/api/transactions/" + id, body(aliceMakan, 30000, "2026-11-11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(30000))
                .andExpect(jsonPath("$.date").value("2026-11-11"));

        alice.get("/api/transactions?month=2026-11").andExpect(jsonPath("$.content", hasSize(1)));

        alice.delete("/api/transactions/" + id).andExpect(status().isNoContent());
        alice.get("/api/transactions?month=2026-11").andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void pagination() throws Exception {
        for (int day = 1; day <= 25; day++) {
            alice.post("/api/transactions", body(aliceMakan, 1000, "2026-11-%02d".formatted(day)));
        }

        alice.get("/api/transactions?month=2026-11&size=20")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(20)))
                .andExpect(jsonPath("$.content[0].date").value("2026-11-25"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(25));
        alice.get("/api/transactions?month=2026-11&size=20&page=1")
                .andExpect(jsonPath("$.content", hasSize(5)));
    }

    @Test
    void month_invalid_returns_400_problem() throws Exception {
        for (String month : new String[]{"2026-13", "abc"}) {
            alice.get("/api/transactions?month=" + month)
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
        }
    }

    @Test
    void month_missing_returns_400() throws Exception {
        alice.get("/api/transactions").andExpect(status().isBadRequest());
    }

    @Test
    void size_out_of_range_returns_400() throws Exception {
        alice.get("/api/transactions?month=2026-11&size=101").andExpect(status().isBadRequest());
        alice.get("/api/transactions?month=2026-11&size=0").andExpect(status().isBadRequest());
        alice.get("/api/transactions?month=2026-11&page=-1").andExpect(status().isBadRequest());
    }

    @Test
    void invalid_body_returns_field_errors() throws Exception {
        alice.post("/api/transactions", """
                        {"categoryId":%d,"amount":0,"note":"%s"}""".formatted(aliceMakan, "x".repeat(256)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.amount").exists())
                .andExpect(jsonPath("$.errors.date").exists())
                .andExpect(jsonPath("$.errors.note").exists());
    }

    @Test
    void user_b_cannot_access_user_a_transaction() throws Exception {
        Long aliceTx = ApiTestClient.idFrom(alice.post("/api/transactions", body(aliceMakan, 25000, "2026-11-10")));
        Long bobMakan = ApiTestClient.idFrom(bob.post("/api/categories", """
                {"name":"Makan","type":"EXPENSE"}"""));

        bob.get("/api/transactions?month=2026-11").andExpect(jsonPath("$.totalElements").value(0));
        bob.put("/api/transactions/" + aliceTx, body(bobMakan, 1, "2026-11-10")).andExpect(status().isNotFound());
        bob.delete("/api/transactions/" + aliceTx).andExpect(status().isNotFound());
        bob.post("/api/transactions", body(aliceMakan, 1, "2026-11-10")).andExpect(status().isNotFound());

        alice.get("/api/transactions?month=2026-11").andExpect(jsonPath("$.content[0].amount").value(25000));
    }

    @Test
    void delete_category_in_use_returns_409() throws Exception {
        alice.post("/api/transactions", body(aliceMakan, 25000, "2026-11-10"));

        alice.delete("/api/categories/" + aliceMakan)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Kategori masih dipakai transaksi"));
    }

    @Test
    void filter_by_category() throws Exception {
        Long gaji = ApiTestClient.idFrom(alice.post("/api/categories", """
                {"name":"Gaji","type":"INCOME"}"""));
        alice.post("/api/transactions", body(aliceMakan, 25000, "2026-11-10"));
        alice.post("/api/transactions", body(gaji, 5000000, "2026-11-01"));

        alice.get("/api/transactions?month=2026-11&categoryId=" + gaji)
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].amount").value(5000000));
    }
}
