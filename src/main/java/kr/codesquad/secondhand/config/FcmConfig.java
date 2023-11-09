package kr.codesquad.secondhand.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import kr.codesquad.secondhand.exception.ErrorCode;
import kr.codesquad.secondhand.exception.InternalServerException;
import kr.codesquad.secondhand.infrastructure.properties.FcmProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Profile("!test")
@Configuration
public class FcmConfig {

    private final FcmProperties fcmProperties;

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        List<FirebaseApp> apps = FirebaseApp.getApps();

        try (FileInputStream refreshToken = new FileInputStream(fcmProperties.getPrivateKeyPath())) {
            return apps.stream()
                    .filter(app -> app.getName().equals(FirebaseApp.DEFAULT_APP_NAME))
                    .map(FirebaseMessaging::getInstance)
                    .findFirst()
                    .orElseGet(() -> createFirebaseMessaging(refreshToken));
        } catch (IOException e) {
            throw new InternalServerException(ErrorCode.FIREBASE_CONFIG_ERROR, "Firebase 설정 파일을 읽어올 수 없습니다.");
        }
    }

    private FirebaseMessaging createFirebaseMessaging(FileInputStream refreshToken) {
        try {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(refreshToken))
                    .build();

            return FirebaseMessaging.getInstance(FirebaseApp.initializeApp(options));
        } catch (IOException e) {
            throw new InternalServerException(ErrorCode.FIREBASE_CONFIG_ERROR, "Firebase 설정 파일을 읽어올 수 없습니다.");
        }
    }
}
