package kr.codesquad.secondhand.infrastructure.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@ConfigurationProperties(prefix = "redis")
@Component
public class RedisProperties {

    private int port;
    private String host;
}
