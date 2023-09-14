package kr.codesquad.secondhand.presentation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import kr.codesquad.secondhand.application.chat.ChatLogService;
import kr.codesquad.secondhand.presentation.dto.ApiResponse;
import kr.codesquad.secondhand.presentation.dto.chat.ChatLogResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class ChatController {

    private final Map<DeferredResult<ApiResponse<ChatLogResponse>>, Long> chatRequests = new ConcurrentHashMap<>();
    private final ChatLogService chatLogService;

    @GetMapping("/chats/{chatRoomId}")
    public DeferredResult<ApiResponse<ChatLogResponse>> readAll(
            @PathVariable Long chatRoomId,
            @RequestParam(required = false, defaultValue = "0") long messageIndex) {
        DeferredResult<ApiResponse<ChatLogResponse>> deferredResult =
                new DeferredResult<>(1000L, new ApiResponse<>(HttpStatus.OK.value()));
        chatRequests.put(deferredResult, messageIndex);

        deferredResult.onCompletion(() -> chatRequests.remove(deferredResult));

        ChatLogResponse messages = chatLogService.getMessages(chatRoomId, messageIndex);
        if (!messages.getChat().isEmpty()) {
            deferredResult.setResult(new ApiResponse<>(HttpStatus.OK.value(), messages));
        }

        return deferredResult;
    }
}
