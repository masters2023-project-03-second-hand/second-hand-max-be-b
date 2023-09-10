package kr.codesquad.secondhand.infrastructure.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConfigurationProperties(prefix = "redis")
public class RedisProperties {

    private int port;
    private String host;

    @ConstructorBinding
    public RedisProperties(int port, String host) {
        this.port = port;
        this.host = host;
    }
}
