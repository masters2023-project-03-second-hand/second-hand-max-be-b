package kr.codesquad.secondhand.presentation;

import kr.codesquad.secondhand.application.chat.ChatLogService;
import kr.codesquad.secondhand.application.chat.ChatRoomService;
import kr.codesquad.secondhand.presentation.dto.ApiResponse;
import kr.codesquad.secondhand.presentation.dto.CustomSlice;
import kr.codesquad.secondhand.presentation.dto.chat.ChatLogResponse;
import kr.codesquad.secondhand.presentation.dto.chat.ChatRequest;
import kr.codesquad.secondhand.presentation.dto.chat.ChatRoomResponse;
import kr.codesquad.secondhand.presentation.support.Auth;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class ChatController {

    private final Map<DeferredResult<ApiResponse<ChatLogResponse>>, ChatData> chatRequests = new ConcurrentHashMap<>();
    private final Map<DeferredResult<ApiResponse<CustomSlice<ChatRoomResponse>>>, Long> chatRoomRequests = new ConcurrentHashMap<>();
    private final ChatLogService chatLogService;
    private final ChatRoomService chatRoomService;

    @GetMapping("/chats/{chatRoomId}")
    public DeferredResult<ApiResponse<ChatLogResponse>> readAll(
            @PathVariable Long chatRoomId,
            @RequestParam(required = false, defaultValue = "0") Long messageId,
            @Auth Long memberId) {
        DeferredResult<ApiResponse<ChatLogResponse>> deferredResult =
                new DeferredResult<>(10000L, new ApiResponse<>(HttpStatus.OK.value(), List.of()));
        chatRequests.put(deferredResult, new ChatData(chatRoomId, messageId, memberId));

        deferredResult.onCompletion(() -> chatRequests.remove(deferredResult));

        ChatLogResponse messages = chatLogService.getMessages(chatRoomId, messageId, memberId);
        if (!messages.getChat().isEmpty()) {
            deferredResult.setResult(new ApiResponse<>(HttpStatus.OK.value(), messages));
        }

        return deferredResult;
    }

    @GetMapping("/chats")
    public DeferredResult<ApiResponse<CustomSlice<ChatRoomResponse>>> readList(
            @PageableDefault Pageable pageable,
            @Auth Long memberId) {
        CustomSlice<ChatRoomResponse> chatRooms = chatRoomService.read(memberId, pageable, null);

        DeferredResult<ApiResponse<CustomSlice<ChatRoomResponse>>> deferredResult =
                new DeferredResult<>(10000L, new ApiResponse<>(HttpStatus.OK.value(), chatRooms));
        chatRoomRequests.put(deferredResult, memberId);

        deferredResult.onCompletion(() -> chatRoomRequests.remove(deferredResult));

        return deferredResult;
    }

    @GetMapping("/items/{itemId}/chats")
    public DeferredResult<ApiResponse<CustomSlice<ChatRoomResponse>>> readListByItem(
            @PageableDefault Pageable pageable,
            @PathVariable Long itemId,
            @Auth Long memberId) {
        CustomSlice<ChatRoomResponse> chatRooms = chatRoomService.read(memberId, pageable, itemId);

        DeferredResult<ApiResponse<CustomSlice<ChatRoomResponse>>> deferredResult =
                new DeferredResult<>(10000L, new ApiResponse<>(HttpStatus.OK.value(), chatRooms));
        chatRoomRequests.put(deferredResult, memberId);

        deferredResult.onCompletion(() -> chatRoomRequests.remove(deferredResult));

        return deferredResult;
    }

    @PostMapping("/chats/{chatRoomId}")
    public ApiResponse<Void> sendMessage(@Valid @RequestBody ChatRequest request,
                                         @PathVariable Long chatRoomId,
                                         @Auth Long senderId) {
        Long receiverId = chatRoomService.getReceiverId(chatRoomId);
        chatLogService.sendMessage(request.getMessage(), chatRoomId, senderId);

        for (var entry : chatRequests.entrySet()) {
            ChatData chatData = entry.getValue();
            if (!chatData.getChatRoomId().equals(chatRoomId)) {
                continue;
            }
            ChatLogResponse messages = chatLogService.getMessages(chatRoomId, chatData.getChatRoomId(), chatData.getTargetMemberId());
            entry.getKey().setResult(new ApiResponse<>(HttpStatus.OK.value(), messages));
        }

        for (var entry : chatRoomRequests.entrySet()) {
            if (!entry.getValue().equals(receiverId)) {
                continue;
            }
            CustomSlice<ChatRoomResponse> chatRooms = chatRoomService.read(senderId, Pageable.ofSize(10), null);
            entry.getKey().setResult(new ApiResponse<>(HttpStatus.OK.value(), chatRooms));
        }

        return new ApiResponse<>(HttpStatus.OK.value());
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping("/items/{itemId}/chats")
    public ApiResponse<Map<String, Long>> createChatRoom(@PathVariable Long itemId, @Auth Long senderId) {
        Long chatRoomId = chatRoomService.createChatRoom(itemId, senderId);
        return new ApiResponse<>(HttpStatus.CREATED.value(), Map.of("chatRoomId", chatRoomId));
    }

    @Getter
    @AllArgsConstructor
    private static class ChatData {

        private Long chatRoomId;
        private Long messageId;
        private Long targetMemberId;
    }
}
