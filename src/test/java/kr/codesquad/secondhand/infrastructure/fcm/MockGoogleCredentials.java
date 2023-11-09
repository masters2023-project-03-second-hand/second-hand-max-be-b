package kr.codesquad.secondhand.infrastructure.fcm;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MockGoogleCredentials extends GoogleCredentials {

    private final String token;
    private final long expiredTime;

    public MockGoogleCredentials(String token) {
        this(token, System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1));
    }

    public MockGoogleCredentials(String token, long expiredTime) {
        this.token = token;
        this.expiredTime = expiredTime;
    }

    @Override
    public AccessToken refreshAccessToken() {
        return new AccessToken(token, new Date(expiredTime));
    }
}
