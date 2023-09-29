package kr.codesquad.secondhand.application.redis;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ViewCountRedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public void set(String key, Integer value, long expiration) {
        redisTemplate.opsForValue().set(key, String.valueOf(value), expiration, TimeUnit.MILLISECONDS);
    }

    public void set(String key, Integer value) {
        redisTemplate.opsForValue().set(key, String.valueOf(value));
    }

    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public void increase(String key) {
        redisTemplate.opsForValue().increment(key);
    }

    public Integer getAndDelete(String key) {
        String deletedData = Optional.ofNullable(redisTemplate.opsForValue().getAndDelete(key))
                .orElse("0");
        return Integer.parseInt(deletedData);
    }

    public List<String> getKeysOrderByExpiration(String keyPattern) {
        ScanOptions scanOptions = ScanOptions.scanOptions()
                .match(keyPattern)
                .count(100)
                .build();

        Map<String, Long> keyExpiredMap = collectKeysAndExpiredTime(scanOptions);

        return sortByExpiredTime(keyExpiredMap);
    }

    private Map<String, Long> collectKeysAndExpiredTime(ScanOptions scanOptions) {
        Cursor<byte[]> keys = redisTemplate.getConnectionFactory().getConnection().scan(scanOptions);

        Map<String, Long> keyExpiredMap = new HashMap<>();
        while (keys.hasNext()) {
            String key = new String(keys.next());
            Long expiredTime = redisTemplate.getExpire(key, TimeUnit.SECONDS);

            if (expiredTime != null) {
                keyExpiredMap.put(key, expiredTime);
            }
        }
        return keyExpiredMap;
    }

    private List<String> sortByExpiredTime(Map<String, Long> keyExpiredMap) {
        List<String> sortedKeys = new ArrayList<>(keyExpiredMap.keySet());
        sortedKeys.sort(Comparator.comparingLong(keyExpiredMap::get));

        return sortedKeys;
    }
}
