package kr.codesquad.secondhand.config;

import kr.codesquad.secondhand.infrastructure.OauthProperties;
import kr.codesquad.secondhand.infrastructure.OauthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
@EnableConfigurationProperties(OauthProperties.class)
public class OauthConfig {

    private final OauthProperties properties;

    @Bean
    public OauthProvider oauthProvider() {
        return OauthProvider.from(properties);
    }
}
