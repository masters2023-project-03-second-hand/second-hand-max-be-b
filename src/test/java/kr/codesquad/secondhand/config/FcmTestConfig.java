package kr.codesquad.secondhand.config;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import kr.codesquad.secondhand.infrastructure.fcm.MockGoogleCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;

@ActiveProfiles("test")
@Configuration
public class FcmTestConfig {

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        List<FirebaseApp> apps = FirebaseApp.getApps();

        return apps.stream()
                .filter(app -> app.getName().equals(FirebaseApp.DEFAULT_APP_NAME))
                .map(FirebaseMessaging::getInstance)
                .findFirst()
                .orElseGet(this::createFirebaseMessaging);
    }

    private FirebaseMessaging createFirebaseMessaging() {
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(new MockGoogleCredentials("test-token"))
                .setProjectId("test-project-id")
                .build();

        return FirebaseMessaging.getInstance(FirebaseApp.initializeApp(options));
    }
}
