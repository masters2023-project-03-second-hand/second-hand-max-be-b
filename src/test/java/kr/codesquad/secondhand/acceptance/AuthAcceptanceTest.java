package kr.codesquad.secondhand.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import kr.codesquad.secondhand.domain.image.ImageFile;
import kr.codesquad.secondhand.domain.member.UserProfile;
import kr.codesquad.secondhand.presentation.dto.OauthTokenResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class AuthAcceptanceTest extends AcceptanceTestSupport {

    private void mockingOAuth() {
        given(naverRequester.getToken(anyString()))
                .willReturn(new OauthTokenResponse("accessToken", "email", "bearer"));
        given(naverRequester.getUserProfile(any(OauthTokenResponse.class)))
                .willReturn(new UserProfile("23Yong@secondhand.com", "profileUrl"));
    }

    private File createFakeFile() throws IOException {
        return File.createTempFile("test-image", ".png");
    }

    @DisplayName("로그인할 때")
    @Nested
    class Login {

        @DisplayName("로그인 정보가 일치하면 로그인에 성공한다.")
        @Test
        void givenLoginData_whenLogin_thenSuccess() {
            // given
            mockingOAuth();

            signup();

            var request = RestAssured
                    .given().log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .queryParam("code", "code")
                    .queryParam("state", "state")
                    .body(Map.of("loginId", "23yong"));

            // when
            var response = login(request);

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
        void givenNoAuthorizationCode_whenLogin_thenResponse400() {
            // given
            var request = RestAssured
                    .given().log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .queryParam("state", "state")
                    .body(Map.of("loginId", "23yong"));

            // when
            var response = login(request);

            // then
            assertThat(response.statusCode()).isEqualTo(400);
        }

        @DisplayName("일치하지 않는 로그인 데이터가 주어지면 401 응답이 온다.")
        @Test
        void givenInvalidLoginData_whenLogin_thenResponse401() {
            // given
            mockingOAuth();

            signup();

            var request = RestAssured
                    .given().log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .queryParam("code", "code")
                    .queryParam("state", "state")
                    .body(Map.of("loginId", "joy"));

            // when
            var response = login(request);

            // then
            assertThat(response.statusCode()).isEqualTo(401);
        }

        private ExtractableResponse<Response> login(RequestSpecification request) {
            return request
                    .post("/api/auth/naver/login")
                    .then().log().all()
                    .extract();
        }
    }

    @DisplayName("회원가입할 때")
    @Nested
    class Signup {

        @DisplayName("올바른 회원가입 정보가 주어지면 회원가입에 성공한다.")
        @Test
        void givenSignupData_whenSignup_thenSuccess() throws IOException {
            // given
            mockingOAuth();
            given(s3Uploader.uploadImageFile(any(ImageFile.class))).willReturn("profileUrl");

            var request = RestAssured
                    .given().log().all()
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                    .queryParam("code", "code")
                    .queryParam("state", "state")
                    .multiPart("request", Map.of("loginId", "bruni", "addressName", "범박동"),
                            MediaType.APPLICATION_JSON_VALUE)
                    .multiPart("profile", createFakeFile(),
                            MediaType.IMAGE_PNG_VALUE);

            // when
            var response = request
                    .when()
                    .post("/api/auth/naver/signup")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(201);
        }

        @DisplayName("인가코드가 주어지지 않으면 400응답을 한다.")
        @Test
        void givenNoAuthorizationCode_whenSignup_thenResponse400() throws IOException {
            // given
            var request = RestAssured
                    .given().log().all()
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                    .queryParam("state", "state")
                    .multiPart("request",
                            Map.of("loginId", "bruni", "addressName", "범박동"),
                            MediaType.APPLICATION_JSON_VALUE)
                    .multiPart("profile",
                            createFakeFile(),
                            MediaType.IMAGE_PNG_VALUE);

            // when
            var response = request
                    .when()
                    .post("/api/auth/naver/signup")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(400);
        }
    }
}
