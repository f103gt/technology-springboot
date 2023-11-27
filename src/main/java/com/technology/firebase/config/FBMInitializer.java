package com.technology.firebase.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class FBMInitializer {
    @Value("${firebase.service-account}")
    private String firebaseProperties;

   /* @PostConstruct
    public void initialize() {
        try {

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials
                            .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())).build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase application has been initialized");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }*/
}
