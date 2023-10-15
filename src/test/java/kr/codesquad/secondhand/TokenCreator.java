package kr.codesquad.secondhand;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import kr.codesquad.secondhand.infrastructure.jwt.JwtProvider;
import kr.codesquad.secondhand.infrastructure.properties.JwtProperties;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public class TokenCreator {

    private static final String secretKey = "2901ujr9021urf0u902hf021y90fh9c210hg093hg091h3g90h30gh901hg09h01";
    private static final JwtProvider jwtProvider = new JwtProvider(
            new JwtProperties(secretKey, 10000, 100000),
            RedisTemplateCreator.getRedisTemplate()
            );

    public static String createToken(Long payload) {
        return jwtProvider.createAccessToken(payload);
    }

    public static String createExpiredToken(Long payload) {
        Date now = new Date();
        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() - 1))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }
}
