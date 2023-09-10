package kr.codesquad.secondhand;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class RedisTemplateCreator {

    private static RedisConnectionFactory getRedisConnectionFactory() {
        return new LettuceConnectionFactory("localhost", 6379);
    }

    public static RedisTemplate<String, Object> getRedisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(getRedisConnectionFactory());
        return redisTemplate;
    }
}
