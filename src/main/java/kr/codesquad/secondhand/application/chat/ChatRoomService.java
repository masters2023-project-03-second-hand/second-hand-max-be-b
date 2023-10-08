package kr.codesquad.secondhand.application.chat;

import kr.codesquad.secondhand.domain.chat.ChatRoom;
import kr.codesquad.secondhand.domain.item.Item;
import kr.codesquad.secondhand.exception.ErrorCode;
import kr.codesquad.secondhand.exception.NotFoundException;
import kr.codesquad.secondhand.presentation.dto.CustomSlice;
import kr.codesquad.secondhand.presentation.dto.chat.ChatRoomResponse;
import kr.codesquad.secondhand.repository.chat.ChatRoomRepository;
import kr.codesquad.secondhand.repository.chat.querydsl.ChatCountRepository;
import kr.codesquad.secondhand.repository.chat.querydsl.ChatPaginationRepository;
import kr.codesquad.secondhand.repository.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChatRoomService {

    private static final long DEFAULT_MESSAGE_COUNT = 0L;

    private final ItemRepository itemRepository;
    private final ChatPaginationRepository chatPaginationRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatCountRepository chatCountRepository;

    public CustomSlice<ChatRoomResponse> read(Long memberId, Pageable pageable, Long itemId) {
        Slice<ChatRoomResponse> response = chatPaginationRepository.findByMemberId(memberId, pageable, itemId);

        List<ChatRoomResponse> contents = response.getContent();

        Map<Long, Long> newMessageCounts = chatCountRepository.countNewMessage(memberId);

        contents.forEach(chatRoomResponse -> {
            Long chatRoomId = chatRoomResponse.getChatRoomId();
            Long messageCount = newMessageCounts.getOrDefault(chatRoomId, DEFAULT_MESSAGE_COUNT);
            chatRoomResponse.assignNewMessageCount(messageCount);
        });

        boolean hasNext = response.hasNext();
        Long nextCursor = hasNext ? Long.valueOf(pageable.getPageNumber() + 1) : null;

        return new CustomSlice<>(contents, nextCursor, response.hasNext());
    }

    @Transactional
    public Long createChatRoom(Long itemId, Long senderId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> NotFoundException.itemNotFound(ErrorCode.NOT_FOUND, itemId));
        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.of(senderId, itemId, item.getMember().getId()));
        item.increaseChatCount();
        return chatRoom.getId();
    }

    public Long getReceiverId(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND));
        return chatRoom.getSeller().getId();
    }
}
