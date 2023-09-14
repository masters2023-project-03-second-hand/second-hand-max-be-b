package kr.codesquad.secondhand.presentation.dto.chat;

import kr.codesquad.secondhand.domain.chat.ChatLog;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SimpleChatLog {

    private Long messageIndex;
    private boolean isMe;
    private String message;

    public static SimpleChatLog from(Long messageIndex, ChatLog chatLog) {
        return new SimpleChatLog(messageIndex, chatLog.isSender(), chatLog.getMessage());
    }
}
