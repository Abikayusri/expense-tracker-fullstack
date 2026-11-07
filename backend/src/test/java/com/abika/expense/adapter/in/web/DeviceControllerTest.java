package com.abika.expense.adapter.in.web;

import com.abika.expense.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class DeviceControllerTest {

    private static final String PHONE = """
            {"fcmToken":"fcm:abc-123_XYZ","platform":"ANDROID"}""";

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

    private int tokenCount() {
        return jdbc.queryForObject("SELECT count(*) FROM device_tokens", Integer.class);
    }

    @Test
    void register_is_idempotent() throws Exception {
        alice.post("/api/devices", PHONE).andExpect(status().isNoContent());
        alice.post("/api/devices", PHONE).andExpect(status().isNoContent());

        assertThat(tokenCount()).isEqualTo(1);
    }

    @Test
    void unregister_removes_token() throws Exception {
        alice.post("/api/devices", PHONE);

        alice.delete("/api/devices/fcm:abc-123_XYZ").andExpect(status().isNoContent());

        assertThat(tokenCount()).isZero();
    }

    @Test
    void unregister_other_users_token_is_ignored() throws Exception {
        alice.post("/api/devices", PHONE);

        bob.delete("/api/devices/fcm:abc-123_XYZ").andExpect(status().isNoContent());

        assertThat(tokenCount()).isEqualTo(1);
    }

    @Test
    void invalid_body_returns_field_errors() throws Exception {
        alice.post("/api/devices", """
                        {"fcmToken":"  ","platform":"IOS"}""")
                .andExpect(status().isBadRequest());
        alice.post("/api/devices", """
                        {"fcmToken":""}""")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.fcmToken").exists())
                .andExpect(jsonPath("$.errors.platform").exists());
    }
}
