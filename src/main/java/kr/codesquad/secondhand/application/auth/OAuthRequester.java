package kr.codesquad.secondhand.application.auth;

import java.util.Map;
import kr.codesquad.secondhand.domain.member.UserProfile;
import kr.codesquad.secondhand.exception.ErrorCode;
import kr.codesquad.secondhand.exception.InternalServerException;
import kr.codesquad.secondhand.presentation.dto.OauthTokenResponse;

public interface OAuthRequester {

    OauthTokenResponse getToken(String code);

    UserProfile getUserProfile(OauthTokenResponse tokenResponse);

    default void validateToken(Map<String, Object> tokenResponse) {
        if (!tokenResponse.containsKey("access_token")) {
            throw new InternalServerException(
                    ErrorCode.OAUTH_FAIL_REQUEST_TOKEN,
                    tokenResponse.get("error_description").toString()
            );
        }
    }
}
