package kr.codesquad.secondhand.presentation.filter;

import java.io.IOException;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import kr.codesquad.secondhand.exception.ErrorCode;
import kr.codesquad.secondhand.exception.UnAuthorizedException;
import kr.codesquad.secondhand.infrastructure.jwt.JwtExtractor;
import kr.codesquad.secondhand.infrastructure.jwt.JwtProvider;
import kr.codesquad.secondhand.presentation.support.AuthenticationContext;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtFilter extends OncePerRequestFilter {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final List<String> excludeUrlPatterns =
            List.of("/api/auth/**/login", "/api/auth/**/signup", "/api/auth/token", "/api/categories");
    private final List<String> excludeGetUrlPatterns =
            List.of("/api/regions/*", "/api/items/*");

    private final JwtProvider jwtProvider;
    private final AuthenticationContext authenticationContext;

    public JwtFilter(JwtProvider jwtProvider, AuthenticationContext authenticationContext) {
        this.jwtProvider = jwtProvider;
        this.authenticationContext = authenticationContext;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        HttpMethod method = HttpMethod.resolve(request.getMethod());
        if (method == HttpMethod.GET && excludeGetUrlPatterns.contains(request.getRequestURI())) {
            return true;
        }

        return excludeUrlPatterns.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, request.getRequestURI()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (CorsUtils.isPreFlightRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = JwtExtractor.extract(request)
                .orElseThrow(() -> new UnAuthorizedException(ErrorCode.INVALID_AUTH_HEADER));
        jwtProvider.validateBlackToken(token);
        jwtProvider.validateToken(token);
        authenticationContext.setMemberId(jwtProvider.extractClaims(token));

        filterChain.doFilter(request, response);
    }
}
