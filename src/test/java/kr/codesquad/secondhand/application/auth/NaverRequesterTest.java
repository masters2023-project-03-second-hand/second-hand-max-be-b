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

public class NaverRequesterTest {

    private static final String TOKEN_RESPONSE = "{\n"
            + "    \"access_token\": \"AAAAQosjWDJieBiQZc3to9YQp6HDLvrmyKC+6+iZ3gq7qrkqf50ljZC+Lgoqrg\",\n"
            + "    \"refresh_token\": \"c8ceMEJisO4Se7uGisHoX0f5JEii7JnipglQipkOn5Zp3tyP7dHQoP0zNKHUq2gY\",\n"
            + "    \"token_type\": \"bearer\",\n"
            + "    \"expires_in\": \"3600\"\n"
            + "}";
    private static final String USER_RESPONSE = "{\n"
            + "  \"resultcode\": \"00\",\n"
            + "  \"message\": \"success\",\n"
            + "  \"response\": {\n"
            + "    \"email\": \"openapi@naver.com\",\n"
            + "    \"nickname\": \"OpenAPI\",\n"
            + "    \"profile_image\": \"https://ssl.pstatic.net/static/pwe/address/nodata_33x33.gif\",\n"
            + "    \"age\": \"40-49\",\n"
            + "    \"gender\": \"F\",\n"
            + "    \"id\": \"32742776\",\n"
            + "    \"name\": \"오픈 API\",\n"
            + "    \"birthday\": \"10-01\"\n"
            + "  }\n"
            + "}";
    private static final String ERROR_RESPONSE = "{\n"
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

            var naverRequester = new NaverRequester(new RestTemplate(), new OauthProperties(new OauthProperties.Naver(
                    new OauthProperties.User("clientId", "clientSecret", "redirectUrl"),
                    new OauthProperties.Provider(
                            String.format("http://%s:%s", mockOAuthServer.getHostName(), mockOAuthServer.getPort()),
                            String.format("http://%s:%s", mockOAuthServer.getHostName(), mockOAuthServer.getPort()),
                            ""
                    )
            ), null), "default-profile-image");

            // when
            OauthTokenResponse tokenResponse = naverRequester.getToken("code");
            UserProfile userProfile = naverRequester.getUserProfile(tokenResponse);

            String email = userProfile.getEmail();

            // then
            assertThat(email).isEqualTo("openapi@naver.com");
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

            var naverRequester = new NaverRequester(new RestTemplate(), new OauthProperties(new OauthProperties.Naver(
                    new OauthProperties.User("clientId", "clientSecret", "redirectUrl"),
                    new OauthProperties.Provider(
                            String.format("http://%s:%s", mockOAuthServer.getHostName(), mockOAuthServer.getPort()),
                            String.format("http://%s:%s", mockOAuthServer.getHostName(), mockOAuthServer.getPort()),
                            ""
                    )
            ), null), "default-profile-image");

            // when & then
            assertThatThrownBy(() -> naverRequester.getToken("code"))
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
