package kr.codesquad.secondhand.presentation.dto.chat;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponse {

    private Long chatRoomId;
    private String thumbnailUrl;
    private String chatPartnerName;
    private String chatPartnerProfile;
    private LocalDateTime lastSendTime;
    private String lastSendMessage;
    private Long newMessageCount;
}
