package kr.codesquad.secondhand.infrastructure.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import kr.codesquad.secondhand.exception.BadRequestException;
import kr.codesquad.secondhand.exception.ErrorCode;
import kr.codesquad.secondhand.infrastructure.properties.JwtProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class JwtProviderTest {

    private final String secretKey = "2901ujr9021urf0u902hf021y90fh9c210hg093hg091h3g90h30gh901hg09h01";
    private final JwtProvider jwtProvider = new JwtProvider(new JwtProperties(secretKey, 10000, 100000));

    @DisplayName("회원의 PK가 payload로 주어지면 액세스 토큰이 생성되는데 성공한다.")
    @Test
    void whenCreateAccessToken_thenSuccess() {
        // given

        // when
        String accessToken = jwtProvider.createAccessToken(1L);

        // then
        assertThat(accessToken).isNotBlank();
    }

    @DisplayName("유효하지 않은 토큰이 주어지면 토큰을 검증할 때 예외가 던져진다.")
    @Test
    void givenInvalidToken_thenThrowsException() {
        // given
        String invalidToken = "asdf.asdf.asdf";

        // when & then
        assertThatThrownBy(() -> jwtProvider.validateToken(invalidToken))
                .isInstanceOf(BadRequestException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.INVALID_TOKEN);
    }

    @DisplayName("만료된 토큰이 주어지면 토큰을 검증할 때 예외가 던져진다.")
    @Test
    void givenExpiredToken_thenThrowsException() {
        // given
        Date now = new Date();
        String expiredToken = Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() - 1))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();

        // when & then
        assertThatThrownBy(() -> jwtProvider.validateToken(expiredToken))
                .isInstanceOf(BadRequestException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.EXPIRED_TOKEN);
    }
}
