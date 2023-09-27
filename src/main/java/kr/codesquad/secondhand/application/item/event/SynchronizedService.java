package kr.codesquad.secondhand.application.item.event;

import kr.codesquad.secondhand.domain.item.Item;
import kr.codesquad.secondhand.repository.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class SynchronizedService {

    private final ItemRepository itemRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void increaseViewCount(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow();
        item.incrementViewCount();
    }
}
