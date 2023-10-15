package kr.codesquad.secondhand.application.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import kr.codesquad.secondhand.application.ApplicationTestSupport;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.domain.member.OAuthProvider;
import kr.codesquad.secondhand.domain.member.UserProfile;
import kr.codesquad.secondhand.domain.token.RefreshToken;
import kr.codesquad.secondhand.exception.ErrorCode;
import kr.codesquad.secondhand.exception.UnAuthorizedException;
import kr.codesquad.secondhand.presentation.dto.OauthTokenResponse;
import kr.codesquad.secondhand.presentation.dto.member.LoginRequest;
import kr.codesquad.secondhand.presentation.dto.member.LoginResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("비즈니스 로직 - 인증/인가")
class AuthServiceTest extends ApplicationTestSupport {

    @Autowired
    private AuthService authService;

    @Nested
    class Login {

        @DisplayName("로그인 정보가 주어지면 로그인에 성공해 토큰정보와 유저 정보를 반환한다.")
        @Test
        void givenLoginData_whenLogin_thenSuccess() {
            // given
            mockingOAuthInfo();

            LoginRequest request = new LoginRequest("joy");
            supportRepository.save(Member.builder()
                    .email("joy@naver.com")
                    .loginId("joy")
                    .profileUrl("url")
                    .build());

            // when
            LoginResponse response = authService.login(OAuthProvider.NAVER, request, "code");

            // then
            Optional<RefreshToken> token = supportRepository.findById(RefreshToken.class, 1L);
            assertAll(
                    () -> assertThat(token).isPresent(),
                    () -> assertThat(response.getJwt().getAccessToken()).isNotBlank(),
                    () -> assertThat(response.getJwt().getRefreshToken()).isNotBlank(),
                    () -> assertThat(response.getUser().getLoginId()).isNotBlank(),
                    () -> assertThat(response.getUser().getProfileUrl()).isNotBlank()
            );
        }

        @DisplayName("리프레시 토큰이 존재하는 사용자가 다시 로그인을 시도할 때 존재하는 리프레시 토큰을 삭제하고 새로운 리프레시 토큰을 저장한다.")
        @Test
        void givenLoginDataAndAlreadyHasRefreshToken_whenLogin_thenSuccess() {
            // given
            mockingOAuthInfo();

            LoginRequest request = new LoginRequest("joy");
            supportRepository.save(Member.builder()
                    .email("joy@naver.com")
                    .loginId("joy")
                    .profileUrl("url")
                    .build());
            supportRepository.save(RefreshToken.builder()
                    .memberId(1L)
                    .token("token.token.token")
                    .build());

            // when
            authService.login(OAuthProvider.NAVER, request, "code");

            // then
            assertThat(supportRepository.findById(RefreshToken.class, 1L).get().getToken())
                    .isNotEqualTo("token.token.token");
        }

        @DisplayName("아이디는 존재하지만 해당 아이디의 이메일과 Naver 이메일 정보가 일치하지 않는 로그인 정보가 주어지면 예외를 던진다.")
        @Test
        void givenInvalidLoginData_whenLogin_thenThrowsException() {
            // given
            mockingOAuthInfo();

            LoginRequest request = new LoginRequest("joy");
            supportRepository.save(Member.builder()
                    .email("joooy@naver.com")
                    .loginId("joy")
                    .profileUrl("url")
                    .build());

            // when & then
            assertThatThrownBy(() -> authService.login(OAuthProvider.NAVER, request, "code"))
                    .isInstanceOf(UnAuthorizedException.class)
                    .extracting("errorCode").isEqualTo(ErrorCode.INVALID_LOGIN_DATA);
        }

        @DisplayName("아이디가 존재하지 않아 로그인할 때 예외를 던진다.")
        @Test
        void givenNotExistsLoginId_whenLogin_thenThrowsException() {
            // given
            mockingOAuthInfo();
            LoginRequest request = new LoginRequest("joy");

            // when & then
            assertThatThrownBy(() -> authService.login(OAuthProvider.NAVER, request, "code"))
                    .isInstanceOf(UnAuthorizedException.class)
                    .extracting("errorCode").isEqualTo(ErrorCode.INVALID_LOGIN_DATA);
        }

        private void mockingOAuthInfo() {
            given(naverRequester.getToken(anyString()))
                    .willReturn(new OauthTokenResponse("a.a.a", "scope", "bearer"));
            given(naverRequester.getUserProfile(any(OauthTokenResponse.class)))
                    .willReturn(UserProfile.builder()
                            .email("joy@naver.com")
                            .profileUrl("url")
                            .build());

            given(kakaoRequester.getToken(anyString()))
                    .willReturn(new OauthTokenResponse("a.a.a", "scope", "bearer"));
            given(kakaoRequester.getUserProfile(any(OauthTokenResponse.class)))
                    .willReturn(UserProfile.builder()
                            .email("joy@naver.com")
                            .profileUrl("url")
                            .build());
        }
    }
}
