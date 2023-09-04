package kr.codesquad.secondhand.infrastructure.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import kr.codesquad.secondhand.exception.ErrorCode;
import kr.codesquad.secondhand.exception.UnAuthorizedException;
import kr.codesquad.secondhand.infrastructure.properties.JwtProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.Map;


@Component
public class JwtProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpirationTime;
    private final long refreshTokenExpirationTime;
    private final RedisTemplate redisTemplate;

    public JwtProvider(JwtProperties jwtProperties, RedisTemplate redisTemplate) {
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationTime = jwtProperties.getAccessTokenExpirationTime();
        this.refreshTokenExpirationTime = jwtProperties.getRefreshTokenExpirationTime();
        this.redisTemplate = redisTemplate;
    }

    public String createAccessToken(Long memberId) {
        Date now = new Date();
        Date accessTokenExpiration = new Date(now.getTime() + accessTokenExpirationTime);

        return Jwts.builder()
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .setIssuedAt(now)
                .setExpiration(accessTokenExpiration)
                .setClaims(Map.of("memberId", memberId))
                .compact();
    }

    public String createRefreshToken(Long memberId) {
        Date now = new Date();
        Date refreshTokenExpiration = new Date(now.getTime() + refreshTokenExpirationTime);

        return Jwts.builder()
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .setIssuedAt(now)
                .setExpiration(refreshTokenExpiration)
                .setClaims(Map.of("memberId", memberId))
                .compact();
    }

    public void validateToken(final String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new UnAuthorizedException(ErrorCode.EXPIRED_TOKEN);
        } catch (JwtException e) {
            throw new UnAuthorizedException(ErrorCode.INVALID_TOKEN);
        }
    }

    public Map<String, Object> extractClaims(final String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Collections.unmodifiableMap(claims);
    }

    public Long getExpiration(String accessToken) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(accessToken)
                .getBody()
                .getExpiration();
        long now = new Date().getTime();
        return expiration.getTime() - now;
    }

    public void validBlackToken(String accessToken) {
        if (redisTemplate.hasKey(accessToken)) {
            throw new UnAuthorizedException(ErrorCode.NOT_LOGIN);
        }
    }
}
