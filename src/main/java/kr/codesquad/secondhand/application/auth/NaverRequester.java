package kr.codesquad.secondhand.application.auth;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import kr.codesquad.secondhand.infrastructure.OauthProvider;
import kr.codesquad.secondhand.presentation.dto.OauthTokenResponse;
import kr.codesquad.secondhand.presentation.dto.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Component
public class NaverRequester {

    private final RestTemplate restTemplate;
    private final OauthProvider oauthProvider;

    public OauthTokenResponse getToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(oauthProvider.getClientId(), oauthProvider.getClientSecret());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(tokenRequest(code), headers);

        ResponseEntity<OauthTokenResponse> response = restTemplate.exchange(
                oauthProvider.getTokenUrl(),
                HttpMethod.POST,
                request,
                OauthTokenResponse.class
        );
        return response.getBody();
    }

    private MultiValueMap<String, String> tokenRequest(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", oauthProvider.getRedirectUrl());
        return formData;
    }

    public UserProfile getUserProfile(OauthTokenResponse tokenResponse) {
        Map<String, Object> responseAttributes = getUserAttributes(tokenResponse);
        Map<String, Object> userAttributes = (Map<String, Object>) responseAttributes.get("response");
        return UserProfile.builder()
                .email((String) userAttributes.get("email"))
                .profileUrl((String) userAttributes.get("profile_image"))
                .build();
    }

    private Map<String, Object> getUserAttributes(OauthTokenResponse tokenResponse) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenResponse.getAccessToken());
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                oauthProvider.getUserInfoUrl(),
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<Map<String, Object>>() {
                }
        );
        return response.getBody();
    }
}
