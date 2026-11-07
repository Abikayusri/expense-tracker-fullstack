package com.abika.expense.config;

import com.abika.expense.adapter.out.push.LoggingPushSender;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PushConfigTest {

    @Test
    void blank_path_uses_logging_sender() {
        assertThat(PushConfig.create("")).isInstanceOf(LoggingPushSender.class);
        assertThat(PushConfig.create(null)).isInstanceOf(LoggingPushSender.class);
    }

    @Test
    void missing_file_uses_logging_sender() {
        assertThat(PushConfig.create("/does/not/exist/firebase.json")).isInstanceOf(LoggingPushSender.class);
    }
}
