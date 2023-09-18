package kr.codesquad.secondhand.application.chat.event;

import kr.codesquad.secondhand.repository.chat.ChatLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class ChatReadEventListener {

    private final ChatLogRepository chatLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void changeReadStatusOfChatLog(ChatReadEvent event) {
        chatLogRepository.updateIsReadStatus(event.getChatRoomId());
    }
}
