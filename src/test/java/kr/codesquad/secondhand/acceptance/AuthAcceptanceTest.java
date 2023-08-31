package kr.codesquad.secondhand.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;

import io.restassured.RestAssured;
import java.util.Map;
import kr.codesquad.secondhand.domain.member.UserProfile;
import kr.codesquad.secondhand.presentation.dto.OauthTokenResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class AuthAcceptanceTest extends AcceptanceTestSupport {

    @DisplayName("로그인할 때")
    @Nested
    class Login {

        @DisplayName("로그인 정보가 일치하면 로그인에 성공한다.")
        @Test
        void givenLoginData_whenLogin_thenSuccess() {
            // given
            given(naverRequester.getToken(anyString()))
                    .willReturn(new OauthTokenResponse("accessToken", "email", "bearer"));
            given(naverRequester.getUserProfile(any(OauthTokenResponse.class)))
                    .willReturn(new UserProfile("23Yong@secondhand.com", "profileUrl"));

            signup();

            var request = RestAssured
                    .given().log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .queryParam("code", "code")
                    .queryParam("state", "state")
                    .body(Map.of("loginId", "23yong"));

            // when
            var response = request
                    .post("/api/auth/naver/login")
                    .then().log().all()
                    .extract();

            // then
            assertAll(
                    () -> assertThat(response.statusCode()).isEqualTo(200),
                    () -> assertThat(response.jsonPath().getString("data.jwt.accessToken")).isNotNull(),
                    () -> assertThat(response.jsonPath().getString("data.jwt.refreshToken")).isNotNull(),
                    () -> assertThat(response.jsonPath().getString("data.user.loginId")).isNotNull(),
                    () -> assertThat(response.jsonPath().getString("data.user.profileUrl")).isNotNull()
            );
        }

        @DisplayName("인가코드가 주어지지 않으면 400 응답이 온다.")
        @Test
        void givenLoginData_whenLogin_thenResponse400() {
            // given
            var request = RestAssured
                    .given().log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .queryParam("state", "state")
                    .body(Map.of("loginId", "23yong"));

            // when
            var response = request
                    .post("/api/auth/naver/login")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(400);
        }

        @DisplayName("일치하지 않는 로그인 데이터가 주어지면 401 응답이 온다.")
        @Test
        void givenInvalidLoginData_whenLogin_thenResponse401() {
            // given
            given(naverRequester.getToken(anyString()))
                    .willReturn(new OauthTokenResponse("accessToken", "email", "bearer"));
            given(naverRequester.getUserProfile(any(OauthTokenResponse.class)))
                    .willReturn(new UserProfile("23Yong@secondhand.com", "profileUrl"));

            signup();

            var request = RestAssured
                    .given().log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .queryParam("code", "code")
                    .queryParam("state", "state")
                    .body(Map.of("loginId", "joy"));

            // when
            var response = request
                    .post("/api/auth/naver/login")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(401);
        }
    }
}
