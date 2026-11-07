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

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class CategoryControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    JdbcTemplate jdbc;

    private ApiTestClient alice;
    private ApiTestClient bob;

    @BeforeEach
    void setUp() throws Exception {
        jdbc.execute("TRUNCATE users RESTART IDENTITY CASCADE");
        alice = ApiTestClient.registerNew(mvc, "alice@mail.com");
        bob = ApiTestClient.registerNew(mvc, "bob@mail.com");
    }

    private Long createMakan(ApiTestClient client) throws Exception {
        return ApiTestClient.idFrom(client.post("/api/categories", """
                {"name":"Makan","type":"EXPENSE","monthlyBudget":1000000}"""));
    }

    @Test
    void crud_flow() throws Exception {
        Long id = ApiTestClient.idFrom(alice.post("/api/categories", """
                        {"name":"Makan","type":"EXPENSE","monthlyBudget":1000000}""")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Makan"))
                .andExpect(jsonPath("$.type").value("EXPENSE"))
                .andExpect(jsonPath("$.monthlyBudget").value(1000000)));

        alice.get("/api/categories").andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));

        alice.put("/api/categories/" + id, """
                        {"name":"Makan","monthlyBudget":1500000}""")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthlyBudget").value(1500000))
                .andExpect(jsonPath("$.type").value("EXPENSE"));

        alice.delete("/api/categories/" + id).andExpect(status().isNoContent());
        alice.get("/api/categories").andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void user_b_cannot_see_or_touch_user_a_category() throws Exception {
        Long aliceCategory = createMakan(alice);

        bob.get("/api/categories").andExpect(jsonPath("$", hasSize(0)));
        bob.put("/api/categories/" + aliceCategory, """
                        {"name":"Punya Bob","monthlyBudget":null}""")
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
        bob.delete("/api/categories/" + aliceCategory).andExpect(status().isNotFound());

        alice.get("/api/categories").andExpect(jsonPath("$[0].name").value("Makan"));
    }

    @Test
    void income_with_budget_returns_400() throws Exception {
        alice.post("/api/categories", """
                        {"name":"Gaji","type":"INCOME","monthlyBudget":1000}""")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Budget hanya untuk kategori pengeluaran"));
    }

    @Test
    void missing_name_and_type_return_field_errors() throws Exception {
        alice.post("/api/categories", "{}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.type").exists());
    }

    @Test
    void duplicate_name_returns_409() throws Exception {
        createMakan(alice);

        alice.post("/api/categories", """
                        {"name":"makan","type":"EXPENSE"}""")
                .andExpect(status().isConflict());
    }

    @Test
    void filter_by_type() throws Exception {
        createMakan(alice);
        alice.post("/api/categories", """
                {"name":"Gaji","type":"INCOME"}""");

        alice.get("/api/categories?type=INCOME")
                .andExpect(jsonPath("$[*].name", contains("Gaji")));
    }

    @Test
    void invalid_type_filter_returns_400() throws Exception {
        alice.get("/api/categories?type=SALARY").andExpect(status().isBadRequest());
    }

    @Test
    void requires_token() throws Exception {
        mvc.perform(get("/api/categories")).andExpect(status().isUnauthorized());
    }
}
