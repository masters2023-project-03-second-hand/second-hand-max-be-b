package kr.codesquad.secondhand;

import org.springframework.context.annotation.Profile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Profile("test")
@Testcontainers
public abstract class TestContainer {

    static final GenericContainer redis = new GenericContainer(DockerImageName.parse("redis"))
            .withExposedPorts(6379);

    static final MySQLContainer mySQLContainer = new MySQLContainer(DockerImageName.parse("mysql:8.0.34"))
            .withDatabaseName("second_hand")
            .withUsername("root")
            .withPassword("root");

    static {
        redis.start();
        mySQLContainer.start();
    }

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("redis.host", redis::getHost);
        registry.add("redis.port", () -> redis.getMappedPort(6379));
    }
}
