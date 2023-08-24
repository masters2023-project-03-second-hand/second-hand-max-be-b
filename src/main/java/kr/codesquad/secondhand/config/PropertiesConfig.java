package kr.codesquad.secondhand.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

@ConfigurationPropertiesScan("kr.codesquad.secondhand.infrastructure.properties")
@Configuration
public class PropertiesConfig {
}
