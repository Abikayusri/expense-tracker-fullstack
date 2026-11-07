package com.abika.expense.application.fake;

import com.abika.expense.application.port.out.PushSender;

import java.util.ArrayList;
import java.util.List;

public class RecordingPushSender implements PushSender {

    public record Push(List<String> tokens, String title, String body) {
    }

    private final List<Push> sent = new ArrayList<>();

    @Override
    public void send(List<String> fcmTokens, String title, String body) {
        sent.add(new Push(List.copyOf(fcmTokens), title, body));
    }

    public List<Push> sent() {
        return List.copyOf(sent);
    }

    public void clear() {
        sent.clear();
    }
}
