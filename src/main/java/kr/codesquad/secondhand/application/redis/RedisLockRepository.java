package kr.codesquad.secondhand.application.redis;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedisLockRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public Boolean lock(String key) {
        return redisTemplate
                .opsForValue()
                .setIfAbsent(key, "lock", Duration.of(3000L, ChronoUnit.SECONDS));
    }

    public Boolean unlock(String key) {
        return redisTemplate.delete(key);
    }
}
