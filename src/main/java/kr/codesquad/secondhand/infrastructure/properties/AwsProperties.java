package kr.codesquad.secondhand.infrastructure.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.Map;

@Getter
@ConfigurationProperties("cloud.aws")
public class AwsProperties {

    private final Credentials credentials;
    private final S3 s3;
    private final String region;

    @ConstructorBinding
    public AwsProperties(Credentials credentials, S3 s3, Map<String, String> region) {
        this.credentials = credentials;
        this.s3 = s3;
        this.region = region.get("static");
    }

    @Getter
    @RequiredArgsConstructor
    public static class Credentials {

        private final String accessKey;
        private final String secretKey;
    }

    @Getter
    @RequiredArgsConstructor
    public static class S3 {

        private final String bucket;
    }
}
