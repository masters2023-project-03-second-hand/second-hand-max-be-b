package kr.codesquad.secondhand.application.chat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChatLogService {

    private final ChatLogRepository chatLogRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ItemRepository itemRepository;

    public ChatLogResponse getMessages(Long chatRoomId, long messageIndex) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND));
        Item item = itemRepository.findById(chatRoom.getItem().getId())
                .orElseThrow(() -> NotFoundException.itemNotFound(ErrorCode.NOT_FOUND, chatRoom.getItem().getId()));
        Member receiver = chatRoom.getReceiver();

        List<ChatLog> chatLogs = chatLogRepository.findAllByChatRoomIdOrderByIdDesc(chatRoomId);
        List<ChatLog> logsAfterIndex = chatLogs.subList((int) messageIndex, chatLogs.size());

        List<SimpleChatLog> chatLogsResponse = LongStream.range(0, logsAfterIndex.size())
                .mapToObj(idx -> SimpleChatLog.from(idx, chatLogs.get((int) idx)))
                .collect(Collectors.toList());

        return new ChatLogResponse(receiver.getLoginId(), ItemSimpleResponse.from(item), chatLogsResponse);
    }
}
