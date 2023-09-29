package kr.codesquad.secondhand.application.redis;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedisLockService {

    private final RedisTemplate<String, Object> redisTemplate;

    public Boolean lock(String key) {
        return redisTemplate
                .opsForValue()
                .setIfAbsent(key, "lock", Duration.ofMillis(3000L));
    }

    public Boolean unlock(String key) {
        return redisTemplate.delete(key);
    }
}
