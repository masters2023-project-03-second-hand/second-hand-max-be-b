package kr.codesquad.secondhand.application.chat;

import java.util.List;
import java.util.Map;
import kr.codesquad.secondhand.domain.chat.ChatLog;
import kr.codesquad.secondhand.domain.chat.ChatRoom;
import kr.codesquad.secondhand.domain.item.Item;
import kr.codesquad.secondhand.exception.ErrorCode;
import kr.codesquad.secondhand.exception.NotFoundException;
import kr.codesquad.secondhand.presentation.dto.CustomSlice;
import kr.codesquad.secondhand.presentation.dto.chat.ChatRoomResponse;
import kr.codesquad.secondhand.repository.chat.ChatLogRepository;
import kr.codesquad.secondhand.repository.chat.ChatRoomRepository;
import kr.codesquad.secondhand.repository.chat.querydsl.ChatCountRepository;
import kr.codesquad.secondhand.repository.chat.querydsl.ChatPaginationRepository;
import kr.codesquad.secondhand.repository.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChatRoomService {

    private static final long DEFAULT_MESSAGE_COUNT = 0L;

    private final ItemRepository itemRepository;
    private final ChatPaginationRepository chatPaginationRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatCountRepository chatCountRepository;
    private final ChatLogRepository chatLogRepository;

    public CustomSlice<ChatRoomResponse> read(Long memberId, Pageable pageable) {
        Slice<ChatRoomResponse> response =
                chatPaginationRepository.findByMemberId(memberId, pageable);

        List<ChatRoomResponse> contents = response.getContent();

        Map<Long, Long> newMessageCounts = chatCountRepository.countNewMessage(memberId);

        contents.forEach(chatRoomResponse -> {
            Long chatRoomId = chatRoomResponse.getChatRoomId();
            Long messageCount = newMessageCounts.getOrDefault(chatRoomId, DEFAULT_MESSAGE_COUNT);
            chatRoomResponse.assignNewMessageCount(messageCount);
        });

        boolean hasNext = response.hasNext();
        Long nextCursor = hasNext ? Long.valueOf(pageable.getPageNumber() + 1) : null;

        ChatLog lastChatLog = chatLogRepository.findFirstByOrderByIdDesc().orElse(null);
        Long lastChatLogId = lastChatLog != null ? lastChatLog.getId() : null;

        return new CustomSlice<>(contents, nextCursor, response.hasNext(), lastChatLogId);
    }

    @Transactional
    public Long createChatRoom(Long itemId, Long senderId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> NotFoundException.itemNotFound(ErrorCode.NOT_FOUND, itemId));
        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.of(senderId, itemId, item.getMember().getId()));
        item.increaseChatCount();
        return chatRoom.getId();
    }

    public boolean existsMessageAfterMessageId(Long messageId) {
        if (messageId == null) {
            return true;
        }
        return chatLogRepository.existsByIdGreaterThan(messageId);
    }
}
