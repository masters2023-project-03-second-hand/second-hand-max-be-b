package kr.codesquad.secondhand.presentation;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import kr.codesquad.secondhand.application.chat.ChatLogService;
import kr.codesquad.secondhand.application.chat.ChatRoomService;
import kr.codesquad.secondhand.presentation.dto.ApiResponse;
import kr.codesquad.secondhand.presentation.dto.CustomSlice;
import kr.codesquad.secondhand.presentation.dto.chat.ChatLogResponse;
import kr.codesquad.secondhand.presentation.dto.chat.ChatRequest;
import kr.codesquad.secondhand.presentation.dto.chat.ChatRoomResponse;
import kr.codesquad.secondhand.presentation.support.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class ChatController {

    private final Map<DeferredResult<ApiResponse<ChatLogResponse>>, Long> chatRequests = new ConcurrentHashMap<>();
    private final Map<DeferredResult<ApiResponse<CustomSlice<ChatRoomResponse>>>, Long> chatRoomRequests = new ConcurrentHashMap<>();

    private final ChatLogService chatLogService;
    private final ChatRoomService chatRoomService;

    @GetMapping("/chats/{chatRoomId}")
    public DeferredResult<ApiResponse<ChatLogResponse>> readAll(
            @PathVariable Long chatRoomId,
            @RequestParam(required = false, defaultValue = "0") long messageIndex,
            @Auth Long memberId) {
        DeferredResult<ApiResponse<ChatLogResponse>> deferredResult =
                new DeferredResult<>(10000L, new ApiResponse<>(HttpStatus.OK.value(), List.of()));
        chatRequests.put(deferredResult, messageIndex);

        deferredResult.onCompletion(() -> chatRequests.remove(deferredResult));

        ChatLogResponse messages = chatLogService.getMessages(chatRoomId, messageIndex, memberId);
        if (!messages.getChat().isEmpty()) {
            deferredResult.setResult(new ApiResponse<>(HttpStatus.OK.value(), messages));
        }

        return deferredResult;
    }

    @GetMapping("/chats")
    public DeferredResult<ApiResponse<CustomSlice<ChatRoomResponse>>> readList(
            @PageableDefault Pageable pageable,
            @RequestParam(required = false) Long messageId,
            @Auth Long memberId) {
        DeferredResult<ApiResponse<CustomSlice<ChatRoomResponse>>> deferredResult =
                new DeferredResult<>(10000L, new ApiResponse<>(HttpStatus.OK.value(), List.of()));
        chatRoomRequests.put(deferredResult, messageId);

        deferredResult.onCompletion(() -> chatRoomRequests.remove(deferredResult));

        if (chatRoomService.existsMessageAfterMessageId(messageId)) {
            CustomSlice<ChatRoomResponse> chatRooms = chatRoomService.read(memberId, pageable);
            deferredResult.setResult(new ApiResponse<>(HttpStatus.OK.value(), chatRooms));
        }

        return deferredResult;
    }

    @PostMapping("/chats/{chatRoomId}")
    public ApiResponse<Void> sendMessage(@RequestBody ChatRequest request,
                                         @PathVariable Long chatRoomId,
                                         @Auth Long senderId) {
        chatLogService.sendMessage(request.getMessage(), chatRoomId, senderId);

        for (var entry : chatRequests.entrySet()) {
            ChatLogResponse messages = chatLogService.getMessages(chatRoomId, entry.getValue(), senderId);
            entry.getKey().setResult(new ApiResponse<>(HttpStatus.OK.value(), messages));
        }
        return new ApiResponse<>(HttpStatus.OK.value());
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping("/items/{itemId}/chats")
    public ApiResponse<Map<String, Long>> createChatRoom(@PathVariable Long itemId, @Auth Long senderId) {
        Long chatRoomId = chatRoomService.createChatRoom(itemId, senderId);
        return new ApiResponse<>(HttpStatus.CREATED.value(), Map.of("chatRoomId", chatRoomId));
    }
}
