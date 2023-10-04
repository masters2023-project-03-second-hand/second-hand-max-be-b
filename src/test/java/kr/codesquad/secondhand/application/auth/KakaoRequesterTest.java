package kr.codesquad.secondhand.application.auth;

import kr.codesquad.secondhand.domain.member.UserProfile;
import kr.codesquad.secondhand.exception.ErrorCode;
import kr.codesquad.secondhand.exception.InternalServerException;
import kr.codesquad.secondhand.infrastructure.properties.OauthProperties;
import kr.codesquad.secondhand.presentation.dto.OauthTokenResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class KakaoRequesterTest {

    private static final String TOKEN_RESPONSE = "{\n"
            + "    \"access_token\": \"AAAAQosjWDJieBiQZc3to9YQp6HDLvrmyKC+6+iZ3gq7qrkqf50ljZC+Lgoqrg\",\n"
            + "    \"refresh_token\": \"c8ceMEJisO4Se7uGisHoX0f5JEii7JnipglQipkOn5Zp3tyP7dHQoP0zNKHUq2gY\",\n"
            + "    \"token_type\": \"bearer\",\n"
            + "    \"expires_in\": \"3600\",\n"
            + "    \"refresh_token_expires_in\": \"5184000\",\n"
            + "    \"scope\": \"account_email profile\"\n"
            + "}";
    private static final String USER_RESPONSE = "{\n"
            + "  \"id\": \"00\",\n"
            + "  \"connected_at\": \"2023-09-22T13:37:33Z\",\n"
            + "  \"kakao_account\": {\n"
            + "    \"profile_nickname_needs_agreement\": false,\n"
            + "    \"profile_image_needs_agreement\": false,\n"
            + "    \"profile\": {\n"
            + "      \"nickname\":\"홍길동\",\n"
            + "      \"thumbnail_image_url\":\"http://yyy.kakao.com/.../aaa.jpg\",\n"
            + "      \"profile_image_url\":\"http://yyy.kakao.com/dn/.../bbb.jpg\",\n"
            + "      \"is_default_image\":false\n"
            + "    },\n"
            + "    \"has_email\":true,\n"
            + "    \"email_needs_agreement\":false,\n"
            + "    \"is_email_valid\":true,\n"
            + "    \"is_email_verified\":true,\n"
            + "    \"email\":\"bruni@kakao.com\"\n"
            + "  }\n"
            + "}";
    private static final String ERROR_RESPONSE = "{\n"
            + "    \"code\": -401,\n"
            + "    \"error\": \"invalid_request\",\n"
            + "    \"error_description\": \"no valid data in session\"\n"
            + "}";

    @DisplayName("Naver 서버에 요청을 보내 인가코드로부터 유저의 정보를 가져오는데 성공한다.")
    @Test
    void givenMockWebServer_whenRequestNaverUserResoruce_thenSuccess() {
        try (MockWebServer mockOAuthServer = new MockWebServer()) {
            // given
            mockOAuthServer.start();
            setUpMockResponse(mockOAuthServer, TOKEN_RESPONSE);
            setUpMockResponse(mockOAuthServer, USER_RESPONSE);

            var kakaoRequester = new KakaoRequester(new RestTemplate(), new OauthProperties(null, new OauthProperties.Kakao(
                    new OauthProperties.User("clientId", "clientSecret", "redirectUrl"),
                    new OauthProperties.Provider(
                            String.format("http://%s:%s", mockOAuthServer.getHostName(), mockOAuthServer.getPort()),
                            String.format("http://%s:%s", mockOAuthServer.getHostName(), mockOAuthServer.getPort()),
                            ""
                    )
            )));

            // when
            OauthTokenResponse tokenResponse = kakaoRequester.getToken("code");
            UserProfile userProfile = kakaoRequester.getUserProfile(tokenResponse);

            String email = userProfile.getEmail();

            // then
            assertThat(email).isEqualTo("bruni@kakao.com");
            mockOAuthServer.shutdown();
        } catch (IOException ignored) {
        }
    }

    @DisplayName("Naver 서버에 요청을 보낼 때 access token을 가져오는데 실패한다.")
    @Test
    void givenInvalidInfo_whenRequestNaverUserResouce_thenFail() {
        try (MockWebServer mockOAuthServer = new MockWebServer()) {
            // given
            mockOAuthServer.start();
            setUpMockResponse(mockOAuthServer, ERROR_RESPONSE);

            var kakaoRequester = new KakaoRequester(new RestTemplate(), new OauthProperties(null, new OauthProperties.Kakao(
                    new OauthProperties.User("clientId", "clientSecret", "redirectUrl"),
                    new OauthProperties.Provider(
                            String.format("http://%s:%s", mockOAuthServer.getHostName(), mockOAuthServer.getPort()),
                            String.format("http://%s:%s", mockOAuthServer.getHostName(), mockOAuthServer.getPort()),
                            ""
                    )
            )));

            // when & then
            assertThatThrownBy(() -> kakaoRequester.getToken("code"))
                    .isInstanceOf(InternalServerException.class)
                    .extracting("errorCode").isEqualTo(ErrorCode.OAUTH_FAIL_REQUEST_TOKEN);

            mockOAuthServer.shutdown();
        } catch (IOException ignored) {
        }
    }

    private void setUpMockResponse(MockWebServer mockOAuthServer, String mockResponse) {
        mockOAuthServer.enqueue(new MockResponse()
                .setBody(mockResponse)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
    }
}
