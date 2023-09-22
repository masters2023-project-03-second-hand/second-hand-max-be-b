package kr.codesquad.secondhand.config;

import kr.codesquad.secondhand.infrastructure.properties.OauthProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OauthProperties.class)
public class OauthConfig {
}
