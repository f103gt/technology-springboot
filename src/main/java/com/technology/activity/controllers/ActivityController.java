package com.technology.activity.controllers;

import com.technology.activity.models.ActivityStatus;
import com.technology.activity.services.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityService activityService;

    @GetMapping("/staff/set-is-active")
    public ResponseEntity<String> setIsActive() {
        activityService.changActivityStatus(ActivityStatus.PRESENT);
        return ResponseEntity.ok("the user set active");
    }

    @GetMapping("/staff/set-not-active")
    public ResponseEntity<String> setNotActive() {
        activityService.changActivityStatus(ActivityStatus.ABSENT);
        return ResponseEntity.ok("the user set inactive");
    }
}
