package kr.codesquad.secondhand.config;

import java.util.List;
import kr.codesquad.secondhand.presentation.support.AuthArgumentResolver;
import kr.codesquad.secondhand.presentation.support.NotNullParamArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthArgumentResolver authArgumentResolver;
    private final NotNullParamArgumentResolver notNullParamArgumentResolver;

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
}
