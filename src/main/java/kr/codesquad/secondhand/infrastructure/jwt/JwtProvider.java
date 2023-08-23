package kr.codesquad.secondhand.infrastructure.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import kr.codesquad.secondhand.exception.ErrorCode;
import kr.codesquad.secondhand.exception.UnAuthorizedException;
import kr.codesquad.secondhand.infrastructure.properties.JwtProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;


@Component
public class JwtProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpirationTime;

    public JwtProvider(JwtProperties jwtProperties) {
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationTime = jwtProperties.getAccessTokenExpirationTime();
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
}
