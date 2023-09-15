package kr.codesquad.secondhand.presentation.dto.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRoomResponse {

    private Long chatRoomId;
    private String thumbnailUrl;
    @JsonProperty(value = "chatPartnerName")
    private String loginId;
    @JsonProperty(value = "chatPartnerProfile")
    private String profileUrl;
    private LocalDateTime lastSendTime;
    private String lastSendMessage;
    private Long newMessageCount;
}
