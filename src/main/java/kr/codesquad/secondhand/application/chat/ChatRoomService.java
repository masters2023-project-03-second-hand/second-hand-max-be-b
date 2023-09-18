package kr.codesquad.secondhand.application.chat;

import java.util.List;
import kr.codesquad.secondhand.application.item.PagingUtils;
import kr.codesquad.secondhand.domain.chat.ChatRoom;
import kr.codesquad.secondhand.domain.item.Item;
import kr.codesquad.secondhand.exception.ErrorCode;
import kr.codesquad.secondhand.exception.NotFoundException;
import kr.codesquad.secondhand.presentation.dto.CustomSlice;
import kr.codesquad.secondhand.presentation.dto.chat.ChatRoomResponse;
import kr.codesquad.secondhand.repository.chat.ChatRoomRepository;
import kr.codesquad.secondhand.repository.chat.querydsl.ChatPaginationRepository;
import kr.codesquad.secondhand.repository.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChatRoomService {

    private final ItemRepository itemRepository;
    private final ChatPaginationRepository chatPaginationRepository;
    private final ChatRoomRepository chatRoomRepository;

    public CustomSlice<ChatRoomResponse> read(Long chatRoomId, int pageSize, Long memberId) {
        Slice<ChatRoomResponse> response =
                chatPaginationRepository.findByMemberId(memberId, chatRoomId, pageSize);
        List<ChatRoomResponse> content = response.getContent();

        Long nextCursor = PagingUtils.setNextCursorForChatRoom(content, response.hasNext());

        return new CustomSlice<>(content, nextCursor, response.hasNext());
    }

    @Transactional
    public Long createChatRoom(Long itemId, Long senderId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> NotFoundException.itemNotFound(ErrorCode.NOT_FOUND, itemId));
        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.from(senderId, itemId, item.getMember().getId()));
        item.increaseChatCount();
        return chatRoom.getId();
    }
}
