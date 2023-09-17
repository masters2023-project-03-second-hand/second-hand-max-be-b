package kr.codesquad.secondhand.application.chat;

import java.util.List;
import kr.codesquad.secondhand.application.item.PagingUtils;
import kr.codesquad.secondhand.presentation.dto.CustomSlice;
import kr.codesquad.secondhand.presentation.dto.chat.ChatRoomResponse;
import kr.codesquad.secondhand.repository.chat.querydsl.ChatPaginationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChatRoomService {

    private final ChatPaginationRepository chatPaginationRepository;

    public CustomSlice<ChatRoomResponse> read(Long chatRoomId, int pageSize, Long memberId) {
        Slice<ChatRoomResponse> response =
                chatPaginationRepository.findByMemberId(memberId, chatRoomId, pageSize);
        List<ChatRoomResponse> content = response.getContent();

        Long nextCursor = PagingUtils.setNextCursorForChatRoom(content, response.hasNext());

        return new CustomSlice<>(content, nextCursor, response.hasNext());
    }
}
