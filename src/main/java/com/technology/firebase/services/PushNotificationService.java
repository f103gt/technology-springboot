package com.technology.firebase.services;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class PushNotificationService {
    private final FirebaseMessaging firebaseMessaging;
    public void sendNotificationToToken(String token, String title, String body) throws FirebaseMessagingException {
        // Create a message object with the token and the notification payload
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();
        // Send the message to the FCM endpoint
        String response = firebaseMessaging.send(message);
        // Handle the response
        log.info("Message sent to token " + token + ": " + response);
    }
}
