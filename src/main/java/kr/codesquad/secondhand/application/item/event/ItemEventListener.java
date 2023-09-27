package kr.codesquad.secondhand.application.item.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class ItemEventListener {

    private final SynchronizedService synchronizedService;

    @TransactionalEventListener
    public synchronized void increaseViewCount(ItemViewEvent event) {
        synchronizedService.increaseViewCount(event.getItemId());
    }
}
