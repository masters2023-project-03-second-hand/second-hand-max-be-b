package kr.codesquad.secondhand.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.codesquad.secondhand.infrastructure.jwt.JwtProvider;
import kr.codesquad.secondhand.presentation.filter.AuthExceptionHandlerFilter;
import kr.codesquad.secondhand.presentation.filter.JwtFilter;
import kr.codesquad.secondhand.presentation.support.AuthenticationContext;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class FilterConfig {

    private final JwtProvider jwtProvider;
    private final AuthenticationContext authenticationContext;
    private final ObjectMapper objectMapper;

    @Bean
    public FilterRegistrationBean<JwtFilter> jwtFilter() {
        FilterRegistrationBean<JwtFilter> jwtFilter = new FilterRegistrationBean<>();
        jwtFilter.setFilter(new JwtFilter(jwtProvider, authenticationContext));
        jwtFilter.addUrlPatterns("/api/*");
        jwtFilter.setOrder(2);
        return jwtFilter;
    }

    @Bean
    public FilterRegistrationBean<AuthExceptionHandlerFilter> authExceptionHandlerFilter() {
        FilterRegistrationBean<AuthExceptionHandlerFilter> authExceptionHandlerFilter = new FilterRegistrationBean<>();
        authExceptionHandlerFilter.setFilter(new AuthExceptionHandlerFilter(objectMapper));
        authExceptionHandlerFilter.addUrlPatterns("/api/*");
        authExceptionHandlerFilter.setOrder(1);
        return authExceptionHandlerFilter;
    }
}
