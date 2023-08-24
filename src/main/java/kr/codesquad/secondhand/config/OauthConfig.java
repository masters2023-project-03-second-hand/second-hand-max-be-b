package kr.codesquad.secondhand.config;

import kr.codesquad.secondhand.infrastructure.OauthProperty;
import kr.codesquad.secondhand.infrastructure.OauthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Configuration
@EnableConfigurationProperties(OauthProperty.class)
public class OauthConfig {

    private final OauthProperty property;


    @Bean
    public OauthProvider oauthProvider() {
       return OauthProvider.from(property);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
