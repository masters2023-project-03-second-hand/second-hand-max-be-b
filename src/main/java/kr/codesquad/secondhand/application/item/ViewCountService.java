package kr.codesquad.secondhand.application.item;

import java.time.Duration;
import java.util.List;
import kr.codesquad.secondhand.application.redis.RedisLockRepository;
import kr.codesquad.secondhand.application.redis.ViewCountRedisService;
import kr.codesquad.secondhand.repository.item.ItemRepository;
import kr.codesquad.secondhand.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ViewCountService {

    private static final int INITIAL_VIEW_COUNT = 1;

    private final ItemRepository itemRepository;
    private final ViewCountRedisService redisService;
    private final RedisLockRepository redisLockRepository;

    public void increaseViewCount(Long itemId) {
        String viewCountKey = RedisUtil.createItemViewCountCacheKey(itemId);

        // locking - spinlock
        while (!redisLockRepository.lock("lock::" + viewCountKey)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }

        try {
            if (redisService.hasKey(viewCountKey)) {
                redisService.increase(viewCountKey);
                return;
            }

            redisService.set(viewCountKey, INITIAL_VIEW_COUNT, Duration.ofSeconds(100).toMillis());
        } finally {
            redisLockRepository.unlock("lock::" + viewCountKey);
        }
    }

    @Async("viewCountExecutor")
    @Scheduled(fixedDelay = 5000L)
    @Transactional
    public void applyViewCountToRDB() {
        List<String> itemViewCountKeys = redisService.getKeysOrderByExpiration(
                RedisUtil.getProductViewCountCacheKeyPattern());

        if (itemViewCountKeys.isEmpty()) {
            return;
        }
        itemViewCountKeys.forEach(key -> {
            int viewCount = redisService.getAndDelete(key);
            itemRepository.findByIdWithPessimisticLock(extractItemId(key))
                    .ifPresent(item -> item.addViewCount(viewCount));
        });
    }

    private Long extractItemId(String key) {
        return Long.parseLong(key.split(RedisUtil.getKeyDelimiter())[1]);
    }
}
