package kr.codesquad.secondhand.application.chat;

import kr.codesquad.secondhand.application.chat.event.ChatReadEvent;
import kr.codesquad.secondhand.domain.chat.ChatLog;
import kr.codesquad.secondhand.domain.chat.ChatRoom;
import kr.codesquad.secondhand.domain.item.Item;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.exception.ErrorCode;
import kr.codesquad.secondhand.exception.NotFoundException;
import kr.codesquad.secondhand.presentation.dto.chat.ChatLogResponse;
import kr.codesquad.secondhand.presentation.dto.chat.SimpleChatLog;
import kr.codesquad.secondhand.presentation.dto.item.ItemSimpleResponse;
import kr.codesquad.secondhand.repository.chat.ChatLogRepository;
import kr.codesquad.secondhand.repository.chat.ChatRoomRepository;
import kr.codesquad.secondhand.repository.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChatLogService {

    private final ApplicationEventPublisher eventPublisher;
    private final ChatLogRepository chatLogRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ItemRepository itemRepository;

    public ChatLogResponse getMessages(Long chatRoomId, long messageId, Long memberId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND));
        Item item = itemRepository.findById(chatRoom.getItem().getId())
                .orElseThrow(() -> NotFoundException.itemNotFound(ErrorCode.NOT_FOUND, chatRoom.getItem().getId()));

        Member receiver = chatRoom.getSeller();

        List<ChatLog> chatLogs = chatLogRepository.findAllByChatRoom_IdAndIdIsGreaterThan(chatRoomId, messageId);
        List<SimpleChatLog> chatLogsResponse = chatLogs.stream()
                .map(chatLog -> SimpleChatLog.of(chatLog, chatLog.getSenderId().equals(memberId)))
                .collect(Collectors.toList());

        eventPublisher.publishEvent(new ChatReadEvent(chatRoomId));

        Long lastMessageId = chatLogs.isEmpty() ? messageId : chatLogs.get(chatLogs.size() - 1).getId();
        return new ChatLogResponse(receiver.getLoginId(), ItemSimpleResponse.from(item), chatLogsResponse, lastMessageId);
    }

    @Transactional
    public void sendMessage(String message, Long chatRoomId, Long senderId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND));

        ChatLog chatLog = ChatLog.of(chatRoom, message, senderId);
        chatLogRepository.save(chatLog);

        chatRoom.setLastSendMessage(message);
        chatRoom.changeLastSendTime();
        // TODO: 알람 보내기
    }
}
