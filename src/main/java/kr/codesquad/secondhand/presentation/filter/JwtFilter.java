package kr.codesquad.secondhand.presentation.filter;

import kr.codesquad.secondhand.exception.ErrorCode;
import kr.codesquad.secondhand.exception.UnAuthorizedException;
import kr.codesquad.secondhand.infrastructure.jwt.JwtExtractor;
import kr.codesquad.secondhand.infrastructure.jwt.JwtProvider;
import kr.codesquad.secondhand.presentation.support.AuthenticationContext;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JwtFilter extends OncePerRequestFilter {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final List<String> excludeUrlPatterns =
            List.of("/api/auth/**", "/api/categories");
    private final List<String> excludeGetUrlPatterns =
            List.of("/api/regions/**", "/api/items/**");

    private final JwtProvider jwtProvider;
    private final AuthenticationContext authenticationContext;

    public JwtFilter(JwtProvider jwtProvider, AuthenticationContext authenticationContext) {
        this.jwtProvider = jwtProvider;
        this.authenticationContext = authenticationContext;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        HttpMethod method = HttpMethod.resolve(request.getMethod());
        if (method == HttpMethod.GET && isExcludeGetUrl(request.getRequestURI())) {
            extractToken(request).ifPresentOrElse(
                    token -> {
                        jwtProvider.validateToken(token);
                        authenticationContext.setMemberId(jwtProvider.extractClaims(token));
                    },
                    () -> authenticationContext.setMemberId(Map.of("memberId", -1L)));
            return true;
        }

        return excludeUrlPatterns.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, request.getRequestURI()));
    }

    private boolean isExcludeGetUrl(String uri) {
        return excludeGetUrlPatterns.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, uri));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (CorsUtils.isPreFlightRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractToken(request)
                .orElseThrow(() -> new UnAuthorizedException(ErrorCode.INVALID_AUTH_HEADER));
        jwtProvider.validateBlackToken(token);
        jwtProvider.validateToken(token);
        authenticationContext.setMemberId(jwtProvider.extractClaims(token));

        filterChain.doFilter(request, response);
    }

    private Optional<String> extractToken(HttpServletRequest request) {
        return JwtExtractor.extract(request);
    }
}
