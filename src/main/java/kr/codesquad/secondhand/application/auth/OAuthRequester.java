package kr.codesquad.secondhand.application.auth;

import kr.codesquad.secondhand.domain.member.UserProfile;
import kr.codesquad.secondhand.presentation.dto.OauthTokenResponse;

public interface OAuthRequester {

    OauthTokenResponse getToken(String code);

    UserProfile getUserProfile(OauthTokenResponse tokenResponse);
}
