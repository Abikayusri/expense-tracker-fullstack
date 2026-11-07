package com.abika.expense.application.port.out;

import java.util.List;

public interface PushSender {

    void send(List<String> fcmTokens, String title, String body);
}
