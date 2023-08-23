package kr.codesquad.secondhand.application;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import kr.codesquad.secondhand.infrastructure.OauthProvider;
import kr.codesquad.secondhand.presentation.dto.LoginResponse;
import kr.codesquad.secondhand.presentation.dto.OauthTokenResponse;
import kr.codesquad.secondhand.presentation.dto.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final OauthProvider oauthProvider;

    public LoginResponse login(String code) {
        OauthTokenResponse tokenResponse = getToken(code);
        UserProfile userProfile = getUserProfile(tokenResponse);
        // loginId로 db에서 가져온 email과 userProfile의 email이 같은지 검증
        // 애플리케이션의 Jwt토큰 만들어서 반환
        return new LoginResponse("he2joo", "1234");
    }

    private OauthTokenResponse getToken(String code) {
        return WebClient.create()
                .post()
                .uri(oauthProvider.getTokenUrl())
                .headers(header -> {
                    header.setBasicAuth(oauthProvider.getClientId(), oauthProvider.getClientSecret());
                    header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    header.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                    header.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
                })
                .bodyValue(tokenRequest(code))
                .retrieve()
                .bodyToMono(OauthTokenResponse.class)
                .block();
    }

    private MultiValueMap<String, String> tokenRequest(String code) {
        MultiValueMap<String, String> formData= new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", oauthProvider.getRedirectUrl());
        return formData;
    }

    private UserProfile getUserProfile(OauthTokenResponse tokenResponse) {
        Map<String, Object> userAttributes = getUserAttributes(tokenResponse);
        return UserProfile.builder()
                .email((String) userAttributes.get("email"))
                .profileUrl((String) userAttributes.get("profile_image"))
                .build();
    }

    private Map<String, Object> getUserAttributes(OauthTokenResponse tokenResponse) {
        return WebClient.create()
                .get()
                .uri(oauthProvider.getUserInfoUrl())
                .headers(header -> header.setBearerAuth(tokenResponse.getAccessToken()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }
}
