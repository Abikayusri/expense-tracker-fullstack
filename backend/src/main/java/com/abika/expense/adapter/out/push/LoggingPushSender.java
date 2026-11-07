package com.abika.expense.adapter.out.push;

import com.abika.expense.application.port.out.PushSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LoggingPushSender implements PushSender {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingPushSender.class);

    @Override
    public void send(List<String> fcmTokens, String title, String body) {
        LOG.info("Push (not sent, Firebase not configured) to {} device(s): {} - {}", fcmTokens.size(), title, body);
    }
}
