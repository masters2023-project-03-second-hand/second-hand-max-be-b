package kr.codesquad.secondhand.application.auth;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import kr.codesquad.secondhand.exception.ErrorCode;
import kr.codesquad.secondhand.exception.UnAuthorizedException;
import kr.codesquad.secondhand.infrastructure.jwt.JwtProvider;
import kr.codesquad.secondhand.presentation.dto.token.AccessTokenResponse;
import kr.codesquad.secondhand.repository.token.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TokenService {

    private final TokenRepository tokenRepository;
    private final JwtProvider jwtProvider;
    private final RedisTemplate redisTemplate;

    public AccessTokenResponse renewAccessToken(final String refreshToken) {
        jwtProvider.validateToken(refreshToken);
        Map<String, Object> claims = jwtProvider.extractClaims(refreshToken);
        Long memberId = Long.valueOf(claims.get("memberId").toString());

        if (!tokenRepository.existsById(memberId)) {
            throw new UnAuthorizedException(ErrorCode.INVALID_TOKEN);
        }
        return new AccessTokenResponse(jwtProvider.createAccessToken(memberId));
    }

    @Transactional
    public void logout(HttpServletRequest request, Long memberId) {
        String accessToken = extractJwt(request);
        Long expiration = jwtProvider.getExpiration(accessToken);
        redisTemplate.opsForValue().set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
        tokenRepository.deleteByMemberId(memberId);
    }

    private String extractJwt(HttpServletRequest request) {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (!StringUtils.hasText(header) || !header.toLowerCase().startsWith("bearer")) {
            throw new UnAuthorizedException(ErrorCode.INVALID_AUTH_HEADER);
        }

        return header.split(" ")[1];
    }
}
