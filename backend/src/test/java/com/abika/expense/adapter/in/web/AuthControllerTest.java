package com.abika.expense.adapter.in.web;

import com.abika.expense.TestcontainersConfiguration;
import com.abika.expense.adapter.out.security.JwtTokenIssuer;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class AuthControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    JwtEncoder jwtEncoder;

    @BeforeEach
    void cleanDatabase() {
        jdbc.execute("TRUNCATE users RESTART IDENTITY CASCADE");
    }

    private ResultActions register(String email, String password) throws Exception {
        return mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email":"%s","password":"%s","name":"Budi"}""".formatted(email, password)));
    }

    private String tokenFrom(ResultActions result) throws Exception {
        return JsonPath.read(result.andReturn().getResponse().getContentAsString(), "$.token");
    }

    @Test
    void register_returns_201_and_token_works_for_me() throws Exception {
        String token = tokenFrom(register("Budi@Mail.com", "rahasia123").andExpect(status().isCreated()));

        mvc.perform(get("/api/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("budi@mail.com"))
                .andExpect(jsonPath("$.name").value("Budi"));
    }

    @Test
    void login_returns_token() throws Exception {
        register("budi@mail.com", "rahasia123");

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"BUDI@mail.com","password":"rahasia123"}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    void register_short_password_returns_400_with_field_error() throws Exception {
        register("budi@mail.com", "1234567")
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.errors.password").exists());
    }

    @Test
    void register_invalid_email_returns_400() throws Exception {
        register("bukan-email", "rahasia123")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").exists());
    }

    @Test
    void register_duplicate_returns_409() throws Exception {
        register("budi@mail.com", "rahasia123");

        register("BUDI@mail.com", "rahasia123")
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    void login_wrong_password_returns_401_problem() throws Exception {
        register("budi@mail.com", "rahasia123");

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"budi@mail.com","password":"salah12345"}"""))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Email atau password salah"));
    }

    @Test
    void me_without_token_returns_401_problem_json() throws Exception {
        mvc.perform(get("/api/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void me_with_malformed_token_returns_401_problem_json() throws Exception {
        mvc.perform(get("/api/me").header("Authorization", "Bearer abc.def.ghi"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void me_with_expired_token_returns_401() throws Exception {
        Clock eightDaysAgo = Clock.fixed(Instant.now().minus(Duration.ofDays(8)), ZoneOffset.UTC);
        String expired = new JwtTokenIssuer(jwtEncoder, Duration.ofDays(7), eightDaysAgo).issue(1L);

        mvc.perform(get("/api/me").header("Authorization", "Bearer " + expired))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void me_for_deleted_user_returns_404() throws Exception {
        String token = tokenFrom(register("budi@mail.com", "rahasia123"));
        jdbc.execute("TRUNCATE users RESTART IDENTITY CASCADE");

        mvc.perform(get("/api/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
