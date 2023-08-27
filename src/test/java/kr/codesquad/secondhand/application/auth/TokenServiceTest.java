package kr.codesquad.secondhand.application.auth;

import kr.codesquad.secondhand.SupportRepository;
import kr.codesquad.secondhand.TokenCreator;
import kr.codesquad.secondhand.application.ApplicationTest;
import kr.codesquad.secondhand.domain.token.RefreshToken;
import kr.codesquad.secondhand.exception.ErrorCode;
import kr.codesquad.secondhand.exception.UnAuthorizedException;
import kr.codesquad.secondhand.infrastructure.jwt.JwtProvider;
import kr.codesquad.secondhand.presentation.dto.token.AccessTokenResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ApplicationTest
class TokenServiceTest {

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private SupportRepository supportRepository;

    @Autowired
    private TokenService tokenService;

    @DisplayName("리프레시 토큰을 통해 액세스 토큰을 갱신하는데 성공한다.")
    @Test
    void givenRefreshToken_whenRenewAccessToken_thenSuccess() {
        // given
        String refreshToken = jwtProvider.createRefreshToken(1L);
        supportRepository.save(RefreshToken.builder()
                .memberId(1L)
                .token(refreshToken)
                .build());

        // when
        AccessTokenResponse result = tokenService.renewAccessToken(refreshToken);

        // then
        assertThat(result.getAccessToken()).isNotBlank();
    }

    @DisplayName("만료된 토큰이 주어지면 토큰을 갱신할 때 에외를 던진다.")
    @Test
    void givenExpiredToken_whenRenewAccessToken_thenThrowsException() {
        // given
        String expiredToken = TokenCreator.createExpiredToken(1L);

        // when & then
        assertThatThrownBy(() -> tokenService.renewAccessToken(expiredToken))
                .isInstanceOf(UnAuthorizedException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.EXPIRED_TOKEN);
    }

    @DisplayName("존재하지 않는 리프레시 토큰이 주어지면 토큰을 갱신할 때 예외를 던진다.")
    @Test
    void givenNotExistsRefreshToken_whenRenewAccessToken_thenThrowsException() {
        // given
        String token = TokenCreator.createToken(1L);

        // when & then
        assertThatThrownBy(() -> tokenService.renewAccessToken(token))
                .isInstanceOf(UnAuthorizedException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.INVALID_TOKEN);
    }
}
