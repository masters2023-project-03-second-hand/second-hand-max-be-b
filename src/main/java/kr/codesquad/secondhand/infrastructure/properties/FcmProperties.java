package kr.codesquad.secondhand.infrastructure.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConfigurationProperties("fcm")
public class FcmProperties {

    private static final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";

    private final String privateKeyPath;
    private final long expirationMillis;
    private final String[] scopes;

    @ConstructorBinding
    public FcmProperties(String privateKeyPath, long expirationMillis) {
        this.privateKeyPath = privateKeyPath;
        this.expirationMillis = expirationMillis;
        this.scopes = new String[]{MESSAGING_SCOPE};
    }
}
