package kr.codesquad.secondhand.infrastructure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "oauth2")
public class OauthProperty {

    private User user = new User();
    private Provider provider = new Provider();

    @Getter
    @Setter
    public static class User {

        private String clientId;
        private String clientSecret;
        private String redirectUrl;
    }

    @Getter
    @Setter
    public static class Provider {

        private String tokenUrl;
        private String userInfoUrl;
        private String userNameAttribute;
    }
}
