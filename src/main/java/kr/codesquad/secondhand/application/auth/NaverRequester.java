package kr.codesquad.secondhand.application.auth;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import kr.codesquad.secondhand.domain.member.UserProfile;
import kr.codesquad.secondhand.exception.ErrorCode;
import kr.codesquad.secondhand.exception.InternalServerException;
import kr.codesquad.secondhand.infrastructure.properties.OauthProperties;
import kr.codesquad.secondhand.infrastructure.properties.OauthProperties.Naver;
import kr.codesquad.secondhand.presentation.dto.OauthTokenResponse;
import org.springframework.beans.factory.annotation.Value;
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

@Component
public class NaverRequester implements OAuthRequester {

    private final RestTemplate restTemplate;
    private final Naver naver;
    private final String defaultProfileImage;

    public NaverRequester(RestTemplate restTemplate, OauthProperties oauthProperties,
                          @Value("${custom.default-profile}") String defaultProfileImage) {
        this.restTemplate = restTemplate;
        this.naver = oauthProperties.getNaver();
        this.defaultProfileImage = defaultProfileImage;
    }

    public OauthTokenResponse getToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(naver.getUser().getClientId(), naver.getUser().getClientSecret());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(tokenRequest(code), headers);

        Map<String, Object> response = restTemplate.postForObject(naver.getProvider().getTokenUrl(),
                request,
                Map.class);

        validateToken(response);

        return new OauthTokenResponse(response.get("access_token").toString(),
                null,
                response.get("token_type").toString());
    }

    private void validateToken(Map<String, Object> tokenResponse) {
        if (!tokenResponse.containsKey("access_token")) {
            throw new InternalServerException(
                    ErrorCode.OAUTH_FAIL_REQUEST_TOKEN,
                    tokenResponse.get("error_description").toString()
            );
        }
    }

    private MultiValueMap<String, String> tokenRequest(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", naver.getUser().getRedirectUrl());
        return formData;
    }

    public UserProfile getUserProfile(OauthTokenResponse tokenResponse) {
        Map<String, Object> responseAttributes = getUserAttributes(tokenResponse);
        Map<String, Object> userAttributes = (Map<String, Object>) responseAttributes.get("response");
        return UserProfile.builder()
                .email((String) userAttributes.get("email"))
                .profileUrl(defaultProfileImage)
                .build();
    }

    private Map<String, Object> getUserAttributes(OauthTokenResponse tokenResponse) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenResponse.getAccessToken());
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                naver.getProvider().getUserInfoUrl(),
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<Map<String, Object>>() {
                }
        );
        return response.getBody();
    }
}
