package kr.codesquad.secondhand.infrastructure.jwt;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

public final class JwtExtractor {

    public static final String BEARER = "bearer";

    public static Optional<String> extract(HttpServletRequest request) {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (!StringUtils.hasText(header) || !header.toLowerCase().startsWith(BEARER)) {
            return Optional.empty();
        }

        return Optional.of(header.split(" ")[1]);
    }

    private JwtExtractor() {}
}
