package com.technology.order.controllers;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//@RestController
@Log4j2
public class SSEController {
    private static final Map<String, SseEmitter> clients =
            new ConcurrentHashMap<>();

    //@GetMapping ("/subscribe")
    public SseEmitter subscribe(@RequestParam("clientId") String clientId) throws IOException {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        log.atInfo().log("The emitter is created " + emitter);
        emitter.send(SseEmitter.event().name("notification").data("connected"));
        clients.put(clientId, emitter);
        log.atInfo().log("The client assigned " + clientId);
        emitter.onCompletion(() -> {
            clients.remove(clientId, emitter);
            log.atInfo().log("the data for " + clientId + " and emitter " + emitter + " cleaned");
        });
        return emitter;
    }

    //@PostMapping("/subscribe/get-notification")
    public static void sendNotification(String clientId,String message) throws IOException {
        SseEmitter emitter = clients.get(clientId);
        log.atInfo().log("The emitter " + emitter + " for client " + clientId + " obtained");
        if (emitter != null) {
            emitter.send(SseEmitter.event().name("notification").data(message));
            log.atInfo().log("The message " + message + " from emitter " + emitter + " sent to client " + clientId);
        }
    }
}
