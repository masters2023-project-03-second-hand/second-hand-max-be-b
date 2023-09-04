package kr.codesquad.secondhand.infrastructure;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@SpringBootTest
public class RedisTemplateTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @DisplayName("Redis가 정상적으로 연결된다.")
    @Test
    void given_when_then() {
        // given
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        String key = "accessToken";

        // when
        valueOperations.set(key, "logout");

        // then
        String value = String.valueOf(valueOperations.get(key));
        assertThat(value).isEqualTo("logout");
    }
}
