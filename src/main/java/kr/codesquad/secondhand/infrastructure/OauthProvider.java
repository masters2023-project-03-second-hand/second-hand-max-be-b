package kr.codesquad.secondhand.infrastructure;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OauthProvider {

    private final String clientId;
    private final String clientSecret;
    private final String redirectUrl;
    private final String tokenUrl;
    private final String userInfoUrl;

    public OauthProvider(OauthProperty.User user, OauthProperty.Provider provider) {
        this(user.getClientId(), user.getClientSecret(), user.getRedirectUrl(),
                provider.getTokenUrl(), provider.getUserInfoUrl());
    }

    @Builder
    public OauthProvider(String clientId, String clientSecret, String redirectUrl, String tokenUrl,
                         String userInfoUrl) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUrl = redirectUrl;
        this.tokenUrl = tokenUrl;
        this.userInfoUrl = userInfoUrl;
    }

    public static OauthProvider from(OauthProperty property) {
        return new OauthProvider(property.getUser(), property.getProvider());
    }
}
