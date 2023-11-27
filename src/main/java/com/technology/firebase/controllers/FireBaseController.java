package com.technology.firebase.controllers;

import com.technology.firebase.services.PushNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class FireBaseController {
    private final PushNotificationService pushNotificationService;

    @PostMapping("/subscribe")
    public ResponseEntity<String> sendNotificationToToken(@RequestBody String token) {
        //TODO SAVE THE TOKEN
        //TODO MAKE THE METHOD ASYNCHRONOUS TO OPTIMIZE THE PROCESS
        //TODO HOW LONG CAN BE THE TOKEN
        /*try {
            pushNotificationService.sendNotificationToToken(token, title, body);
            return ResponseEntity.ok("Notification sent to token " + token);
        } catch (FirebaseMessagingException e) {
            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending notification to token " + token + ": " + e.getMessage());
        }*/
        return null;
    }
}
