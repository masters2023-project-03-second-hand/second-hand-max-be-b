package kr.codesquad.secondhand.infrastructure;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConfigurationProperties(prefix = "oauth2")
public class OauthProperties {

    private final User user;
    private final Provider provider;

    @ConstructorBinding
    public OauthProperties(User user, Provider provider) {
        this.user = user;
        this.provider = provider;
    }

    @Getter
    @RequiredArgsConstructor
    public static class User {

        private final String clientId;
        private final String clientSecret;
        private final String redirectUrl;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Provider {

        private final String tokenUrl;
        private final String userInfoUrl;
        private final String userNameAttribute;
    }
}
