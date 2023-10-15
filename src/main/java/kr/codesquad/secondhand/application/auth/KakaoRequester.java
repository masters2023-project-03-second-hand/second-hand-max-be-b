package kr.codesquad.secondhand.application.auth;

import java.util.Map;
import kr.codesquad.secondhand.domain.member.UserProfile;
import kr.codesquad.secondhand.infrastructure.properties.OauthProperties;
import kr.codesquad.secondhand.infrastructure.properties.OauthProperties.Kakao;
import kr.codesquad.secondhand.presentation.dto.OauthTokenResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class KakaoRequester implements OAuthRequester {

    private final RestTemplate restTemplate;
    private final Kakao kakao;

    public KakaoRequester(RestTemplate restTemplate, OauthProperties oauthProperties) {
        this.restTemplate = restTemplate;
        this.kakao = oauthProperties.getKakao();
    }

    @Override
    public OauthTokenResponse getToken(String code) {
        String url = UriComponentsBuilder.fromHttpUrl(kakao.getProvider().getTokenUrl())
                .build()
                .toString();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        var request = new HttpEntity<>(createTokenRequestBody(code), httpHeaders);

        Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
        validateToken(response);

        return new OauthTokenResponse(
                response.get("access_token").toString(),
                response.get("scope").toString(),
                response.get("token_type").toString()
        );
    }

    private MultiValueMap<String, String> createTokenRequestBody(final String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakao.getUser().getClientId());
        params.add("redirect_uri", kakao.getUser().getRedirectUrl());
        params.add("code", code);
        params.add("client_secret", kakao.getUser().getClientSecret());
        return params;
    }

    @Override
    public UserProfile getUserProfile(OauthTokenResponse tokenResponse) {
        String url = UriComponentsBuilder.fromHttpUrl(kakao.getProvider().getUserInfoUrl())
                .queryParam("property_keys",
                        "[\"kakao_account.profile\", \"kakao_account.email\", \"kakao_account.name\"]")
                .build()
                .toString();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.setBearerAuth(tokenResponse.getAccessToken());

        HttpEntity<Void> request = new HttpEntity<>(null, httpHeaders);

        Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
        Map<String, Object> kakaoAccount = (Map<String, Object>) response.get("kakao_account");
        String profileUrl = ((Map<String, Object>) kakaoAccount.get("profile")).get("profile_image_url").toString();
        String email = kakaoAccount.get("email").toString();

        return UserProfile.builder()
                .email(email)
                .profileUrl(profileUrl)
                .build();
    }
}
