package kr.codesquad.secondhand.application.item.event;

import kr.codesquad.secondhand.domain.item.Item;
import kr.codesquad.secondhand.exception.ErrorCode;
import kr.codesquad.secondhand.exception.NotFoundException;
import kr.codesquad.secondhand.repository.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class ItemEventListener {

    private final ItemRepository itemRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void increaseViewCount(ItemViewEvent event) {
        Item item = itemRepository.findByIdWithPessimisticLock(event.getItemId())
                .orElseThrow(() -> NotFoundException.itemNotFound(ErrorCode.NOT_FOUND, event.getItemId()));
        item.incrementViewCount();
    }
}
