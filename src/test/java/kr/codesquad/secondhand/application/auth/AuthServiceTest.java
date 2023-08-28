package kr.codesquad.secondhand.application.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import kr.codesquad.secondhand.SupportRepository;
import kr.codesquad.secondhand.application.ApplicationTest;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.domain.token.RefreshToken;
import kr.codesquad.secondhand.exception.ErrorCode;
import kr.codesquad.secondhand.exception.UnAuthorizedException;
import kr.codesquad.secondhand.presentation.dto.LoginRequest;
import kr.codesquad.secondhand.presentation.dto.LoginResponse;
import kr.codesquad.secondhand.presentation.dto.OauthTokenResponse;
import kr.codesquad.secondhand.presentation.dto.UserProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@ApplicationTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private SupportRepository supportRepository;

    @MockBean
    private NaverRequester naverRequester;

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
            LoginResponse response = authService.login(request, "code");

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
            assertThatThrownBy(() -> authService.login(request, "code"))
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
            assertThatThrownBy(() -> authService.login(request, "code"))
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
        }
    }
}
