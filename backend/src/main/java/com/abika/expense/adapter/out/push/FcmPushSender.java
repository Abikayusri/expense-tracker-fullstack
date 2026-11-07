package com.abika.expense.adapter.out.push;

import com.abika.expense.application.port.out.PushSender;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FcmPushSender implements PushSender {

    private static final Logger LOG = LoggerFactory.getLogger(FcmPushSender.class);

    private final FirebaseMessaging messaging;

    public FcmPushSender(FirebaseMessaging messaging) {
        this.messaging = messaging;
    }

    @Override
    public void send(List<String> fcmTokens, String title, String body) {
        MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(fcmTokens)
                .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                .build();
        try {
            BatchResponse response = messaging.sendEachForMulticast(message);
            if (response.getFailureCount() > 0) {
                LOG.warn("FCM delivered {} of {} pushes", response.getSuccessCount(), fcmTokens.size());
            }
        } catch (FirebaseMessagingException e) {
            throw new IllegalStateException("FCM request failed", e);
        }
    }
}
