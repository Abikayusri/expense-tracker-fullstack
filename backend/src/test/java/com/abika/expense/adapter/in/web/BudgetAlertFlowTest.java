package com.abika.expense.adapter.in.web;

import com.abika.expense.TestcontainersConfiguration;
import com.abika.expense.application.fake.RecordingPushSender;
import com.abika.expense.application.port.out.PushSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import({TestcontainersConfiguration.class, BudgetAlertFlowTest.RecordingPushConfig.class})
class BudgetAlertFlowTest {

    @TestConfiguration(proxyBeanMethods = false)
    static class RecordingPushConfig {

        @Bean
        @Primary
        RecordingPushSender recordingPushSender() {
            return new RecordingPushSender();
        }
    }

    @Autowired
    MockMvc mvc;

    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    RecordingPushSender push;

    @Autowired
    PushSender activeSender;

    private ApiTestClient alice;
    private Long makan;

    @BeforeEach
    void setUp() throws Exception {
        jdbc.execute("TRUNCATE users RESTART IDENTITY CASCADE");
        push.clear();
        alice = ApiTestClient.registerNew(mvc, "alice@mail.com");
        alice.post("/api/devices", """
                {"fcmToken":"phone-1","platform":"ANDROID"}""");
        makan = ApiTestClient.idFrom(alice.post("/api/categories", """
                {"name":"Makan","type":"EXPENSE","monthlyBudget":1000000}"""));
    }

    private void spend(long amount, String date) throws Exception {
        alice.post("/api/transactions", """
                        {"categoryId":%d,"amount":%d,"date":"%s"}""".formatted(makan, amount, date))
                .andExpect(status().isCreated());
    }

    @Test
    void over_budget_sends_one_push_per_month() throws Exception {
        assertThat(activeSender).isSameAs(push);

        spend(600_000, "2026-11-01");
        assertThat(push.sent()).isEmpty();

        spend(500_000, "2026-11-02");
        spend(100_000, "2026-11-03");

        assertThat(push.sent()).singleElement().satisfies(p -> {
            assertThat(p.tokens()).containsExactly("phone-1");
            assertThat(p.body()).isEqualTo("Pengeluaran Makan bulan ini Rp1.100.000 melebihi budget Rp1.000.000");
        });
    }

    @Test
    void deleting_category_clears_its_alerts() throws Exception {
        spend(1_500_000, "2026-11-01");
        jdbc.update("DELETE FROM transactions");

        alice.delete("/api/categories/" + makan).andExpect(status().isNoContent());

        assertThat(jdbc.queryForObject("SELECT count(*) FROM budget_alerts", Integer.class)).isZero();
    }
}
