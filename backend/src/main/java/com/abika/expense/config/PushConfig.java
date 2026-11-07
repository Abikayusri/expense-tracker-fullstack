package com.abika.expense.config;

import com.abika.expense.adapter.out.push.FcmPushSender;
import com.abika.expense.adapter.out.push.LoggingPushSender;
import com.abika.expense.application.port.out.PushSender;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class PushConfig {

    private static final Logger LOG = LoggerFactory.getLogger(PushConfig.class);
    private static final String FIREBASE_APP_NAME = "expense";

    @Bean
    PushSender pushSender(@Value("${app.firebase.credentials-path:}") String credentialsPath) {
        return create(credentialsPath);
    }

    static PushSender create(String credentialsPath) {
        if (credentialsPath == null || credentialsPath.isBlank()) {
            LOG.info("Firebase credentials not set; using LoggingPushSender");
            return new LoggingPushSender();
        }
        Path path = Path.of(credentialsPath);
        if (!Files.isRegularFile(path)) {
            LOG.warn("Firebase credentials file {} not found; using LoggingPushSender", path);
            return new LoggingPushSender();
        }
        try (InputStream in = Files.newInputStream(path)) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(in))
                    .build();
            FirebaseApp app = FirebaseApp.getApps().stream()
                    .filter(a -> a.getName().equals(FIREBASE_APP_NAME))
                    .findFirst()
                    .orElseGet(() -> FirebaseApp.initializeApp(options, FIREBASE_APP_NAME));
            LOG.info("Firebase credentials loaded; using FcmPushSender");
            return new FcmPushSender(FirebaseMessaging.getInstance(app));
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read Firebase credentials at " + path, e);
        }
    }
}
