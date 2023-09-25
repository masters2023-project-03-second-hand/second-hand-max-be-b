package kr.codesquad.secondhand.config;

import java.util.List;
import kr.codesquad.secondhand.presentation.interceptor.RegionNotLoginInterceptor;
import kr.codesquad.secondhand.presentation.support.AuthArgumentResolver;
import kr.codesquad.secondhand.presentation.support.NotNullParamArgumentResolver;
import kr.codesquad.secondhand.presentation.support.converter.IsWishRequestConverter;
import kr.codesquad.secondhand.presentation.support.converter.OAuthProviderConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthArgumentResolver authArgumentResolver;
    private final NotNullParamArgumentResolver notNullParamArgumentResolver;
    private final IsWishRequestConverter isWishRequestConverter;
    private final OAuthProviderConverter oAuthProviderConverter;
    private final RegionNotLoginInterceptor regionNotLoginInterceptor;

    @Value("${custom.front-local-url}")
    private String frontLocalUrl;
    @Value("${custom.front-server-url}")
    private String frontServerUrl;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authArgumentResolver);
        resolvers.add(notNullParamArgumentResolver);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(frontLocalUrl, frontServerUrl)
                .allowedMethods("HEAD", "OPTIONS", "GET", "POST", "PUT", "DELETE", "PATCH");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(isWishRequestConverter);
        registry.addConverter(oAuthProviderConverter);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(regionNotLoginInterceptor)
                .addPathPatterns("/api/items");
    }
}
