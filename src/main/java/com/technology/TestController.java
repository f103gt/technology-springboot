package com.technology;

import com.technology.order.controllers.SSEController;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Log4j2
public class TestController {
/*
   @GetMapping("/trigger-notification")
    public void triggerSocket() throws IOException {
        String employeeUUID = "123";
       log.atInfo().log("The message triggered for client " + employeeUUID);
       SSEController.sendNotification(employeeUUID,"hello");
       log.atInfo().log("The message triggered for client " + employeeUUID + " is sent");
    }*/

    /*@AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class Message{
        private String employeeEmail;
        private String email;
    }*/
}
