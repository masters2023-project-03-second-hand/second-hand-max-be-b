package kr.codesquad.secondhand.application.redis;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void set(String key, String value, long expiration) {
        redisTemplate.opsForValue().set(key, value, expiration, TimeUnit.MILLISECONDS);
    }
}
